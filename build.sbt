import sbt._

name := "MPM"

version := "1.0"

scalaVersion := "2.11.8"
resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies in ThisBuild <+= scalaVersion("org.scala-lang" % "scala-reflect" % _)

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)


//libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.2"
libraryDependencies += "io.argonaut" %% "argonaut" % "6.2-M1"
libraryDependencies += "io.argonaut" %% "argonaut-scalaz" % "6.2-M1"
libraryDependencies += "io.argonaut" %% "argonaut-monocle" % "6.2-M1"

val monocleVersion = "1.3.1"

libraryDependencies ++= Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"    % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-generic" % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-macro"   % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-state"   % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-refined" % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-law"     % monocleVersion % "test"
)

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

// for @Lenses macro support
addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)

libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.3"
libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.6"
libraryDependencies += "org.scala-lang.modules" %% "scala-pickling" % "0.10.1"
libraryDependencies += "com.gilt" %% "gfc-semver" % "0.0.3"
libraryDependencies += "org.scala-graph" %% "graph-core" % "1.11.2"
libraryDependencies += "io.reactivex" %% "rxscala" % "0.26.3"
