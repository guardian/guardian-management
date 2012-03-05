package com.gu.management

import xml.NodeSeq
import net.liftweb.json.Extraction

trait ManagementPage {
  /**
   * The path to this page. You should include the full
   * servlet path including /management
   */
  val path: String

  lazy val url = path.dropWhile('/' ==)
  lazy val linktext = path

  /**
   * Process a get request to this page
   */
  def get(req: HttpRequest): Response

  // You probably don't need to override this one unless you're doing something
  // very funky.
  def dispatch: PartialFunction[HttpRequest, Response] = {
    case r @ HttpRequest(GET, p, _, _) if p equalsIgnoreCase path => get(r)
  }

  def canDispatch(request: HttpRequest): Boolean = dispatch isDefinedAt request
}

/**
 * Mixin this trait if you want to support posting to your management page
 */
trait Postable extends ManagementPage {
  def post(request: HttpRequest)

  override def dispatch = super.dispatch orElse {
    case r @ HttpRequest(POST, p, requestURI, _) if p equalsIgnoreCase path =>
      post(r)
      RedirectResponse(requestURI)
  }
}

trait JsonManagementPage extends ManagementPage {
  implicit val formats = net.liftweb.json.DefaultFormats

  def get(request: HttpRequest) = JsonResponse(Extraction.decompose(jsonObj))

  def jsonObj: Any
}

trait HtmlManagementPage extends ManagementPage {
  final def get(request: HttpRequest) = HtmlResponse(
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>{ title }</title>
      </head>
      <body>
        <h2>{ title }</h2>
        { body(request) }
      </body>
    </html>)

  def title: String
  def body(request: HttpRequest): NodeSeq
}

