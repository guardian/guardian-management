package com.gu.management.play

import com.gu.management.ManagementPage
import com.gu.management.internal._
import org.reflections.Reflections
import scala.collection.JavaConversions._
import play.api.{ Logger, Play, Plugin, Application }

trait Management {
  val applicationName: String
  val pages: List[ManagementPage]
}

class InternalManagementPlugin(val app: Application) extends Plugin {

  implicit val log = Logger(getClass)

  override def onStart() {
    log.debug("Registering management pages")

    val searchRoot = app.getConfigurationProperty("management.search.root", "conf")
    val classes = classOf[Management].subTypesFrom(searchRoot).toList

    classes match {
      case Nil =>
        log.error("Can't start management server, no subtype of com.gu.management.play.Management found in package %s." format searchRoot)
        throw new RuntimeException("No management subtype found")

      case _ if classes.size > 1 =>
        log.error("Not starting management server, multiple subtypes of com.gu.management.play.Management found in package %s." format searchRoot)
        throw new RuntimeException("No management subtype found")

      //      case List(managementClass: Class[_ <: Management]) =>
      case List(managementClass) =>
        val management = managementClass.getField("MODULE$").get(null).asInstanceOf[Management]
        val handler = new ManagementHandler {
          val applicationName = management.applicationName
          val pages = management.pages
        }

        log.debug("Starting internal management server")
        ManagementServer.start(handler)
    }
  }

  override def onStop() {
    log.debug("Shutting down management server")
    ManagementServer.shutdown()
  }
}
