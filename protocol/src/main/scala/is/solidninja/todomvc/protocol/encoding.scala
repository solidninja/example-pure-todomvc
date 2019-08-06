package is
package solidninja
package todomvc
package protocol

import io.circe._
import io.circe.generic.semiauto._

trait JsonProtocol {
  implicit val todoCodec: Codec[Todo] = deriveCodec
}

object JsonProtocol extends JsonProtocol
