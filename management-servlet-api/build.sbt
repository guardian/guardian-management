
libraryDependencies ++= Seq(
    "javax.servlet" % "servlet-api" % "2.4" % "provided",
    "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.1" exclude("javax.transaction", "jta"),
    "org.scalatest" %% "scalatest" % "1.9.1" % "test",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "net.liftweb" %% "lift-testkit" % "2.5-M4" % "test"
)


// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

seq(scalariformSettings: _*)
