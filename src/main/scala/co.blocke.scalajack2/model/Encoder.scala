package co.blocke.scalajack2
package model

trait Encoder[T]:
  def encode(payload: T, writer: Writer[_]): Unit