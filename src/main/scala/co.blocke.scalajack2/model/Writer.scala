package co.blocke.scalajack2
package model

/*
 The implementations of Writer will almost certainly be statefull, containing some kind of buffer
 for utlimate output, e.g. a StringBuilder for JSON.  This isolates the statefullness in one spot.
 */
trait Writer[WIRE]:
  def writeArray[T]( payload: List[T], elementEncoder: Encoder[T] ): Unit
  def writeString( payload: String ): Unit
  def writeLong( payload: Long ): Unit
  def writeNull(): Unit
  def writeBoolean(b: Boolean): Unit
  def getValue: WIRE