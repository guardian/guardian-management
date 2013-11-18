

libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "0.9.27",
    "javax.servlet" % "servlet-api" % "2.4" % "provided"
)

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

seq(scalariformSettings: _*)
