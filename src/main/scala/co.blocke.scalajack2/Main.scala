package co.blocke.scalajack2

import json.*
import codec.*
import io.bullet.borer.Json
import io.bullet.borer.{Encoder=>BEncoder, Decoder=>BDecoder}
import co.blocke.scalajack.ScalaJack
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

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

  val sj = ScalaJack()
  val sj2 = co.blocke.scalajack2.ScalaJack()

  val js: JSON = """{"name":"Greg","age":55}""".asInstanceOf[JSON]
  val obj = sj2.read[Person](js)

  //--- For Borer
  implicit val encoder: BEncoder[Person] = BEncoder { (writer, person) =>
    writer
      .writeMapStart()
      .writeString(person.name)
      .writeInt(person.age)
      .writeMapClose()
  }
  implicit val decoder: BDecoder[Person] = BDecoder { reader =>
    val unbounded = reader.readMapOpen(2)
    var n: String = ""
    var a: Int = 0
    for i <- (0 to 1) do
      reader.readString() match {
        case "name" => n = reader.readString()
        case "age" => a = reader.readInt()
      }
    val person = new Person(
      n,
      a
    )
    reader.readMapClose(unbounded, person)
    person
  }
  //--------------

  //  val sj2 = co.blocke.scalajack2.ScalaJack()
  //  val obj = sj2.read[Person](js)
  //  println(obj)
  //  println(sj2.render(obj))

  timeit( ()=>{
    sj2.read[Person](js)
  },"SJ2 decoding       ")
  timeit( () => {
    sj.read[Person](js.asInstanceOf[co.blocke.scalajack.json.JSON])
  },"ScalaJack decoding ")
  timeit( () => {
    decode[Person](js.asInstanceOf[String]).asInstanceOf[Right[_,Person]]
  },"Circe decoding     ")
  timeit( ()=>{
    Json.decode(js.asInstanceOf[String].getBytes).to[Person].value
  },"borer decoding     ")

  println("--------------------")

  timeit( ()=>{
    sj2.render(obj)
  },"SJ2 encoding       ")
  timeit( () => {
    sj.render(obj)
  },"ScalaJack encoding ")
  timeit( () => {
    obj.asJson.noSpaces
  },"Circe encoding     ")
  timeit( ()=>{
    Json.encode(obj).toUtf8String
  },"borer encoding     ")
