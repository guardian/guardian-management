resolvers ++= Seq(
  Classpaths.typesafeResolver,
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Web plugin repo" at "http://siasia.github.com/maven2"
)

addSbtPlugin("play" % "sbt-plugin" % "2.1-RC2")

addSbtPlugin("com.typesafe.sbtscalariform" % "sbtscalariform" % "0.4.0")

libraryDependencies += "com.github.siasia" %% "xsbt-web-plugin" % "0.12.0-0.2.11.1"
