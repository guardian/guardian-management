
libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.6.1",
    "net.liftweb" %% "lift-json" % "2.4",
    "org.specs2" %% "specs2" % "1.5" % "test",
    "net.liftweb" %% "lift-testkit" % "2.4" % "test"
)

libraryDependencies <<= (scalaVersion, libraryDependencies) { (sv, deps) =>
  deps :+ ( sv match {
    case "2.8.1" => "com.github.scala-incubator.io" %% "core" % "0.1.1"
    case "2.9.0-1" => "com.github.scala-incubator.io" %% "core" % "0.1.2"
    case "2.9.1" => "com.github.scala-incubator.io" %% "scala-io-core" % "0.2.0"
  } )
}

// needed for specs2
resolvers += ScalaToolsSnapshots

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

seq(scalariformSettings: _*)