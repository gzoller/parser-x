package co.blocke.scalajack2

import model._
import json._

object ScalaJack:
  def apply()                                      = JsonFlavor()
  def apply[S](kind: JackFlavor[S]): JackFlavor[S] = kind

