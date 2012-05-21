package com.gu.management.internal

import com.gu.management._
import scala.collection.JavaConversions._
import java.nio.charset.Charset
import com.sun.net.httpserver.{ HttpsServer, HttpServer, HttpExchange }

object SunHttpRequest {
  def apply(exchange: HttpExchange): HttpRequest = {
    val uriQueryStringRegex = """\?.*$"""
    val protocol = exchange.getURIProtocol
    val host = exchange.getRequestHeaders.get("Host").head
    val path = exchange.getRequestURI.getPath.toString.replaceAll(uriQueryStringRegex, "")
    HttpRequest(
      Method(exchange.getRequestMethod),
      path,
      "%s://%s%s".format(protocol, host, path),
      exchange.getParameters
    )
  }
}

case class SunHttpResponse(exchange: HttpExchange) extends HttpResponse with Loggable {
  var contentType = "text/html"
  var status = 200

  def send() {
    if (body.isDefined)
      headers += ("Content-Type" -> "%s; charset=%s".format(contentType, encoding))

    val responseHeaders = exchange.getResponseHeaders
    headers foreach {
      case (key, value) =>
        responseHeaders.add(key, value)
    }

    val responseLength = body.map { _.length }.getOrElse(-1)
    exchange.sendResponseHeaders(status, responseLength)

    body.foreach { realBody =>
      val os = exchange.getResponseBody
      os.write(realBody.toText.getBytes(Charset.forName(encoding)));
      os.flush
      os.close
    }

    exchange.close
  }

  def sendError(code: Int, message: String) {
    status = code
    contentType = "text/html"
    body = Some(HtmlResponseBody(
      <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
          <title>Error { code } { message }</title>
        </head>
        <body>
          <h2>HTTP ERROR { code }</h2>
          <p>Reason:<pre>{ message }</pre></p>
          <br/>
        </body>
      </html>
    ))
    send()
  }

}
