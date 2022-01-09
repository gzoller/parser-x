package co.blocke.scalajack2
package model

import co.blocke.scala_reflection.RType

trait Codec[T]:
  lazy val decoder: Decoder[T]
  lazy val encoder: Encoder[T]
  def newDecoder(): Decoder[T] = decoder
  lazy val defaultValue: Option[T] = None

// These are created for self-referencing types: Person(... p: Person)
// At the moment of creation, we don't yet have a fully constructed Person Codec, so this
// is the placeholder in the cache.  It's replaced when the Person Codec is fully unwound/build.
// But... all the self-refs point to the SelfCodec, so upon use they're dereferenced.  This
// inserts a minor 1-level redirection "tax" for self-references, but... worth it.
case class SelfCodec[T](rType: RType, codecCache: CodecCache) extends Codec[T]:
  var _resolved: Option[Codec[T]] = None

  import codec.classes.ScalaClassCodec

  lazy val decoder =
    _resolved.getOrElse {
      val codec = codecCache.of(rType).asInstanceOf[Codec[T]]
      _resolved = Some(codec)
      codec
    }.newDecoder()

  lazy val encoder =
    _resolved.getOrElse {
      val codec = codecCache.of(rType).asInstanceOf[Codec[T]]
      _resolved = Some(codec)
      codec
    }.encoder

  override lazy val defaultValue: Option[T] =
    _resolved.getOrElse {
      val codec = codecCache.of(rType).asInstanceOf[Codec[T]]
      _resolved = Some(codec)
      codec
    }.defaultValue
