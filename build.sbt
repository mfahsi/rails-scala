
scalaVersion :="3.1.1"

name := "rails"
organization := "winova"
version := "1.0"

//scalacOptions := Seq("-target:jvm-1.11")

// Java then Scala for main sources
Compile / compileOrder := CompileOrder.JavaThenScala
testOptions += Tests.Argument(TestFrameworks.JUnit /*--tests=<REGEXPS>*/)

val localRepo = Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

lazy val ITest = config("it") extend Test

libraryDependencies ++= {
  Seq(
    "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1",
    "org.scalatest" %% "scalatest" % "3.2.14" % Test,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
    "ch.qos.logback" % "logback-classic" % "1.2.10",
    "com.typesafe"   % "config"  % "1.4.1"
  )
}

val cats_effect         = "org.typelevel" %% "cats-effect" % "2.9.0"
val cats_core           = "org.typelevel" %% "cats-core" % "2.9.0"



val java_junit                    =  "com.github.sbt" % "junit-interface" % "0.13.2" % Test
val java_junit_interface          =  "com.novocode" % "junit-interface" % "0.11" % Test

val extraDependencies = Seq(java_junit)

libraryDependencies ++= extraDependencies

//libraryDependencies ++= "com.novocode" % "junit-interface" % "0.11" % "test"

// lazy val root = (project in file(".")).
//   settings(
//     inThisBuild(List(
//       organization := "winova",
//       scalaVersion := "3.1.1"
//     )),
//     name := "hello-world"
//   )
