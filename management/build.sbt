
libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.6.1",
    "net.liftweb" %% "lift-json" % "2.5-M4",
    "org.specs2" %% "specs2" % "1.13" % "test",
    "net.liftweb" %% "lift-testkit" % "2.5-M4" % "test"
)

// needed for specs2
resolvers += ScalaToolsSnapshots

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

seq(scalariformSettings: _*)
