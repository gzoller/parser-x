package co.blocke.scalajack2
package model

enum EmitResult:
  case ACCEPTED, REJECTED

trait Decoder[T]:
  def emit(token: ParseToken, parser: Parser): Either[EmitResult, T]
  def error( msg: String, parser: Parser ) = throw new Exception(msg+" @ "+parser.getErrorContext())
  def reset(): Unit = {}
  def getResult: T = ???  // undefined except for top-most level call (in JackFlavor) to retrieve fianl value
  val isPrimitive = false


// This Decoder derivation will capture te successful completed value to be returned.
// Used in JackFlavor to return the final value of parseing.
case class ToplevelDecoder[T](wrapped: Decoder[T]) extends Decoder[T]:
  private var result: T = null.asInstanceOf[T]
  def emit(token: ParseToken, parser: Parser): Either[EmitResult, T] =
    wrapped.emit(token,parser) match {
      case Right(worked) =>
        result = worked.asInstanceOf[T]
        Right(worked.asInstanceOf[T])
      case x => x
    }
  override def getResult =
    wrapped.reset()
    result

case class DebugDecoder(limit: Int) extends Decoder[Any]:
  private var counter = limit
  def emit(token: ParseToken, parser: Parser): Either[EmitResult, Any] =
    println("Token: "+token)
    counter -= 1
    if counter == 0 then
      System.exit(0)
    Right(0)
