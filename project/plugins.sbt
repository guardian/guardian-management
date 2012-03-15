resolvers ++= Seq(
  Classpaths.typesafeResolver,
  "Web plugin repo" at "http://siasia.github.com/maven2",
  "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
)

addSbtPlugin("play" % "sbt-plugin" % "2.0")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")

addSbtPlugin("com.typesafe.sbtscalariform" % "sbtscalariform" % "0.3.0")

libraryDependencies += "com.github.siasia" %% "xsbt-web-plugin" % "0.11.2-0.2.11"