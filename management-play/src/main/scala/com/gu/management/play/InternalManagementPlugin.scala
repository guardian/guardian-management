package com.gu.management.play

import com.gu.management.{ Loggable, ManagementPage }
import com.gu.management.internal._
import play.api.{ Plugin, Application }

class InternalManagementPlugin(val app: Application) extends Plugin with Loggable {

  def registerPages(pages: List[ManagementPage]) {
    logger.debug("Registering new management pages")
    handlerPages = pages
  }

  def appName: Option[String] = Option(System.getProperty("GUapps"))

  val handler = new ManagementHandler {
    def pages = handlerPages
  }

  var handlerPages: List[ManagementPage] = Nil

  override def onStart() {
    logger.debug("Starting internal management server")
    println(app.configuration.keys.toList.sorted.mkString("\n"))
    ManagementServer.start(handler, appName)
  }
  override def onStop() {
    logger.debug("Shutting down management server")
    ManagementServer.shutdown()
  }
}
