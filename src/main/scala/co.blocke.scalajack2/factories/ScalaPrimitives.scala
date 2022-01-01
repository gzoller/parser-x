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

object BooleanCodecFactory extends CodecFactory with Codec[Boolean] with Decoder[Boolean] with Encoder[Boolean]:
  def matches(concrete: RType): Boolean = concrete.infoClass == PrimitiveType.Scala_Boolean.infoClass
  def makeCodec(concrete: RType)(implicit codecCache: CodecCache): Codec[Boolean] = this

  val decoder: Decoder[Boolean] = this
  val encoder: Encoder[Boolean] = this

  def emit(token: ParseToken, parser: Parser): Either[EmitResult, Boolean] =
    (token: @switch) match {
      case ParseToken.TRUE =>
        Right(true)
      case ParseToken.FALSE =>
        Right(false)
      case _ =>
        Left(EmitResult.REJECTED)
    }

  def encode(payload: Boolean, writer: Writer[_]): Unit = writer.writeBoolean(payload)


//---------------------------------------------------------------------------------


object IntCodecFactory extends CodecFactory with Codec[Int] with Decoder[Int] with Encoder[Int]:
  def matches(concrete: RType): Boolean = concrete.infoClass == PrimitiveType.Scala_Int.infoClass
  def makeCodec(concrete: RType)(implicit codecCache: CodecCache): Codec[Int] = this

  val decoder: Decoder[Int] = this
  val encoder: Encoder[Int] = this

  def emit(token: ParseToken, parser: Parser): Either[EmitResult, Int] =
    (token: @switch) match {
      case ParseToken.LONG =>
        Right(parser.getLastLong().toInt)
      case _ =>
        Left(EmitResult.REJECTED)
    }

  def encode(payload: Int, writer: Writer[_]): Unit = writer.writeLong(payload)


//---------------------------------------------------------------------------------


object StringCodecFactory extends CodecFactory with Codec[String] with Decoder[String] with Encoder[String]:
  def matches(concrete: RType): Boolean = concrete.infoClass == PrimitiveType.Scala_String.infoClass
  def makeCodec(concrete: RType)(implicit codecCache: CodecCache): Codec[String] = this

  val decoder: Decoder[String] = this
  val encoder: Encoder[String] = this

  def emit(token: ParseToken, parser: Parser): Either[EmitResult, String] =
    (token: @switch) match {
      case ParseToken.STRING =>
        Right(parser.getLastString())
      case ParseToken.NULL =>
        Right(null)
      case _ =>
        Left(EmitResult.REJECTED)
    }

  def encode(payload: String, writer: Writer[_]): Unit = writer.writeString(payload)

