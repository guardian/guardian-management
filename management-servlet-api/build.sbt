
libraryDependencies ++= Seq(
    "javax.servlet" % "servlet-api" % "2.4" % "provided",
    "org.specs2" %% "specs2" % "1.5" % "test",
    "net.liftweb" %% "lift-testkit" % "2.4" % "test"
)

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

seq(scalariformSettings: _*)