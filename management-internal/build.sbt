resolvers ++= Seq(Classpaths.typesafeResolver, ScalaToolsReleases)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.5" % "test",
  "org.scala-tools.sbt" %% "io" % "0.11.2"
)

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false