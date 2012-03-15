import java.util.jar._

crossScalaVersions in ThisBuild := Seq("2.9.1")

// doing "in ThisBuild" makes this default setting for all projects in this build
version in ThisBuild := "5.7-SNAPSHOT"

organization in ThisBuild := "com.gu"

publishArtifact := false

packageOptions in ThisBuild <+= (version, name) map { (v, n) =>
  Package.ManifestAttributes(
    Attributes.Name.IMPLEMENTATION_VERSION -> v,
    Attributes.Name.IMPLEMENTATION_TITLE -> n,
    Attributes.Name.IMPLEMENTATION_VENDOR -> "guardian.co.uk"
  )
}

publishTo in ThisBuild <<= (version) { version: String =>
    val publishType = if (version.endsWith("SNAPSHOT")) "snapshots" else "releases"
    Some(
        Resolver.file(
            "guardian github " + publishType,
            file(System.getProperty("user.home") + "/guardian.github.com/maven/repo-" + publishType)
        )
    )
}


scalacOptions in ThisBuild += "-deprecation"

