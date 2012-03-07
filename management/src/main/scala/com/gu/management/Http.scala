package com.gu.management

import scala.collection.mutable
import scala.xml.Elem
import net.liftweb.json._

sealed abstract class Method
case object GET extends Method
case object POST extends Method

object Method {
  def apply(method: String): Method = method.toUpperCase match {
    case "GET" => GET
    case "POST" => POST
    // Only GET and POST are supported for Management URLs
  }
}

sealed abstract class ResponseBody { def toText: String }
case class TextResponseBody(text: String) extends ResponseBody { lazy val toText = text }
case class HtmlResponseBody(html: Elem) extends ResponseBody { lazy val toText = html.toString() }
case class XmlResponseBody(xml: Elem) extends ResponseBody { lazy val toText = xml.toString() }
case class JsonResponseBody(json: JValue) extends ResponseBody { lazy val toText = pretty(render(json)) }
case object NoResponseBody extends ResponseBody { lazy val toText = "" }

/*
 * requestURI: the part of this request's URL from the protocol name up to the query string
 *             in the first line of the HTTP request.
 */
case class HttpRequest(
    method: Method,
    path: String,
    requestURI: String,
    parameters: Map[String, List[String]]) {

  def getParameter(key: String): Option[String] = (parameters get key) flatMap { _.headOption }
}

trait HttpResponse {
  var encoding: String = "UTF-8"
  val headers: mutable.Map[String, String] = mutable.Map()

  var contentType: String

  var status: Int
  var body: ResponseBody

  def send()
  def sendError(code: Int, message: String)
}
