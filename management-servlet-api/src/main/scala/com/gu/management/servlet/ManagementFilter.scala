package com.gu.management.servlet

import com.gu.management.{ ManagementPage, IndexPage, ManagementBuildInfo, Loggable }
import javax.servlet._
import javax.servlet.http.{ HttpServletResponse, HttpServletRequest }

trait ManagementFilter extends AbstractHttpFilter with Loggable {
  lazy val version = ManagementBuildInfo.version

  override def init(filterConfig: FilterConfig) {
    logger.info("Management filter v%s initialised" format version)
  }

  def doHttpFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
    val httpRequest = ServletHttpRequest(request)
    val httpResponse = ServletHttpResponse(response)

    val page = pagesWithIndex find { _.canDispatch(httpRequest) }
    page match {
      case Some(page) => page.dispatch(httpRequest).sendTo(httpResponse)
      case _ => chain.doFilter(request, response)
    }
  }

  lazy val pagesWithIndex = IndexPage(pages, applicationName, version) :: pages

  /**
   * Implement these members with the application name and a list of the
   * management pages you want to include
   */
  val applicationName: String
  val pages: List[ManagementPage]
}
