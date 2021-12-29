package co.blocke.scalajack2
package model

trait Writer[WIRE]:
  def writeArray[T]( payload: List[T], elementEncoder: Encoder[T] ): Unit
  def writeString( payload: String ): Unit
  def writeNull(): Unit
  def writeBoolean(b: Boolean): Unit
  def getValue: WIRE