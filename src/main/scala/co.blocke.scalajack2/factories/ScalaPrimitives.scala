package co.blocke.scalajack2
package factories

import model.*
import codec.*
import co.blocke.scala_reflection.*
import co.blocke.scala_reflection.impl.PrimitiveType
import scala.annotation.switch

/*
 For Codecs that consume/produce immediately in one emit, for example primitives, the Factory
 can also be the Codec, as we're not accumulating pieces to build a bigger result.  This saves
 a bit of unnecessary object creation.
 */

object BooleanTypeAdapterFactory extends CodecFactory with Codec[Boolean]:
  def matches(concrete: RType): Boolean = concrete.infoClass == PrimitiveType.Scala_Boolean.infoClass
  def makeCodec(concrete: RType)(implicit codecCache: CodecCache): ()=>Codec[Boolean] = ()=>this

  val decoder = new Decoder[Boolean] {
    def emit(token: ParseToken, parser: Parser): Either[EmitResult, Boolean] =
      (token: @switch) match {
        case ParseToken.TRUE =>
          Right(true)
        case ParseToken.FALSE =>
          Right(false)
        case _ =>
          Left(EmitResult.REJECTED)
      }
  }

  val encoder = new Encoder[Boolean] {
    def encode(payload: Boolean, writer: Writer[_]): Unit = writer.writeBoolean(payload)
  }


//---------------------------------------------------------------------------------


object StringCodecFactory extends CodecFactory with Codec[String]:
  def matches(concrete: RType): Boolean = concrete.infoClass == PrimitiveType.Scala_String.infoClass
  def makeCodec(concrete: RType)(implicit codecCache: CodecCache): ()=>Codec[String] = ()=>this

  val decoder = new Decoder[String] {
    def emit(token: ParseToken, parser: Parser): Either[EmitResult, String] =
      (token: @switch) match {
        case ParseToken.STRING =>
          Right(parser.getLastString())
        case ParseToken.NULL =>
          Right(null)
        case _ =>
          Left(EmitResult.REJECTED)
      }
  }

  val encoder = new Encoder[String] {
    def encode(payload: String, writer: Writer[_]): Unit = writer.writeString(payload)
  }

