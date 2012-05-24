package com.gu.management.play

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import play.api.Play
import com.gu.management.internal.ManagementServer
import com.gu.management._
import io.Source

object TestManagement extends Management {
  val applicationName = "test-app"
  val pages = List(
    new ManagementPage {
      def get(req: HttpRequest): Response = new PlainTextResponse("response")
      val path: String = "/management/test"
    }
  )
}

class PluginTest extends Specification {

  "plugin" should {
    "be created" in {
      running(FakeApplication(
        additionalPlugins = Seq("com.gu.management.play.InternalManagementPlugin"),
        additionalConfiguration = Map("management.search.root" -> "com.gu.management.play")
      )) {
        Play.current.plugin[InternalManagementPlugin] must beSome
      }
    }
    "start management server" in {
      running(FakeApplication(
        additionalPlugins = Seq("com.gu.management.play.InternalManagementPlugin"),
        additionalConfiguration = Map("management.search.root" -> "com.gu.management.play")
      )) {
        ManagementServer.isRunning must beTrue
      }
    }
    "serve management page" in {
      running(FakeApplication(
        additionalPlugins = Seq("com.gu.management.play.InternalManagementPlugin"),
        additionalConfiguration = Map("management.search.root" -> "com.gu.management.play")
      )) {
        val port = ManagementServer.port()
        val response = Source.fromURL("http://localhost:%d/management/test" format port) mkString ""
        response must be equalTo ("response")
      }
    }
  }

}
