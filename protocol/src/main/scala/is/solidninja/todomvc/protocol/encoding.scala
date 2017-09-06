package is
package solidninja
package todomvc
package protocol

import io.circe._
import io.circe.generic.semiauto._

trait EncoderInstances {
  implicit val encodeTodo: Encoder[Todo] = deriveEncoder
}

trait DecoderInstances {
  implicit val decodeTodo: Decoder[Todo] = deriveDecoder
}

object JsonProtocol extends EncoderInstances with DecoderInstances
