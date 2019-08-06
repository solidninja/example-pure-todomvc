package is
package solidninja
package todomvc
package server

import cats.effect._
import cats.syntax.functor._
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.server.middleware.CORS

object Main extends IOApp {

  private def db: Resource[IO, TodoDatabase[IO]] =
    for {
      xa <- DbConnection.h2[IO]()
      db = new DoobieTodoDatabase[IO](xa)
      _ <- Resource.liftF(db.init)
    } yield db

  override def run(args: List[String]): IO[ExitCode] =
    db.map(API.todoService).use { routes =>
      BlazeServerBuilder[IO]
        .bindHttp(8080, "localhost")
        .withHttpApp(CORS(routes).orNotFound)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    }
}
