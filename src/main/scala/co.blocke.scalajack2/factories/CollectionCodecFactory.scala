package co.blocke.scalajack2
package factories

import model.*
import codec.*
import co.blocke.scala_reflection.*
import co.blocke.scala_reflection.impl.CollectionRType
import co.blocke.scala_reflection.info._

object CollectionCodecFactory extends CodecFactory:
  def matches(concrete: RType): Boolean =
    concrete match {
      case _: CollectionRType => true
      case _ => false
    }

  def makeCodec(concrete: RType)(implicit codecCache: CodecCache): Codec[_] =
    concrete match {
      case c: SeqLikeInfo =>
        val elementInfo = c.elementType
        val companionClass = Class.forName(c.infoClass.getName + "$")
        val companionInstance = companionClass.getField("MODULE$").get(companionClass)
        val builderMethod = companionClass.getMethod("newBuilder")
        val elementCodec = codecCache.of(elementInfo)
        SeqCodec(
          new SeqDecoder(
            builderMethod,
            companionInstance,
            elementCodec.decoder),
          new SeqEncoder(elementCodec.encoder)
        )
    }
