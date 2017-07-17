import com.typesafe.sbt.SbtScalariform
import sbt._
import sbt.Keys._
import play.Play.autoImport._

object ManagementBuild extends Build {

  implicit def project2noPublish(project: Project) = new {
    lazy val noPublish : sbt.Project = project.settings(publish := false)
  }

  lazy val root = Project("management-root", file(".")).aggregate(
    management,
    managementServletApi,
    managementInternal,
    managementLogback,
    exampleServletApi
  ).noPublish

  lazy val management = managementProject("management")

  lazy val managementServletApi = managementProject("management-servlet-api") dependsOn (management)
  lazy val managementInternal = managementProject("management-internal") dependsOn (management)
  lazy val managementLogback = managementProject("management-logback") dependsOn (management)

  lazy val exampleServletApi = managementProject("example-servlet-api").dependsOn(
    management,
    managementServletApi,
    managementLogback
  ).noPublish

  def managementProject(name: String) = Project(name, file(name)).settings(SbtScalariform.scalariformSettings :_*)
}
