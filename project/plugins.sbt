resolvers ++= Seq(
  Classpaths.typesafeResolver,
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Web plugin repo" at "http://siasia.github.com/maven2"
)

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.5")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.1")

addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "0.9.0")