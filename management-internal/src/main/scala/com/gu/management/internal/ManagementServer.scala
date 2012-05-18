package com.gu.management.internal

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import com.gu.management._
import java.net.{BindException, InetSocketAddress}
import java.io.File
import sbt._

object ManagementServer extends Loggable {
  val managementPort = 18080
  val managementLimit = 18099
  var server: Option[HttpServer] = None
  var portFile: Option[File] = None

  def start(handler: ManagementHandler, appName: Option[String] = None) {
    if (server.isEmpty) {
      server = startServer(managementPort, handler)
      server.foreach { realServer =>
        portFile = PortFile.createFile(appName,realServer.getAddress.getPort)
      }
    }
  }

  private def startServer(port:Int, handler:ManagementHandler):Option[HttpServer] = {
    if ( managementPort to managementLimit contains port ) {
      synchronized {
        try {
          val server = HttpServer.create(new InetSocketAddress(port), 10)
          server.createContext("/", handler)
          server.setExecutor(null)
          server.start()
          Some(server)
        } catch {
          case e:BindException => {
            startServer(port + 1, handler)
          }
        }
      }
    } else {
      None
    }
  }

  def shutdown() {
    synchronized{
      server.foreach{ _.stop(0) }
      server = None
      portFile.foreach { PortFile.deleteFile(_) }
    }
  }
}

object PortFile extends Loggable {
  val portFileRoot="/var/run/ports/"
  def createFile(appName: Option[String], port: Int): Option[File] = {
    val file = new File(portFileRoot + appName.getOrElse(port.toString) + ".port")
    try {
      IO.write(file, port.toString, append=false)
      Some(file)
    } catch {
      case _ => None
    }
  }
  def deleteFile(file: File) {
    IO.delete(file)
  }
}

trait ManagementHandler extends HttpHandler with Loggable {
  lazy val version = ManagementBuildInfo.version

  def handle(httpExchange: HttpExchange) {
    try {
      logger.debug("Entered handler for "+httpExchange.getRequestURI.toString)
      val httpRequest = SunHttpRequest(httpExchange)
      val httpResponse = SunHttpResponse(httpExchange)
      logger.debug("Handling request for "+httpRequest)

      val response = httpRequest match {
        case request if request.path == "/" => {
          RedirectResponse(request.requestURI + "management")
        }
        case request if request.requestURI.endsWith("/") => {
          RedirectResponse(request.requestURI.replaceAll("/$",""))
        }
        case request => {
          val page = pagesWithIndex find { _ canDispatch request }
          logger.debug("Serving page: "+page.getOrElse("none"))
          page map { _ dispatch httpRequest } getOrElse {
            ErrorResponse(404, "No management page for: " + request.path)
          }
        }
      }

      response to httpResponse
    } catch {
      case e => {
        logger.error("Caught an exception whilst handling internal management page request",e)
      }
    }
  }

  lazy val pagesWithIndex = IndexPage(pages, version) :: pages

  /**
   * Implement this member with a list of the management pages
   * you want to include
   */
  def pages: List[ManagementPage]

}