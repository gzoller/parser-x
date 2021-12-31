package co.blocke.scalajack2
package codec

import model.*

import java.lang.reflect.Method
import scala.annotation.switch

case class SeqCodec[ELEM,TO](decoder: Decoder[TO], encoder: Encoder[TO]) extends Codec[TO]

case class SeqDecoder[ELEM,TO](
                            builderMethod: Method,
                            companionInstance:  Object,
                            elemDecoder: Decoder[ELEM]
                          ) extends Decoder[TO]:

  private var inArray           = false
  private var arrayDone         = false
  private var result            = null.asInstanceOf[TO]

  private val builder           = builderMethod.invoke(companionInstance).asInstanceOf[scala.collection.mutable.Builder[ELEM,TO]]

  override def getResult: TO = result

  override def reset(): Unit =
    builder.clear()
    inArray   = false
    arrayDone = false
    result    = null.asInstanceOf[TO]

  def emit(token: ParseToken, parser: Parser): Either[EmitResult, TO] =
    if arrayDone then
      error("Unexpected content after array end", parser)
    else if inArray then
      (elemDecoder.emit(token, parser): @switch) match {
        case Right(cooked) =>
          builder += cooked
          elemDecoder.reset()
          Left(EmitResult.ACCEPTED)
        case Left(EmitResult.ACCEPTED) =>
          Left(EmitResult.ACCEPTED) // do nothing... let element consume token
        case Left(EmitResult.REJECTED) =>
          if token == ParseToken.ARRAYEND then
            inArray = false
            arrayDone = true
            result = builder.result()
            Right(builder.result())
          else
            error("Unexpected token "+token, parser)
      }
    else
      token match {
        case ParseToken.ARRAYSTART =>
          inArray = true
          Left(EmitResult.ACCEPTED)
        case ParseToken.ARRAYEND =>
          Left(EmitResult.REJECTED)
        case ParseToken.NULL =>
          inArray = false
          arrayDone = true
          Right(null.asInstanceOf[TO])
        case _ =>
          error("Unexpected token "+token, parser)
      }


class SeqEncoder[ELEM,TO](elemEncoder: Encoder[ELEM]) extends Encoder[TO]:
  def encode(payload: TO, writer: Writer[_]): Unit =
    if payload == null then
      writer.writeNull()
    else
      writer.writeArray[ELEM](payload.asInstanceOf[Seq[ELEM]].toList, elemEncoder)