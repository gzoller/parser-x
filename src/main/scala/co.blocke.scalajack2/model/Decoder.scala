package co.blocke.scalajack2
package model

enum EmitResult:
  case ACCEPTED, REJECTED

trait Decoder[T]:
  def emit(token: ParseToken, parser: Parser): Either[EmitResult, T]
  def error( msg: String, parser: Parser ) = throw new Exception(msg+" @ "+parser.getErrorContext())
  def reset(): Unit = {}
  def getResult: T = ???  // undefined except for "assembled" types, e.g. classes, collections, ...
