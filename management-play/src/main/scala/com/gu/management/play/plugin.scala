package com.gu.management.play

import com.gu.management.{ Loggable, ManagementPage }
import com.gu.management.internal._
import play.api.{ Play, Plugin, Application }

trait ManagementPageManifest {
  val pages: List[ManagementPage]
  val applicationName: String
  def handler = new ManagementHandler {
    def pages = ManagementPageManifest.this.pages
    val applicationName = ManagementPageManifest.this.applicationName
  }
}

object CompanionReflector {
  def companion[T](name: String)(implicit man: Manifest[T]): T =
    Class.forName(name + "$").getField("MODULE$").get(man.erasure).asInstanceOf[T]
}

class InternalManagementPlugin(val app: Application) extends Plugin with Loggable {

  lazy val appName: String = app.configuration.getString("application.name").get
  var handlerPages: List[ManagementPage] = Nil

  def registerPages(pages: List[ManagementPage]) {
    logger.debug("Registering new management pages")
    handlerPages = pages
  }

  override def onStart() {
    logger.debug("Registering management pages")
    try {
      val pageManifestClassName = app.configuration.getString("management.manifestobject").getOrElse("conf.Management")
      val pageManifest: ManagementPageManifest = CompanionReflector.companion[ManagementPageManifest](pageManifestClassName)
      val handler = pageManifest.handler
      logger.debug("Starting internal management server")
      ManagementServer.start(handler)
    } catch {
      case e: Exception => logger.error("Failed to start management server", e)
    }
  }

  override def onStop() {
    logger.debug("Shutting down management server")
    ManagementServer.shutdown()
  }
}
