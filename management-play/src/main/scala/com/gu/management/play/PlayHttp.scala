package com.gu.management.play

import play.api.mvc.{ Result, Results, Request }
import play.api.libs.json.Json
import net.liftweb.json.{ compact, render }
import com.gu.management._

object PlayHttpRequest {
  def apply[A](request: Request[A]): HttpRequest =
    HttpRequest(
      Method(request.method),
      request.path,
      request.requestURI,
      request.parameters
    )
}

case class PlayHttpResponse(results: Results) extends HttpResponse {
  var contentType: String = "text/html"
  var status: Int = 200
  var body: ResponseBody = _
  var result: Result = _

  def send() {
    var simpleResult = body match {
      case TextResponseBody(text) => results.Status(status)(text)
      case HtmlResponseBody(html) => results.Status(status)(html)
      case XmlResponseBody(xml) => results.Status(status)(xml)
      case JsonResponseBody(json) => results.Status(status)(Json.parse(compact(render(json))))
      case NoResponseBody => results.Status(status)
    }

    headers foreach { header =>
      simpleResult = simpleResult.withHeaders(header)
    }

    result = simpleResult as "%s; charset=%s".format(contentType, encoding)
  }

  def sendError(code: Int, message: String) {
    status = code
    contentType = "text/html"
    body = HtmlResponseBody(
      <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
          <title>Error { code } { message }</title>
        </head>
        <body>
          <h2>HTTP ERROR { code }</h2>
          <p>Reason:<pre>{ message }</pre></p>
          <hr/>
          <i><small>Powered by Play://</small></i>
          <br/>
        </body>
      </html>
    )
    send()
  }
}

