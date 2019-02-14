package is
package solidninja
package todomvc
package server

import cats.effect._
import cats.implicits._
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.server.middleware.CORS

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    DoobieTodoDatabase.create.map(API.todoService).use { routes =>
      BlazeServerBuilder[IO]
        .bindHttp(8080, "localhost")
        .withHttpApp(CORS(routes).orNotFound)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    }
}
