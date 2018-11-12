import sbt._

object Dependencies {

  val akkaVersion = "2.5.17"
  val akkaDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion
  )
  val scalajDependencies = Seq(
    "org.scalaj" %% "scalaj-http" % "2.4.1"
  )

  val akkaHttpVersion = "10.1.5"
  val akkaHttpDependencies = Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test
  )

  val specs2Dependencies = Seq(
    "org.specs2" %% "specs2-core" % "4.3.4" % Test,
    "org.specs2" %% "specs2-mock" % "4.3.4" % Test
  )
}
