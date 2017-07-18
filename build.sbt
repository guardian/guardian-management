organization in ThisBuild := "com.gu"

scalaVersion in ThisBuild := "2.12.2"

crossScalaVersions in ThisBuild := Seq("2.12.2", "2.11.9")

scalacOptions in ThisBuild += "-deprecation"

publishArtifact := false

homepage in ThisBuild := Some(url("https://github.com/guardian/guardian-management/"))
licenses in ThisBuild := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))
publishMavenStyle in ThisBuild := true
publishArtifact in Test in ThisBuild := false
scmInfo in ThisBuild := Some(ScmInfo(
  url("https://github.com/guardian/guardian-management"),
  "scm:git:git@github.com:guardian/guardian-management.git"
))

pomExtra in ThisBuild := {
  <developers>
    <developer>
      <id>philwills</id>
      <name>Phil Wills</name>
      <url>https://github.com/philwills</url>
    </developer>
  </developers>
}

import ReleaseTransformations._

releaseCrossBuild in ThisBuild := true

releaseProcess in ThisBuild := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = true),
  pushChanges
)

