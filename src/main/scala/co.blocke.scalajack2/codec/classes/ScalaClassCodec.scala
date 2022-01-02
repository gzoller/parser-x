package co.blocke.scalajack2
package codec.classes

import model.*
import co.blocke.scala_reflection.info.*
import co.blocke.scala_reflection.*

import scala.annotation.switch
import scala.collection.mutable

trait ScalaClassCodec[T](implicit codecCache: CodecCache) extends ClassCodecBase[T]:

  val typeMembersByName:  Map[String, TypeMemberInfo]

  val isSJCapture = info.asInstanceOf[ClassInfo].hasMixin(SJ_CAPTURE)

  def _read_createInstance(args: Array[Object], foundBits: Long, captured: java.util.HashMap[String, _]): T
//  def _read_updateFieldMembers( fmbn: Map[String, ClassFieldMember[_,_]]): ScalaClassTypeAdapter[T]

  val decoder: Decoder[T] = new Decoder[T] {
    private var inObject             = false
    private var objectDone           = false
    private val args: Array[Object]  = Array.fill(numArgs){null.asInstanceOf[Object]}
    private var fieldBits: Long      = fieldBitsInitial
    private var expectLabel: Boolean = true
    private var fieldIndex: Int      = -1
    private var fieldCodec: Codec[_]  = null

    override def reset(): Unit =
      inObject   = false
      objectDone = false
      fieldBits  = fieldBitsInitial
      fieldIndex = -1
      fieldCodec = null
      defaultArgMap.map{ (i,obj) => args(i) = obj }

    def emit(token: ParseToken, parser: Parser): Either[EmitResult, T] =
      if objectDone then
        error("Unexpected content after object end", parser)
      else if inObject then
        if expectLabel then
          (token: @switch) match {
            case ParseToken.STRING =>
              val fieldName = parser.getLastString()
              val fieldMember = fieldMembersByName.get(fieldName).getOrElse(???)
              fieldCodec = fieldMember.valueCodec
              fieldIndex = fieldMember.info.index
              fieldBits = setBit(fieldBits, fieldIndex)
              expectLabel = false
              Left(EmitResult.ACCEPTED)
            case ParseToken.OBJEND =>
              inObject = false
              objectDone = true
              if fieldBits != allFieldBits then
                error("Missing fields. Cannot create object", parser)
              else
                val result = _read_createInstance(args, fieldBits, null)
                fieldCodec.decoder.reset()
                Right(result)
            case _ =>
              error("Unexpected token where field label expected", parser)
          }
        else
          (fieldCodec.decoder.emit(token,parser): @switch) match {
            case Right(cooked) =>
              args(fieldIndex) = cooked.asInstanceOf[Object]
              fieldCodec.decoder.reset()
              expectLabel = true
              Left(EmitResult.ACCEPTED)
            case Left(EmitResult.ACCEPTED) =>
              Left(EmitResult.ACCEPTED) // do nothing... let element consume token
            case Left(EmitResult.REJECTED) =>
              error("Unexpected token in object "+token, parser)
          }
      else
        token match {
          case ParseToken.OBJSTART =>
            this.reset()
            inObject = true
            expectLabel = true
            Left(EmitResult.ACCEPTED)
          case ParseToken.NULL =>
            inObject = false
            objectDone = true
            expectLabel = false
            Right(null.asInstanceOf[T])
          case _ =>
            Left(EmitResult.REJECTED)
        }
  }

  val encoder: Encoder[T] = new Encoder{
    def encode(payload: T, writer: Writer[_]): Unit =
      if payload == null then
        writer.writeNull()
      else
        var isFirst = true
        writer.writeObject( ()=>{
          orderedFieldNames
            .map { fieldName => // Strictly-speaking JSON has no order, but it's clean to write out in constructor order.
              val oneField = fieldMembersByName(fieldName)
              val target = oneField.info.valueOf(payload)
              val enc = oneField.valueCodec.encoder.asInstanceOf[Encoder[Any]]
              writer.writeField(isFirst, fieldName, () => {enc.encode(target, writer)})
              isFirst = false
            }})
  }
