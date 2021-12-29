package co.blocke.scalajack2
package model

enum EmitResult:
  case COMPLETE, ACCEPTED, REJECTED

trait Decoder[T]:
  def emit(token: ParseToken, parser: Parser): EmitResult
  def getValue: T
  def error( msg: String, parser: Parser ) = throw new Exception(msg+" @ "+parser.getErrorContext())
  def reset(): Unit = {}