package is
package solidninja
package todomvc
package server

import java.util.UUID

import scala.concurrent.ExecutionContext

import cats.effect.{ContextShift, IO, Resource}
import cats.syntax.functor._
import is.solidninja.todomvc.protocol.Todo
import is.solidninja.todomvc.server.ApiModel.NewTodo
import org.scalactic.Equality
import org.scalatest.{FreeSpec, Matchers}

class DoobieTodoDatabaseTest extends FreeSpec with Matchers {

  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global
  implicit val cs: ContextShift[IO] = IO.contextShift(implicitly)

  "A todo database" - {
    "should be able to save and retrieve a todo" in {
      val todo = NewTodo("Write a book", completed = false).withRandomUuid
      val resF = db.use { db =>
        for {
          _ <- db.save(todo)
          retrieved <- db.find(todo.id)
        } yield retrieved
      }

      val retrieved = resF.unsafeRunSync()
      retrieved should contain(todo)
    }

    "should come pre-populated with standard todos" in {
      implicit val todoEquality: Equality[Todo] = (a: Todo, other: Any) =>
        other match {
          case b: Todo => a.title == b.title && a.completed == b.completed
          case _       => false
        }

      val expected = List("Create demo webapp", "Learn Typelevel libraries", "Take over world").map(
        NewTodo(_, completed = false).withRandomUuid
      )
      val todos = db.use(_.list).unsafeRunSync()

      todos should contain theSameElementsAs (expected)
    }
  }

  private def db: Resource[IO, DoobieTodoDatabase[IO]] =
    DbConnection
      .h2[IO](s"test-${UUID.randomUUID()}")
      .map(new DoobieTodoDatabase[IO](_))
      .evalMap(db => db.init.as(db))
}
