package com.gu.management.internal

import org.specs2.mutable._
import com.gu.management.{ PlainTextResponse, Response, HttpRequest, ManagementPage }
import io.Source

class TestManagementServer extends Specification {

  step {
    val handler = new ManagementHandler {
      def pages: List[ManagementPage] = List(
        new ManagementPage {
          def get(req: HttpRequest): Response = new PlainTextResponse("response")
          val path: String = "/management/test"
        }
      )

      val applicationName: String = "test"
    }
    ManagementServer.start(handler)
  }

  sequential

  "management server" should {
    "bind to free port" in {
      ManagementServer.isRunning must beTrue
      ManagementServer.port() must be greaterThanOrEqualTo (18080)
      ManagementServer.port() must be lessThanOrEqualTo (18099)
    }
    "serve a management page" in {
      val port = ManagementServer.port()
      val response = Source.fromURL("http://localhost:%d/management/test" format port) mkString ""
      response must be equalTo ("response")
    }
  }

  step {
    ManagementServer.shutdown()
  }

}
