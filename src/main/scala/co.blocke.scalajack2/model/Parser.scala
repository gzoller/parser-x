package co.blocke.scalajack2
package model

enum ParseToken:
  case STRING, NULL, TRUE, FALSE, LONG, DOUBLE, OBJSTART, OBJEND, ARRAYSTART, ARRAYEND

trait Parser:
  def getLastString(): String
  def getErrorContext(): String
  def getLastLong(): Long

