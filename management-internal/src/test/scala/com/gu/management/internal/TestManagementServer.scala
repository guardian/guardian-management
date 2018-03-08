package com.gu.management.internal

import org.specs2.mutable._
import com.gu.management.{ PlainTextResponse, Response, HttpRequest, ManagementPage }
import scala.io.Source

class TestManagementServer extends Specification {

  val handler = new ManagementHandler {
    val applicationName: String = "test"
    def pages: List[ManagementPage] = List(
      new ManagementPage {
        def get(req: HttpRequest): Response = new PlainTextResponse("response")
        val path: String = "/management/test"
      }
    )
  }

  sequential

  "management server" should {
    "bind to free port" in new Context {
      ManagementServer.start(handler)
      ManagementServer.isRunning must beTrue
      ManagementServer.port must be greaterThanOrEqualTo (18080)
      ManagementServer.port must be lessThanOrEqualTo (18099)
    }
    "serve a management page" in new Context {
      ManagementServer.start(handler)
      val port = ManagementServer.port
      val response = Source.fromURL("http://localhost:%d/management/test" format port) mkString ""
      response must be equalTo ("response")
    }

    "optionally bind to a fixed port" in new Context {
      ManagementServer.start(handler, 18000)
      ManagementServer.isRunning must beTrue
      ManagementServer.port must be equalTo (18000)
    }
  }

  trait Context extends BeforeAfter {
    def before: Any = {}
    def after: Any = ManagementServer.shutdown()
  }

}
