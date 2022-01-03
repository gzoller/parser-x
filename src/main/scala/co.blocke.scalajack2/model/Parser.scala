package co.blocke.scalajack2
package model

enum ParseToken:
  case STRING, NULL, TRUE, FALSE, LONG, DOUBLE, OBJSTART, OBJEND, ARRAYSTART, ARRAYEND

trait Parser:
  def setDecoder(decoder: Decoder[_]): Unit
  def getLastString(): String
  def getLastLong(): Long
  def getErrorContext(): String


