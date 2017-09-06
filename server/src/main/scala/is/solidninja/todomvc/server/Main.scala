package is
package solidninja
package todomvc
package server

import fs2.Stream
import cats.effect.IO
import org.http4s.server.blaze._
import org.http4s.server.middleware.CORS
import org.http4s.util.StreamApp

object Main extends StreamApp[IO] {

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, Nothing] =
    Stream
      .eval(DoobieTodoDatabase.create.map(API.todoService))
      .flatMap(
        todoService =>
          BlazeBuilder[IO]
            .bindHttp(8080, "localhost")
            .mountService(CORS(todoService), "/")
            .serve)
}
