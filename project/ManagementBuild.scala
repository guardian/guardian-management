import com.typesafe.sbtscalariform.ScalariformPlugin
import org.sbtidea.SbtIdeaPlugin._
import sbt._
import sbt.PlayProject._

object ManagementBuild extends Build {

  lazy val root = Project("management-root", file(".")).aggregate(
    management,
    managementServletApi,
    managementPlay,
    managementInternal,
    managementLogback,
    managementMongo,
    exampleServletApi,
    examplePlay
  ).settings(ideaSettings: _*)

  lazy val management = managementProject("management")

  lazy val managementServletApi = managementProject("management-servlet-api") dependsOn (management)
  lazy val managementInternal = managementProject("management-internal") dependsOn (management)
  lazy val managementPlay = managementProject("management-play") dependsOn (management,managementInternal)
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
    mainLang = SCALA).settings(ideaSettings: _*) dependsOn (management, managementPlay, managementLogback)

  def managementProject(name: String) = Project(name, file(name)).settings(ideaSettings: _*).settings(ScalariformPlugin.scalariformSettings :_*)
}
