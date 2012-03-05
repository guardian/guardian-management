package com.gu.management

import io.Source

class ManifestPage extends ManagementPage {
  val path = "/management/manifest"

  def get(req: HttpRequest) = response

  lazy val response = PlainTextResponse(
    Manifest.asStringOpt.getOrElse("Could not find version.txt on classpath.  Did you include the sbt-version-info-plugin?")
  )
}

object Manifest {
  lazy val asStreamOpt = Option(getClass.getResourceAsStream("/version.txt")).map(Source.fromInputStream(_))
  lazy val asStringOpt = asStreamOpt.map(_.mkString)
  lazy val asString = asStringOpt getOrElse ""
  lazy val asList = asStringOpt map { _.split("\n").toList } getOrElse Nil
  lazy val asKeyValuePairs = (asList map { _.split(":") } collect { case Array(k, v) => k -> v }).toMap
}
