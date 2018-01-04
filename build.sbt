import sbt.Keys._

scalaVersion in ThisBuild := "2.12.3"

name in ThisBuild := "sprite"

organization in ThisBuild := "ai.dragonfly.code"

version in ThisBuild := "0.1"

resolvers in ThisBuild += "dragonfly.ai" at "http://code.dragonfly.ai:8080/"

publishTo in ThisBuild := Some(Resolver.file("file",  new File( "/var/www/maven" )) )

val sprite = crossProject.settings(
  // shared settings
  libraryDependencies ++= Seq(
    "ai.dragonfly.code" %%% "img" % "0.1",
  )
).jsSettings(
  // JS-specific settings here
  jsDependencies += RuntimeDOM
).jvmSettings(
  // JVM-specific settings here
  libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"
)

lazy val js = sprite.js

lazy val jvm = sprite.jvm
