package co.blocke.scalajack2
package model

import co.blocke.scala_reflection.*

trait CodecFactory:
  def matches(concrete: RType): Boolean
  def makeCodec(concrete: RType)(implicit codecCache: CodecCache): Codec[_]