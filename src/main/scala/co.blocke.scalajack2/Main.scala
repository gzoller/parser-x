package co.blocke.scalajack2

import json.*
import codec.*
import io.bullet.borer.Json
import io.bullet.borer.{ Encoder => BEncoder, Decoder => BDecoder }
import co.blocke.scalajack.ScalaJack
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

case class Foo(a: Int)
case class Person(name: String, age: Int, foo: Foo)

case class Person2(name: String, age: Int, boss: Person2)

def timeit(fn: () => Any, msg: String) =
  val now = System.currentTimeMillis()
  for (i <- 1 to 10000)
    fn()
  val later = System.currentTimeMillis()
  println(msg + " :: " + (later - now))

@main def hello: Unit =

//  val js:JSON = """[["one","two"], ["three", "four"], ["five", "six"], ["seven", "eight"], ["nine", "ten"]]""".asInstanceOf[JSON]
//  val js:JSON = """[[1,2], [31,41], [543,654], [-7,-8], [-91,-101]]""".asInstanceOf[JSON]
  val sj = ScalaJack()
  val sj2 = co.blocke.scalajack2.ScalaJack()

//  val js: JSON = """{"name":"Greg","age":55,"foo":{"a":1}}""".asInstanceOf[JSON]
  val js: JSON =
    """{"name":"Greg","age":55,"boss":{"name":"Bryan","age":43,"boss":{"name":"Will","age":43,"boss":{"name":"Robb","age":50,"boss":null}}}}"""
      .asInstanceOf[JSON]

  val obj = sj2.read[Person2](js)

  // --- For Borer
  implicit val encoder1: BEncoder[Foo] = BEncoder { (writer, foo) =>
    writer.writeMapStart().writeString("a").writeInt(foo.a).writeMapClose()
  }
  implicit val decoder1: BDecoder[Foo] = BDecoder { reader =>
    val unbounded = reader.readMapOpen(1)
    reader.readString()
    val a = reader.readInt()
    val foo = new Foo(a)
    reader.readMapClose(unbounded, foo)
    foo
  }
  implicit val encoder: BEncoder[Person] = BEncoder { (writer, person) =>
    writer
      .writeMapStart()
      .writeString("name")
      .writeString(person.name)
      .write("age")
      .writeInt(person.age)
      .writeString("foo")
      .write[Foo](person.foo)
      .writeMapClose()
  }
  implicit val decoder: BDecoder[Person] = BDecoder { reader =>
    val unbounded = reader.readMapOpen(3)
    var n: String = ""
    var a: Int = 0
    var f: Foo = null
    for i <- (0 to 2) do
      reader.readString() match {
        case "name" => n = reader.readString()
        case "age"  => a = reader.readInt()
        case "foo"  => f = reader.read[Foo]()
      }
    val person = new Person(n, a, f)
    reader.readMapClose(unbounded, person)
    person
  }
//--------------

  timeit(
    () => {
      sj2.read[Person2](js)
    },
    "SJ2 decoding       "
  )
  timeit(
    () => {
      sj.read[Person2](js.asInstanceOf[co.blocke.scalajack.json.JSON])
    },
    "ScalaJack decoding "
  )
//  timeit(
//    () => {
//      decode[Person2](js.asInstanceOf[String]).asInstanceOf[Right[_, Person2]]
//    },
//    "Circe decoding     "
//  )
//  timeit(
//    () => {
//      Json.decode(js.asInstanceOf[String].getBytes).to[Person2].value
//    },
//    "borer decoding     "
//  )

  println("--------------------")

  timeit(
    () => {
      sj2.render(obj)
    },
    "SJ2 encoding       "
  )
  timeit(
    () => {
      sj.render(obj)
    },
    "ScalaJack encoding "
  )
//  timeit(
//    () => {
//      obj.asJson.noSpaces
//    },
//    "Circe encoding     "
//  )
//  timeit(
//    () => {
//      Json.encode(obj).toUtf8String
//    },
//    "borer encoding     "
//  )

//  val jsx = Json.decode(js.asInstanceOf[String].getBytes).to[Person].value
//  println(jsx)
//  println(Json.encode(jsx).toUtf8String)

//  val p2 = Person2(
//    "Greg",
//    55,
//    Person2("Bryan", 43, Person2("Will", 43, Person2("Robb", 50, null)))
//  )
//  val rtype = co.blocke.scala_reflection.RType.of[Person2]
//  val z = sj2.render(p2)
//  println(z)

// {"name":"Greg","age":55,"boss":{"name":"Bryan","age":43,"boss":{"name":"Will","age":43,"boss":{"name":"Robb","age":50,"boss":null}}}}
// 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
//           1         2         3         4         5         6         7         8         9

//  println(sj2.read[Person2](z))
//  println(sj2.read[Person2](z))
