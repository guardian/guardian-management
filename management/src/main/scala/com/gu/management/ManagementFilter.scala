package com.gu.management

import javax.servlet.http.{ HttpServletResponse, HttpServletRequest }
import javax.servlet._

trait ManagementFilter extends AbstractHttpFilter with Loggable {
  lazy val version = ManagementBuildInfo.version

  override def init(filterConfig: FilterConfig) {
    logger.info("Management filter v%s initialised" format (version))
  }

  def doHttpFilter(httpReq: HttpServletRequest, httpResp: HttpServletResponse, chain: FilterChain) {
    val page = pagesWithIndex.find(_.dispatch.isDefinedAt(httpReq))

    if (page.isDefined) {
      page.foreach(_.dispatch(httpReq).writeTo(httpResp))
    } else {
      chain.doFilter(httpReq, httpResp)
    }
  }

  object IndexPage extends HtmlManagementPage {
    val path = "/management"
    val title = "Management Index"

    def body(r: HttpServletRequest) =
      <xml:group>
        <ul>
          { for (p <- pages) yield <li><a href={ p.url }>{ p.linktext }</a></li> }
        </ul>
        <hr/>
        <p><small>
             This page generated by<a href="http://github.com/guardian/guardian-management">guardian-management</a>{ version }
           </small></p>
      </xml:group>
  }

  lazy val pagesWithIndex = IndexPage :: pages

  /**
   * Implement this member with a list of the management pages
   * you want to include
   */
  val pages: List[ManagementPage]
}