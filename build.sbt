val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "parser-x",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    addCompilerPlugin("co.blocke" %% "scala-reflection" % "cachefix_575eb0"),

    libraryDependencies ++= Seq(
      "io.bullet" % "borer-core_2.13" % "1.7.2",
      "co.blocke" %% "scalajack" % "7.0.1",
      "co.blocke" %% "scala-reflection" % "cachefix_575eb0",//1.1.1",
    	"com.novocode" % "junit-interface" % "0.11" % "test")
  )