val scala3Version = "3.0.1"

val circeVersion = "0.15.0-M1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "parser-x",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    addCompilerPlugin("co.blocke" %% "scala-reflection" % "cachefix_ebe2e1"),

    libraryDependencies ++= Seq(
      "io.bullet" % "borer-core_2.13" % "1.7.2",
      "co.blocke" %% "scalajack" % "skip_d1b433",
      "co.blocke" %% "scala-reflection" % "cachefix_575eb0",//1.1.1",
    	"com.novocode" % "junit-interface" % "0.11" % "test"),

    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
      ).map(_ % circeVersion)
  )