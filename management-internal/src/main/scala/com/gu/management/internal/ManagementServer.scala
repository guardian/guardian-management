package com.gu.management.internal

import com.sun.net.httpserver.{ HttpExchange, HttpHandler, HttpServer }
import com.gu.management._
import java.net.{ BindException, InetSocketAddress }
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.util

object ManagementServer extends Loggable with PortFileHandling {
  val permittedPorts = 18080 to 18099
  private var server: Option[HttpServer] = None

  def start(handler: ManagementHandler): Boolean =
    withServerStopped(ifRunning = false) {
      permittedPorts.takeWhile(!start(handler, _))
      isRunning
    }

  def start(handler: ManagementHandler, port: Int): Boolean =
    withServerStopped(ifRunning = false) {
      startServer(port, handler)
        .fold(err => { logger.warn(err); false }, ok => { logger.info(ok); true })
    }

  def isRunning: Boolean = server.isDefined
  def port: Int = server.get.getAddress.getPort

  private def startServer(bindToPort: Int, handler: ManagementHandler): Either[String, String] = {
    def launchServer() = {
      val srv = HttpServer.create(new InetSocketAddress(bindToPort), 10)
      srv.createContext("/", handler)
      srv.setExecutor(null)
      srv.start()
      srv
    }

    synchronized {
      withServerStopped[Either[String, String]](ifRunning = (msg: String) => Left(msg)) {
        try {
          val newServer = launchServer()
          createPortFile(handler.applicationName, newServer.getAddress.getPort)
          server = Some(newServer)
          Right(s"Management server started on port $bindToPort")
        } catch {
          case e: BindException => Left(s"Cannot bind port $bindToPort. Already in use.")
        }
      }
    }
  }

  def shutdown() {
    synchronized {
      server.foreach { _.stop(0) }
      server = None
      deletePortFile()
    }
  }

  private def withServerStopped[T](ifRunning: T)(block: => T): T =
    withServerStopped[T]((_: String) => ifRunning)(block)

  private def withServerStopped[T](ifRunning: String => T)(block: => T): T = {
    if (isRunning) {
      val msg = s"Management server already started. Running on port $port"
      logger.warn(msg)
      ifRunning(msg)
    } else {
      block
    }
  }
}

trait PortFileHandling extends Loggable {
  val portFileRoot = "/var/run/ports/"
  private var portFile: Option[File] = None
  def createPortFile(appName: String, port: Int): Boolean = {
    val file = new File(portFileRoot + appName + ".port")
    try {
      Files.write(file.toPath, util.Arrays.asList(port.toString), Charset.defaultCharset())
      portFile = Some(file)
      true
    } catch {
      case t: Throwable =>
        logger.warn("Could not create management port file at " + file)
        false
    }
  }
  def deletePortFile() {
    portFile.foreach(f => Files.delete(f.toPath))
    portFile = None
  }
}

trait ManagementHandler extends HttpHandler with Loggable {
  lazy val version = ManagementBuildInfo.version

  def handle(httpExchange: HttpExchange) {
    try {
      logger.debug("Entered handler for " + httpExchange.getRequestURI.toString)
      val httpRequest = SunHttpRequest(httpExchange)
      val httpResponse = SunHttpResponse(httpExchange)
      logger.debug("Handling request for " + httpRequest)

      val response = httpRequest match {
        case request if request.path == "/" =>
          RedirectResponse(request.requestURI + "management")

        case request if request.requestURI.endsWith("/") =>
          RedirectResponse(request.requestURI.replaceAll("/$", ""))

        case request =>
          val page = pagesWithIndex find { _ canDispatch request }
          logger.debug("Serving page: " + page.getOrElse("none"))
          try {
            page map { _ dispatch httpRequest } getOrElse {
              ErrorResponse(404, "No management page for: " + request.path)
            }
          } catch {
            case e: Exception =>
              val sw = new StringWriter()
              e.printStackTrace(new PrintWriter(sw))
              ErrorResponse(500, "Exception thrown whilst handling internal management page request:\n\n%s\n    %s" format (e.toString, sw.toString.replace("\n", "\n    ")))
          }
      }

      response to httpResponse
    } catch {
      case e: Throwable => {
        logger.error("Caught an exception whilst handling internal management page request", e)
      }
    }
  }

  lazy val pagesWithIndex = IndexPage(pages, applicationName, version) :: pages

  /**
   * Implement these members with an application name and list of the
   * management pages you want to include
   */
  val applicationName: String
  def pages: List[ManagementPage]

}
