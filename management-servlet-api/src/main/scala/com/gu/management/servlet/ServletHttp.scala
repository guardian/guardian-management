package com.gu.management.servlet

import com.gu.management._
import javax.servlet.http.{ HttpServletRequest, HttpServletResponse }

object ServletHttpRequest {
  def apply(servletRequest: HttpServletRequest): HttpRequest =
    HttpRequest(
      Method(servletRequest.getMethod),
      servletRequest.path,
      servletRequest.getRequestURI,
      servletRequest.parameters
    )
}

case class ServletHttpResponse(httpServletResponse: HttpServletResponse) extends HttpResponse {
  var contentType: String = "text/html"
  var status: Int = 200
  var body: ResponseBody = NoResponseBody

  def send() {
    httpServletResponse setCharacterEncoding encoding
    httpServletResponse setContentType contentType
    headers foreach {
      case (name, value) =>
        httpServletResponse.setHeader(name, value)
    }

    httpServletResponse setStatus status
    httpServletResponse.getWriter.println(body.toText)
  }

  def sendError(code: Int, message: String) {
    httpServletResponse.sendError(code, message)
  }
}
