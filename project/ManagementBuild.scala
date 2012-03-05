import sbt._

object ManagementBuild extends Build {
  lazy val root = Project("root", file(".")) aggregate(
    management,
    managementServletApi,
    managementLogback,
    managementMongo,
    exampleServletApi
  )

  lazy val management = managementProject("management")

  lazy val managementServletApi = managementProject("management-servlet-api") dependsOn (management)
  lazy val managementLogback = managementProject("management-logback") dependsOn (management)
  lazy val managementMongo = managementProject("management-mongo") dependsOn  (management)

  lazy val exampleServletApi = managementProject("example-servlet-api") dependsOn (management, managementServletApi, managementLogback)

  def managementProject(name: String) = Project(name, file(name))

}
