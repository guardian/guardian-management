package com.gu.management

import scala.collection.mutable

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
  var body: String

  def send()
  def sendError(code: Int, message: String)
}
