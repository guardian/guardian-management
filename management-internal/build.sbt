resolvers ++= Seq(Classpaths.typesafeResolver)

libraryDependencies ++= Seq(
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.1" exclude("javax.transaction", "jta")
)

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false
