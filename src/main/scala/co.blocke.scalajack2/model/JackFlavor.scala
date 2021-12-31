package co.blocke.scalajack2
package model

//trait TopDecoder[T] extends Decoder[T]:
//  def getResult: T

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
//    val wrapper = new TopDecoder[T]{
//      private var result: T = null.asInstanceOf[T]
//      def emit(token: ParseToken, parser: Parser): Either[EmitResult, T] =
//        codec.decoder.emit(token,parser) match {
//          case Right(worked) =>
//            result = worked.asInstanceOf[T]
//            Right(worked.asInstanceOf[T])
//          case x => x
//        }
//      def getResult = result
//    }
//    getParser(input, wrapper)
//    wrapper.getResult
    getParser(input, codec.decoder)
    codec.decoder.getResult

  final inline def render[T](t: T): WIRE =
    val codec:Codec[T] = codecCache.of[T]
    val writer = getWriter
    codec.encoder.encode(t, writer)
    writer.getValue
