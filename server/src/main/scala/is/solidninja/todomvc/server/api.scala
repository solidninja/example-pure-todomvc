package is
package solidninja
package todomvc
package server

import java.util.UUID

import scala.util.Try

import cats.effect._

import io.circe._
import io.circe.syntax._

import org.http4s._
import org.http4s.dsl._

import is.solidninja.todomvc.protocol._

object ApiModel {
  import io.circe.generic.semiauto._

  case class NewTodo(title: String, completed: Boolean) {
    def withRandomUuid: Todo = Todo(title, completed, UUID.randomUUID())
  }

  implicit val decodeNewTodo: Decoder[NewTodo] = deriveDecoder
}

object API {
  import org.http4s.circe._
  import JsonProtocol._
  import ApiModel._

  private implicit val entityDecoderForNewTodo = jsonOf[IO, NewTodo]
  private implicit val entityDecoderForTodoList = jsonOf[IO, List[Todo]]

  object UuidVar {
    def unapply(str: String): Option[UUID] =
      if (!str.isEmpty) Try(UUID.fromString(str)).toOption else None
  }

  def todoService(db: TodoDatabase) = HttpService[IO] {
    case GET -> Root / "todo" =>
      db.list.flatMap(okJson[List[Todo]])
    case GET -> Root / "todo" / UuidVar(id) =>
      db.find(id).flatMap {
        case Some(todo) => okJson(todo)
        case None => NotFound()
      }
    case req @ POST -> Root / "todo" =>
      req.decode[NewTodo] { todo =>
        // FIXME does not handle duplicate data
        db.save(todo.withRandomUuid).flatMap(okJson[Todo])
      }
    case req @ PUT -> Root / "todo" / UuidVar(id) =>
      req.decode[NewTodo] { updated =>
        db.find(id).flatMap {
          case Some(todo) =>
            db.save(todo.copy(title = updated.title, completed = updated.completed))
              .flatMap(okJson[Todo])
          case None => NotFound()
        }
      }
    case req @ PUT -> Root / "todo" =>
      req.decode[List[Todo]] { todos =>
        db.sync(todos).flatMap(okJson[List[Todo]])
      }
    case DELETE -> Root / "todo" / UuidVar(id) =>
      db.delete(id).flatMap {
        case true => Ok(())
        case _ => NotFound()
      }
  }

  private def okJson[A: Encoder](v: A) = Ok(v.asJson)
}
