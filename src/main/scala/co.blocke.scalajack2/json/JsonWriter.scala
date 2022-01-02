package co.blocke.scalajack2
package json

import model.*

case class JsonWriter() extends Writer[JSON]:

  val sb = new java.lang.StringBuilder()

  def getValue: JSON = sb.toString.asInstanceOf[JSON]

  def writeArray[T]( payload: List[T], elementEncoder: Encoder[T] ) =
    if payload == null then
      writeNull()
    else
      sb.append('[')
      var first = true
      payload.map{ p =>
        if !first then
          sb.append(',')
        first = false
        elementEncoder.encode(p, this)
      }
      sb.append(']')

  def writeObject[T]( payload: List[(String,Object,Encoder[_])] ): Unit =
    if payload == null then
      writeNull()
    else
      sb.append('{')
      var first = true
      payload.map{ (label,obj,enc) =>
        if !first then
          sb.append(',')
        first = false
        writeString(label)
        sb.append(':')
        enc.asInstanceOf[Encoder[Any]].encode(obj, this)
      }
      sb.append('}')

  def writeLong( payload: Long ): Unit =
    sb.append(payload.toString)

  def writeString( payload: String ) =
    if payload == null then
      writeNull()
    else
      sb.append('"')
      sb.append(payload)
      sb.append('"')

  def writeNull() =
    sb.append("null")

  def writeBoolean(b: Boolean) =
    if b then
      sb.append("true")
    else
      sb.append("false")
