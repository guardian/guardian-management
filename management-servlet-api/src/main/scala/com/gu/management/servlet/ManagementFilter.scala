package com.gu.management.servlet

import com.gu.management.{ ManagementPage, IndexPage, ManagementBuildInfo, Loggable, UserProvider, UserCredentials }
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
      case Some(page) if page.needsAuth => request.getHeaderOption("Authorization") match {
        case Some(authString) if userProvider.isValid(extractCredentials(authString)) => {
          page.dispatch(httpRequest).sendTo(httpResponse)
        }
        case _ => response.sendNeedsAuthorisation(userProvider.realm)
      }
      case Some(page) => page.dispatch(httpRequest).sendTo(httpResponse)
      case _ => chain.doFilter(request, response)
    }
  }

  lazy val pagesWithIndex = IndexPage(pages, applicationName, version) :: pages

  private def extractCredentials(authString: String) = {
    // authstring consists of "Basic Base64(user:pass)".
    authString.drop(6).base64Decoded.kv(":") match {
      case (user, pass) => UserCredentials(user, pass)
      case _ => UserCredentials.missing
    }
  }

  /**
   * Implement these members with the application name and a list of the
   * management pages you want to include
   */
  val applicationName: String
  val pages: List[ManagementPage]
  def userProvider: UserProvider
}
