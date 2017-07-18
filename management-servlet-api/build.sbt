
libraryDependencies ++= Seq(
    "javax.servlet" % "servlet-api" % "2.4" % "provided",
    "commons-codec" % "commons-codec" % "1.6" % "test",
    "org.specs2" %% "specs2-core" % "2.4.17" % "test",
    "org.scalatest" %% "scalatest" % "3.0.3" % "test",
    "org.mockito" % "mockito-core" % "1.9.5" % "test"
)

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

scalariformSettings
