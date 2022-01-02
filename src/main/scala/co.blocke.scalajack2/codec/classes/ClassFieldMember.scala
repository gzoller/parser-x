package co.blocke.scalajack2
package codec.classes

import co.blocke.scala_reflection.info._
import codec._
import model._

case class ClassFieldMember[OWNER,T](
                                      info:           FieldInfo,
                                      valueCodec:     Codec[T],
                                      outerClass:     java.lang.Class[OWNER],  // class that "owns" this field
                                      dbKeyIndex:     Option[Int],
                                      fieldMapName:   Option[String]
                                    ):
  def name: String = fieldMapName.getOrElse(info.name)
  lazy val isOptional: Boolean = false
//  lazy val isOptional: Boolean = valueCodec match {
//    case _: OptionTypeAdapter[_] => true
//    case _: JavaOptionalTypeAdapter[_] => true
//    case _ if info.annotations.contains(OPTIONAL_ANNO) => true
//    case _ => false
//  }