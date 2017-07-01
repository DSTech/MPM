import sbt._

name := "MPM"

version := "1.0"

scalaOrganization := "org.typelevel"
scalaVersion := "2.12.2-bin-typelevel-4"

resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += scalaOrganization.value % "scala-reflect" % scalaVersion.value % "provided"
scalacOptions ++= Seq("-Yinduction-heuristics", "-Ykind-polymorphism", "-Yliteral-types", "-Xstrict-patmat-analysis", "-Xexperimental")

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.3",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test"
)

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.5"


//libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.2"
libraryDependencies += "io.argonaut" %% "argonaut" % "6.2"
libraryDependencies += "io.argonaut" %% "argonaut-scalaz" % "6.2"
libraryDependencies += "io.argonaut" %% "argonaut-monocle" % "6.2"

val monocleVersion = "1.4.0"

libraryDependencies ++= Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"    % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-generic" % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-macro"   % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-state"   % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-refined" % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-law"     % monocleVersion % "test"
)
libraryDependencies += "com.github.kenbot" %% "goggles-dsl" % "1.0"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.14"
libraryDependencies += "com.lihaoyi" %% "upickle" % "0.4.4"
libraryDependencies += "org.scala-graph" %% "graph-core" % "1.11.5"
libraryDependencies += "io.reactivex" %% "rxscala" % "0.26.5"
