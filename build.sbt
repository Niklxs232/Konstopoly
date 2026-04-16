ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "Konstopoly",
    idePackagePrefix := Some("de.konstopoly"),
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
      "org.scalactic"          %% "scalactic"   % "3.2.18",
      "org.scalatest"          %% "scalatest"   % "3.2.18" % Test
    )
  )