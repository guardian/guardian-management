import sbt._
import PlayProject._
import sbt.PlayProject._

object ManagementBuild extends Build {

  lazy val root = Project("root", file(".")) aggregate (
    management,
    managementServletApi,
    managementPlay,
    managementLogback,
    managementMongo,
    exampleServletApi,
    examplePlay
  )

  lazy val management = managementProject("management")

  lazy val managementServletApi = managementProject("management-servlet-api") dependsOn (management)
  lazy val managementPlay = managementProject("management-play") dependsOn (management)
  lazy val managementLogback = managementProject("management-logback") dependsOn (management)
  lazy val managementMongo = managementProject("management-mongo") dependsOn  (management)

  lazy val exampleServletApi = managementProject("example-servlet-api") dependsOn (
    management,
    managementServletApi,
    managementLogback
  )

  lazy val examplePlay = PlayProject(
    name = "example-play",
    applicationVersion = "1.0",
    dependencies = Nil,
    path = file("example-play"),
    mainLang = SCALA) dependsOn (management, managementPlay, managementLogback)

  def managementProject(name: String) = Project(name, file(name))
}