resolvers ++= Seq(Classpaths.typesafeResolver)

libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "0.9.27",
    "org.mongodb" %% "casbah-core" % "2.7.3"
)

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

seq(scalariformSettings: _*)
