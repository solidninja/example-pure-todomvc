package is
package solidninja
package todomvc
package server

import java.util.UUID

import scala.concurrent.ExecutionContext

import cats.effect.{ContextShift, IO}
import cats.syntax.apply._
import io.circe.literal._
import is.solidninja.todomvc.protocol.Todo
import org.http4s._
import org.http4s.dsl.io.GET
import org.http4s.implicits._
import org.scalatest.{FreeSpec, Matchers}

class TodoAPITest extends FreeSpec with Matchers with Http4sSpec {
  import org.http4s.circe._

  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global
  implicit val cs: ContextShift[IO] = IO.contextShift(implicitly)

  "GET /todo" - {
    "should return empty list" in {
      val response = StubTodoDatabase[IO].use { db =>
        API.todoService(db).orNotFound.run(Request(GET, uri"/todo"))
      }

      val expected = json"[]"

      checkResponse(response, Status.Ok, Some(expected))
    }

    "should return some TODOs when database contains them" in {
      val todos = List(
        Todo(UUID.fromString("08b58315-0aac-4a17-aa30-2f1c11224519"), "Homework", completed = true),
        Todo(UUID.fromString("2694e363-6f6e-4c9b-8dff-3e87dd289d2f"), "Win the war on hogs", completed = false)
      )

      val response = StubTodoDatabase[IO].use { db =>
        db.sync(todos) *> API.todoService(db).orNotFound.run(Request(GET, uri"/todo"))
      }

      val expected = json"""[
        {
          "id": "08b58315-0aac-4a17-aa30-2f1c11224519",
          "title": "Homework",
          "completed": true
        },
        {
          "id": "2694e363-6f6e-4c9b-8dff-3e87dd289d2f",
          "title": "Win the war on hogs",
          "completed": false
        }
      ]"""

      checkResponse(response, Status.Ok, Some(expected))
    }
  }
}
