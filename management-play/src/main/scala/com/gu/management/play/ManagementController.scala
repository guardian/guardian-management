package com.gu.management.play

import _root_.play.api.mvc.{ Action, Controller }
import com.gu.management._

trait ManagementController extends Controller with Loggable {
  lazy val version = ManagementBuildInfo.version

  logger.info("Management controller v%s initialised" format version)

  def apply(path: String) = Action { request =>
    request match {
      case _ if request.path.endsWith("/") =>
        Redirect(request.path.replaceAll("/$", ""))

      case _ =>
        val httpRequest = PlayHttpRequest(request)
        val httpResponse = PlayHttpResponse(this)

        val page = pagesWithIndex find { _ canDispatch httpRequest }
        val dispatched = page map { _ dispatch httpRequest } getOrElse {
          ErrorResponse(404, "No management page for: " + httpRequest.path)
        }

        dispatched to httpResponse

        httpResponse.result
    }
  }

  lazy val pagesWithIndex = IndexPage(pages, version) :: pages

  /**
   * Implement this member with a list of the management pages
   * you want to include
   */
  val pages: List[ManagementPage]
}
