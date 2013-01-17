
libraryDependencies ++= Seq(
    "play" %% "play" % "2.1-RC2",
    "org.reflections" % "reflections" % "0.9.8",
    "org.specs2" %% "specs2" % "1.5" % "test",
    "play" %% "play-test" % "2.1-RC2" % "test"
)

// needed for Play
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

// sad situation https://github.com/jesseeichar/scala-io/issues/77#issuecomment-11991815
moduleConfigurations += ModuleConfiguration("com.github.scala-incubator.io", DefaultMavenRepository)

moduleConfigurations += ModuleConfiguration("com.jsuereth", DefaultMavenRepository)

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

seq(scalariformSettings: _*)
