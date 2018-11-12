import Dependencies._


lazy val searchEngine = project
  .in(file("."))
  .settings(
    scalaVersion := "2.12.6",
    scalacOptions in Compile ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint")
  ).settings(
    libraryDependencies ++= akkaDependencies,
    libraryDependencies ++= akkaHttpDependencies,
    libraryDependencies ++= scalajDependencies,
    libraryDependencies ++= specs2Dependencies
  ).settings(
    scalacOptions in Test ++= Seq("-Yrangepos"),
    Compile / run / fork := true
  )