resolvers ++= Seq(Classpaths.typesafeResolver)

libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.0.13",
    "org.mongodb" % "mongo-java-driver" % "2.11.3",
    "org.mongodb" %% "casbah-core" % "2.6.4"
)

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

seq(scalariformSettings: _*)
