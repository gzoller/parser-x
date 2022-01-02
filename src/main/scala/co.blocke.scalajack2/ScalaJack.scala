package co.blocke.scalajack2

import model._
import json._

object ScalaJack:
  def apply()                                      = JsonFlavor()
  def apply[S](kind: JackFlavor[S]): JackFlavor[S] = kind


inline def setBit( in: Long, index: Int ): Long = in | 1 << index

val CHANGE_ANNO = "co.blocke.scalajack.Change"
val OPTIONAL_ANNO = "co.blocke.scalajack.Optional"
val IGNORE = "co.blocke.scalajack.Ignore"
val DB_KEY = "co.blocke.scalajack.DBKey"
val DB_COLLECTION = "co.blocke.scalajack.Collection"
val SJ_CAPTURE  = "co.blocke.scalajack.SJCapture"