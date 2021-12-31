package co.blocke.scalajack2
package factories

import model.*
import codec.*
import co.blocke.scala_reflection.*
import co.blocke.scala_reflection.impl.PrimitiveType

object StringCodecFactory extends CodecFactory:
  def matches(concrete: RType): Boolean = concrete.infoClass == PrimitiveType.Scala_String.infoClass
  def makeCodec(concrete: RType)(implicit codecCache: CodecCache): ()=>Codec[String] = ()=>StringCodec()
