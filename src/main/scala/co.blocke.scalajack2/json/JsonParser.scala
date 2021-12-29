package co.blocke.scalajack2
package json

import model.*

import scala.annotation.{switch, tailrec}

opaque type JSON = String

case class JsonParser[T](jsRaw: JSON, decoder: Decoder[T]) extends Parser:

  private val js = jsRaw.asInstanceOf[String]
  private val jsChars: Array[Char] = js.toCharArray
  private var i = 0   // Master index into byte array
  private val max: Int             = jsChars.length
  private var mark = 0

  def getLastString(): String = js.substring(mark,i)
  def getErrorContext(): String = ""+i

  @tailrec private def consumeString(): Unit =
    if i == max then
      decoder.error("Unterminated string", this)
    else if jsChars(i) == '"' then
      decoder.emit(ParseToken.STRING, this)
      i += 1
    else
      i += 1
      consumeString()

  while( i < max ) {
    ( jsChars(i): @switch ) match {
      case '"' =>
        mark = i+1
        i += 1
        consumeString()
      case 'n' =>
        if i + 4 <= max && jsChars(i+1) == 'u' && jsChars(i+2) == 'l' && jsChars(i+3) == 'l' then
          decoder.emit(ParseToken.NULL, this)
          i += 4
        else
          decoder.error("Expected null", this)
          i = max
      case 't' =>
        if i + 4 <= max && jsChars(i+1) == 'r' && jsChars(i+2) == 'u' && jsChars(i+3) == 'e' then
          decoder.emit(ParseToken.TRUE, this)
          i += 4
        else
          decoder.error("Expected true", this)
          i = max
      case 'f' =>
        if i + 5 <= max && jsChars(i+1) == 'a' && jsChars(i+2) == 'l' && jsChars(i+3) == 's' && jsChars(i+4) == 'e' then
          decoder.emit(ParseToken.FALSE, this)
          i += 5
        else
          decoder.error("Expected false", this)
          i = max
      case ' ' =>
        i += 1
      case '\t' =>
        i += 1
      case '\n' =>
        i += 1
      case '{' =>
        decoder.emit(ParseToken.OBJSTART, this)
        i += 1
      case '}' =>
        decoder.emit(ParseToken.OBJEND, this)
        i += 1
      case '[' =>
        decoder.emit(ParseToken.ARRAYSTART, this)
        i += 1
      case ']' =>
        decoder.emit(ParseToken.ARRAYEND, this)
        i += 1
      case ':' =>
        i += 1
      case ',' =>
        i += 1
      case c =>
        decoder.error("Unexpected character "+c, this)
        i = max
    }
  }
