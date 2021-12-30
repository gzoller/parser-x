package co.blocke.scalajack2
package model

trait Codec[T]:
  val decoder: Decoder[T]
  val encoder: Encoder[T]
