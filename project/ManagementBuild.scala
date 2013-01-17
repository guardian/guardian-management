import com.typesafe.sbtscalariform.ScalariformPlugin
import sbt._
import sbt.Keys._
import sbt.PlayProject._

object ManagementBuild extends Build {

  implicit def project2noPublish(project: Project) = new {
    lazy val noPublish : sbt.Project = project.settings(publish := false)
  }



  lazy val root = Project("management-root", file(".")).aggregate(
    management,
    managementServletApi,
    managementPlay,
    managementInternal,
    managementLogback,
    managementMongo,
    exampleServletApi,
    examplePlay
  ).noPublish

  lazy val management = managementProject("management")

  lazy val managementServletApi = managementProject("management-servlet-api") dependsOn (management)
  lazy val managementInternal = managementProject("management-internal") dependsOn (management)
  lazy val managementPlay = managementProject("management-play") dependsOn (management,managementInternal)
  lazy val managementLogback = managementProject("management-logback") dependsOn (management)
  lazy val managementMongo = managementProject("management-mongo") dependsOn  (management)

  lazy val exampleServletApi = managementProject("example-servlet-api").dependsOn(
    management,
    managementServletApi,
    managementLogback
  ).noPublish

  lazy val examplePlay = play.Project(
    name = "example-play",
    applicationVersion = "1.0",
    dependencies = Nil,
    path = file("example-play")).
    dependsOn(management, managementPlay, managementLogback).
    noPublish

  def managementProject(name: String) = Project(name, file(name)).settings(ScalariformPlugin.scalariformSettings :_*)
}
