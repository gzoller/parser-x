package co.blocke.scalajack2
package codec

import model.*
import co.blocke.scala_reflection.*
import scala.collection.mutable

/** This helps fix the concurrent/recursion error on maps.  This lets the Codec resolve later (i.e. Lazy)
 */
case class LazyCodec[T](codecCache: CodecCache, info: RType)
  extends Codec[T] {

  var resolvedCodec: Codec[T] = null

  override def resolved: Codec[T] = {
    var codec = resolvedCodec

    // $COVERAGE-OFF$Can't really test as this is triggered by race condition, if it can happen at all.
    if (codec == null) {
      codec = codecCache.of(info).resolved.asInstanceOf[Codec[T]]
      if (codec.isInstanceOf[LazyCodec[_]]) {
        throw new IllegalStateException(
          s"Type adapter for ${info.name} is still being built"
        )
      }
      resolvedCodec = codec
    }
    // $COVERAGE-ON$

    codec
  }

  val decoder: Decoder[T] = resolved.decoder
  val encoder: Encoder[T] = resolved.encoder
}