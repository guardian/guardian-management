
libraryDependencies ++= Seq(
    "javax.servlet" % "servlet-api" % "2.4" % "provided",
    "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.3-1" exclude("javax.transaction", "jta"),
    "commons-codec" % "commons-codec" % "1.6" % "test",
    "org.specs2" %% "specs2" % "2.4.6" % "test",
    "org.scalatest" %% "scalatest" % "2.2.2" % "test",
    "org.mockito" % "mockito-core" % "1.9.5" % "test"
)

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

seq(scalariformSettings: _*)
