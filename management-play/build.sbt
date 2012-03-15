
libraryDependencies ++= Seq(
    "play" %% "play" % "2.0",
    "org.specs2" %% "specs2" % "1.5" % "test",
    "play" %% "play-test" % "2.0" % "test"
)

// needed for Play
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

seq(scalariformSettings: _*)