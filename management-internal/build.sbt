resolvers ++= Seq(Classpaths.typesafeResolver)

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.4.6" % "test",
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3-1" exclude("javax.transaction", "jta")
)

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false
