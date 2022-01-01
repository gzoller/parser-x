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
    val wrapper = ToplevelDecoder[T](codecCache.of[T].decoder)
    getParser(input, wrapper)
    wrapper.getResult

  final inline def render[T](t: T): WIRE =
    val writer = getWriter
    codecCache.of[T].encoder.encode(t, writer)
    writer.getValue
