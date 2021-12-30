package co.blocke.scalajack2
package json

import model.*

class JsonFlavor() extends JackFlavor[JSON]:
  def getParser[T](input: JSON, decoder: Decoder[T]): Parser = JsonParser(input, decoder)
  def getWriter: Writer[JSON] = JsonWriter()
