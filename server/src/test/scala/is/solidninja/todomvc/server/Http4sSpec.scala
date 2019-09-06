package is
package solidninja
package todomvc
package server

import cats.effect.IO
import org.http4s._
import org.scalatest.{Matchers, Suite}

trait Http4sSpec extends Matchers { self: Suite =>

  /**
    * Check http4s response against an expected status code and an (optional) body
    */
  def checkResponse[A](actual: IO[Response[IO]], expectedStatus: Status, expectedBody: Option[A])(
      implicit ev: EntityDecoder[IO, A]
  ): Unit = {
    val gotResponse = actual.unsafeRunSync
    withClue("HTTP status") {
      gotResponse.status should ===(expectedStatus)
    }

    withClue("HTTP body") {
      val _ = expectedBody.fold[Any](gotResponse.body.compile.toVector.unsafeRunSync should be(empty))(
        expected => gotResponse.as[A].unsafeRunSync should ===(expected)
      )
    }
  }

}
