name := "users"
version := "1.0.0"

scalaVersion := "2.12.3"
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-Ypartial-unification"
)

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

val http4sVersion = "0.21.24"

libraryDependencies ++= Seq(
  "com.softwaremill.quicklens" %% "quicklens" % "1.4.11",
  "org.typelevel"              %% "cats-core" % "1.0.0-MF",
  "org.http4s"                 %% "http4s-dsl" % http4sVersion,
  "org.http4s"                 %% "http4s-blaze-server" % http4sVersion,
  "org.http4s"                 %% "http4s-blaze-client" % http4sVersion,
  "org.http4s"                 %% "http4s-circe" % http4sVersion,
  "io.circe"                   %% "circe-generic" % "0.13.0",
  "io.circe"                   %% "circe-literal" % "0.13.0",
  "com.github.jwt-scala"       %% "jwt-circe" % "8.0.2",
  compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")
)
