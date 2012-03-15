package com.gu.management

import net.liftweb.json._
import scala.xml.Elem

/**
 * Parent of all (immutable) response objects. Pattern drawn from Lift response wrappers.
 */
trait Response {
  def sendTo(resp: HttpResponse)
  def to(resp: HttpResponse) { sendTo(resp) }
}

case class PlainTextResponse(text: String) extends Response {
  def sendTo(response: HttpResponse) {
    response.contentType = "text/plain"
    response.body = TextResponseBody(text)

    response.send()
  }
}

case class HtmlResponse(html: Elem) extends Response {
  def sendTo(response: HttpResponse) {
    response.contentType = "application/xhtml+xml"
    response.body = HtmlResponseBody(html)

    response.send()
  }
}

case class XmlResponse(xml: Elem) extends Response {
  def sendTo(response: HttpResponse) {
    response.contentType = "application/xml"
    response.body = XmlResponseBody(xml)

    response.send()
  }
}

case class ErrorResponse(code: Int, msg: String) extends Response {
  def sendTo(response: HttpResponse) {
    response.sendError(code, msg)
  }
}

case class RedirectResponse(to: String) extends Response {
  def sendTo(response: HttpResponse) {
    response.status = 302
    response.headers += ("Location" -> to)
    response.body = NoResponseBody

    response.send()
  }
}

case class JsonResponse(json: JValue) extends Response {
  def sendTo(response: HttpResponse) {
    response.contentType = "application/json"
    response.body = JsonResponseBody(json)

    response.send()
  }
}
