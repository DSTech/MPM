logLevel := Level.Warn

resolvers += Resolver.sonatypeRepo("releases")
libraryDependencies in ThisBuild <+= scalaVersion("org.scala-lang" % "scala-reflect" % _)
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
