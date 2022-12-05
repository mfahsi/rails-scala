
scalaVersion :="3.1.1"

name := "rails"
organization := "winova"
version := "1.0"


libraryDependencies ++= {
  Seq(
    "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1",
    "org.scalatest" %% "scalatest" % "3.2.14" % Test,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
    "ch.qos.logback" % "logback-classic" % "1.2.10",
    "com.typesafe"   % "config"  % "1.4.1"
  )
}



