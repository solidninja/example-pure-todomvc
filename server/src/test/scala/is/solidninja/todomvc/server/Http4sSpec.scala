package is
package solidninja
package todomvc
package server

import cats.effect.IO
import org.http4s._

object Http4sSpec {

  // Return true if match succeeds; otherwise false
  def check[A](actual: IO[Response[IO]], expectedStatus: Status, expectedBody: Option[A])(
      implicit ev: EntityDecoder[IO, A]
  ): Boolean = {
    val actualResp = actual.unsafeRunSync
    val statusCheck = actualResp.status == expectedStatus
    val bodyCheck =
      expectedBody.fold[Boolean](actualResp.body.compile.toVector.unsafeRunSync.isEmpty)( // Verify Response's body is empty.
        expected => actualResp.as[A].unsafeRunSync == expected
      )
    statusCheck && bodyCheck
  }
}
