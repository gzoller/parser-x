package co.blocke.scalajack2
package model

trait TypeAdapter[T]:
  val decoder: Decoder[T]
  val encoder: Encoder[T]
