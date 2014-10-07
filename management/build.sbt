
libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.6.1",
    "org.scala-lang.modules" %% "scala-xml" % "1.0.2",
    "org.json4s" %% "json4s-jackson" % "3.2.9",
    "org.specs2" %% "specs2" % "2.4.6" % "test"
)

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

seq(scalariformSettings: _*)
