package co.blocke.scalajack2
package model

trait JackFlavor[WIRE]:

  def getParser[T](input: WIRE, decoder: Decoder[T]): Parser
  def getWriter: Writer[WIRE]

  // TODO... this is far more complex in final form.  See JackFlavor bakeCache()
  lazy val codecCache: CodecCache =
    CodecCache(
      this,
      CodecCache.StandardFactories
      )

  final inline def read[T](input: WIRE): T =
    val codec:Codec[T] = codecCache.of[T]
    getParser(input, codec.decoder)
    codec.decoder.getValue.asInstanceOf[T]

  final inline def render[T](t: T): WIRE =
    val codec:Codec[T] = codecCache.of[T]
    val writer = getWriter
    codec.encoder.encode(t, writer)
    writer.getValue
