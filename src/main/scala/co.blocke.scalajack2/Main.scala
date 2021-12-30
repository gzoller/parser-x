package co.blocke.scalajack2

import json.*
import codec.*
import io.bullet.borer.Json
import co.blocke.scalajack.ScalaJack

def timeit( fn: ()=>Any, msg: String ) =
  val now = System.currentTimeMillis()
  for (i <- 1 to 10000)
    fn()
  val later = System.currentTimeMillis()
  println(msg+" :: "+(later-now))

@main def hello: Unit =
  val js:JSON = """[["one","two"], ["three", "four"], ["five", "six"], ["seven", "eight"], ["nine", "ten"]]""".asInstanceOf[JSON]

  val companionClass = Class.forName("scala.collection.immutable.List$")
  val companionInstance = companionClass.getField("MODULE$").get(companionClass)
  val builderMethod = companionClass.getMethod("newBuilder")

  val strCodec = new StringCodec()
  val subdecoder = new SeqDecoder(builderMethod, companionInstance, strCodec.decoder)
  val decoder = new SeqDecoder(builderMethod, companionInstance, subdecoder)
  JsonParser(js, decoder)
  val obj = decoder.getValue.asInstanceOf[List[List[String]]]
  println(obj)

  val encoder = new SeqEncoder[List[String],List[List[String]]](new SeqEncoder(strCodec.encoder))
  val writer = new JsonWriter()
  encoder.encode(obj, writer)
  println(writer.getValue)

  /*
  timeit(() => {
    val subdecoder = new SeqDecoder(builderMethod, companionInstance, new StringJsonDecoder())
    val decoder = new SeqDecoder(builderMethod, companionInstance, subdecoder)
    JsonParser(js, decoder)
    val obj:List[List[String]] = decoder.getValue
    val encoder = new SeqEncoder[List[String],List[List[String]]](new SeqEncoder(new StringJsonEncoder()))
    val writer = new JsonWriter()
    encoder.encode(obj, writer)
  }, "SJ2")

  timeit( ()=>{
    val obj = Json.decode(js.asInstanceOf[String].getBytes).to[List[List[String]]].value
    Json.encode(obj).toUtf8String
  }, "borer" )

  val sj = ScalaJack()
  timeit( () => {
    val obj = sj.read[List[List[String]]](js.asInstanceOf[co.blocke.scalajack.json.JSON])
    sj.render(obj)
  }, "ScalaJack" )
  */

