package co.blocke.scalajack2
package codec

import model.*

import java.lang.reflect.Method
import scala.annotation.switch

case class SeqCodec[ELEM,TO](decoder: Decoder[TO], encoder: Encoder[TO]) extends Codec[TO]

class SeqDecoder[ELEM,List[ELEM]](
                                        builderMethod: Method,
                                        companionInstance:  Object,
                                        elemDecoder: Decoder[ELEM]
                                      ) extends Decoder[List[ELEM]]:

  private var inArray           = false
  private var arrayDone         = false

  private var value: List[ELEM] = null.asInstanceOf[List[ELEM]]
  private val builder           = builderMethod.invoke(companionInstance).asInstanceOf[scala.collection.mutable.Builder[ELEM,List[ELEM]]]

  override def reset(): Unit =
    builder.clear()
    inArray   = false
    arrayDone = false
    value     = null.asInstanceOf[List[ELEM]]

  def getValue: List[ELEM] = value

  def emit(token: ParseToken, parser: Parser): EmitResult =
    if arrayDone then
      error("Unexpected content after array end", parser)
    else if inArray then
      (elemDecoder.emit(token, parser): @switch) match {
        case EmitResult.COMPLETE =>
          builder += elemDecoder.getValue
          elemDecoder.reset()
          EmitResult.ACCEPTED
        case EmitResult.ACCEPTED =>
          EmitResult.ACCEPTED // do nothing... let element consume token
        case EmitResult.REJECTED =>
          if token == ParseToken.ARRAYEND then
            value = builder.result()
            inArray = false
            arrayDone = true
            EmitResult.COMPLETE
          else
            error("Unexpected token "+token, parser)
      }
    else
      token match {
        case ParseToken.ARRAYSTART =>
          inArray = true
          EmitResult.ACCEPTED
        case ParseToken.ARRAYEND =>
          EmitResult.REJECTED
        case ParseToken.NULL =>
          value = null.asInstanceOf[List[ELEM]]
          inArray = false
          arrayDone = true
          EmitResult.COMPLETE
        case _ =>
          error("Unexpected token "+token, parser)
      }


class SeqEncoder[ELEM,TO](elemEncoder: Encoder[ELEM]) extends Encoder[TO]:
  def encode(payload: TO, writer: Writer[_]): Unit =
    writer.writeArray[ELEM](payload.asInstanceOf[Seq[ELEM]].toList, elemEncoder)