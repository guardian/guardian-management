resolvers ++= Seq(Classpaths.typesafeResolver, ScalaToolsReleases)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.5" % "test",
  "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.1" exclude("javax.transaction", "jta"),
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.1"
)

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false
