val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "ls",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "io.bullet" % "borer-core_2.13" % "1.7.2",
      "co.blocke" %% "scalajack" % "7.0.1",
    	"com.novocode" % "junit-interface" % "0.11" % "test")
  )