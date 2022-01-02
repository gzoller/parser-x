package co.blocke.scalajack2

import json.*
import codec.*
import io.bullet.borer.Json
import co.blocke.scalajack.ScalaJack

case class Person(name: String, age: Int)


def timeit( fn: ()=>Any, msg: String ) =
  val now = System.currentTimeMillis()
  for (i <- 1 to 10000)
    fn()
  val later = System.currentTimeMillis()
  println(msg+" :: "+(later-now))

@main def hello: Unit =

//  val js:JSON = """[["one","two"], ["three", "four"], ["five", "six"], ["seven", "eight"], ["nine", "ten"]]""".asInstanceOf[JSON]
//  val js:JSON = """[[1,2], [31,41], [543,654], [-7,-8], [-91,-101]]""".asInstanceOf[JSON]

  val js: JSON = """{"name":"Greg","age":55}""".asInstanceOf[JSON]

//  val sj2 = co.blocke.scalajack2.ScalaJack()
//  val obj = sj2.read[Person](js)
//  println(obj)
//  println(sj2.render(obj))

  val sj2 = co.blocke.scalajack2.ScalaJack()
  timeit( ()=>{
    val obj = sj2.read[Person](js)
    sj2.render(obj)
  },"SJ2")

//  timeit( ()=>{
//    val obj = Json.decode(js.asInstanceOf[String].getBytes).to[Person].value
//    val obj = Json.decode(js.asInstanceOf[String].getBytes).to[List[List[Int]]].value
//    Json.encode(obj).toUtf8String
//  }, "borer" )

  val sj = ScalaJack()
  timeit( () => {
    val obj = sj.read[Person](js.asInstanceOf[co.blocke.scalajack.json.JSON])
    sj.render(obj)
  }, "ScalaJack" )

inline def setBit( in: Long, index: Int ): Long = in | 1 << index

val CHANGE_ANNO = "co.blocke.scalajack.Change"
val OPTIONAL_ANNO = "co.blocke.scalajack.Optional"
val IGNORE = "co.blocke.scalajack.Ignore"
val DB_KEY = "co.blocke.scalajack.DBKey"
val DB_COLLECTION = "co.blocke.scalajack.Collection"
val SJ_CAPTURE  = "co.blocke.scalajack.SJCapture"