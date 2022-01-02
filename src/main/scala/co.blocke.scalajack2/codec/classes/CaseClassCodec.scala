package co.blocke.scalajack2
package codec.classes

import model._
import co.blocke.scala_reflection.info._
import co.blocke.scala_reflection._

import scala.collection.mutable

case class CaseClassCodec[T](
                                    info:               RType,
                                    fieldMembersByName: Map[String, ClassFieldMember[_,_]],
                                    defaultArgMap:      Map[Int,Object],
                                    fieldBitsInitial:   Long,
                                    allFieldBits:       Long,
                                    numArgs:            Int,
                                    typeMembersByName:  Map[String, TypeMemberInfo],
                                    orderedFieldNames:  List[String],
                                    dbCollectionName:   Option[String] = None
                                  )(implicit codecCache: CodecCache) extends ScalaClassCodec[T]:

  override val isCaseClass = true

  private val classInfo = info.asInstanceOf[ScalaCaseClassInfo]
  private val constructor = classInfo.infoClass.getConstructors.head // <-- NOTE: head here isn't bullet-proof, but a generally safe assumption for case classes.  (Req because of arg typing mess.)

//  inline def constructWith(args: Array[Object]): T =
// ZZZ HEY!   This first part of the if statement doesn't seem to actually do anything!
//     Please check... seems to have to involve type members....
//    if (classInfo.typeMembers.nonEmpty) then
//      val originalArgTypes = classInfo.fields.map(_.fieldType.infoClass)
//      constructor.newInstance(args:_*).asInstanceOf[T]
//    else
//      constructor.newInstance(args:_*).asInstanceOf[T]

  def _read_createInstance(args: Array[Object], foundBits: Long, captured: java.util.HashMap[String, _]): T =
    val asBuilt = constructor.newInstance(args:_*).asInstanceOf[T]
//    if isSJCapture then
//      asBuilt.asInstanceOf[SJCapture].captured = captured
    asBuilt

//  def _read_updateFieldMembers( fmbn: Map[String, ClassFieldMember[_,_]]): ScalaClassTypeAdapter[T] =
//    this.copy(fieldMembersByName = fmbn)