resolvers ++= Seq(Classpaths.typesafeResolver)

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "org.specs2" %% "specs2-core" % "2.4.17" % "test"
)

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false
