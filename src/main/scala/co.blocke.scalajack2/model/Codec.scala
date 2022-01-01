package co.blocke.scalajack2
package model

trait Codec[T]:
  //def resolved: Codec[T] = this // Might be something else during Lazy construction
  val decoder: Decoder[T]
  val encoder: Encoder[T]
