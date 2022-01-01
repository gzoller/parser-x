package co.blocke.scalajack2
package codec

import model.*

import java.lang.reflect.Method
import scala.annotation.switch

case class SeqCodec[ELEM,TO](builderMethod: Method,
                             companionInstance:  Object,
                             elementDecoder: Decoder[ELEM],
                             elementEncoder: Encoder[ELEM]) extends Codec[TO]:

  val decoder: Decoder[TO] = new Decoder[TO] {
    private var inArray           = false
    private var arrayDone         = false
    private val builder           = builderMethod.invoke(companionInstance).asInstanceOf[scala.collection.mutable.Builder[ELEM,TO]]

    override def reset(): Unit =
      builder.clear()
      inArray   = false
      arrayDone = false

    def emit(token: ParseToken, parser: Parser): Either[EmitResult, TO] =
      if arrayDone then
        error("Unexpected content after array end", parser)
      else if inArray then
        (elementDecoder.emit(token, parser): @switch) match {
          case Right(cooked) =>
            builder += cooked
            elementDecoder.reset()
            Left(EmitResult.ACCEPTED)
          case Left(EmitResult.ACCEPTED) =>
            Left(EmitResult.ACCEPTED) // do nothing... let element consume token
          case Left(EmitResult.REJECTED) =>
            if token == ParseToken.ARRAYEND then
              inArray = false
              arrayDone = true
              elementDecoder.reset()
              Right(builder.result())
            else
              error("Unexpected token "+token, parser)
        }
      else
        token match {
          case ParseToken.ARRAYSTART =>
            this.reset()
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
  }

  val encoder: Encoder[TO] = new Encoder[TO] {
    def encode(payload: TO, writer: Writer[_]): Unit =
      if payload == null then
        writer.writeNull()
      else
        writer.writeArray[ELEM](payload.asInstanceOf[Seq[ELEM]].toList, elementEncoder)
  }
