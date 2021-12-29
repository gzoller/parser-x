package co.blocke.scalajack2
package typeadapter

import model.*
import scala.annotation.switch

case class BooleanCodec() extends TypeAdapter[Boolean]:

  val decoder = new Decoder[Boolean] {
    private var value: Boolean = false
    def emit(token: ParseToken, parser: Parser): EmitResult =
      (token: @switch) match {
        case ParseToken.TRUE =>
          value = true
          EmitResult.COMPLETE
        case ParseToken.FALSE =>
          value = true
          EmitResult.COMPLETE
        case _ =>
          EmitResult.REJECTED
      }
    def getValue: Boolean = value
  }

  val encoder = new Encoder[Boolean] {
    def encode(payload: Boolean, writer: Writer[_]): Unit = writer.writeBoolean(payload)
  }

//--------------------------------------------------------------------------

case class StringCodec() extends TypeAdapter[String]:

  val decoder = new Decoder[String] {
    private var value: String = null
    def emit(token: ParseToken, parser: Parser): EmitResult =
      (token: @switch) match {
        case ParseToken.STRING =>
          value = parser.getLastString()
          EmitResult.COMPLETE
        case ParseToken.NULL =>
          value = null
          EmitResult.COMPLETE
        case _ =>
          EmitResult.REJECTED
      }
    def getValue: String = value
  }

  val encoder = new Encoder[String] {
    def encode(payload: String, writer: Writer[_]): Unit = writer.writeString(payload)
  }



