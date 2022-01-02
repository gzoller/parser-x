package co.blocke.scalajack2
package codec.classes

import model._
import co.blocke.scala_reflection._
import co.blocke.scala_reflection.info._

import scala.collection.mutable

// For case classes and Java/Scala plain classes, but not traits
trait ClassCodecBase[T] extends Codec[T]: // with Classish:
  val info:               RType
  val defaultArgMap:      Map[Int,Object]
  val fieldBitsInitial:   Long  // bitfield with default arg bits preset
  val allFieldBits:       Long  // refrence for what fully-populated object looks like
  val numArgs:            Int   // 63 fields max
  val isSJCapture:        Boolean
  val fieldMembersByName: Map[String, ClassFieldMember[_,_]]
  val isCaseClass:        Boolean = false
  val orderedFieldNames:  List[String]
  val dbCollectionName:   Option[String]

  def dbKeys: List[ClassFieldMember[_,_]] =
    fieldMembersByName.values.toList
      .filter(_.dbKeyIndex.isDefined)
      .sortBy(_.dbKeyIndex.get)