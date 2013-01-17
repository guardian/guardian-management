resolvers ++= Seq(Classpaths.typesafeResolver, ScalaToolsReleases)

libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "0.9.27",
    "org.mongodb" % "mongo-java-driver" % "2.7.3",
    "org.mongodb" % "casbah-core_2.9.2" % "2.4.1"
)

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

seq(scalariformSettings: _*)
