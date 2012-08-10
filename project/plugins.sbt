resolvers ++= Seq(
  Classpaths.typesafeResolver,
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Web plugin repo" at "http://siasia.github.com/maven2",
  "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
)

addSbtPlugin("play" % "sbt-plugin" % "2.0.3")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0-M2-TYPESAFE")

addSbtPlugin("com.typesafe.sbtscalariform" % "sbtscalariform" % "0.4.0")

libraryDependencies += "com.github.siasia" %% "xsbt-web-plugin" % "0.11.2-0.2.11"
