package co.blocke.scalajack2
package model

import co.blocke.scalajack2.codec.SeqEncoder
import co.blocke.scalajack2.json.JsonWriter

trait JackFlavor[WIRE]:

  def getParser[T](input: WIRE, decoder: Decoder[T]): Parser
  def getWriter: Writer[WIRE]

  final inline def read[T](input: WIRE): T =
    val codec:Codec[T] = ??? //codecCache.of[T]
    getParser(input, codec.decoder)
    codec.decoder.getValue.asInstanceOf[T]

  final inline def render[T](t: T): WIRE =
    val codec:Codec[T] = ??? //codecCache.of[T]
    val writer = getWriter
    codec.encoder.encode(t, writer)
    writer.getValue
