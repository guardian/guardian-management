
libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.6.1",
    "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
    "org.json4s" %% "json4s-jackson" % "3.5.2",
    "org.specs2" %% "specs2-core" % "2.4.17" % "test"
)

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

scalariformSettings
