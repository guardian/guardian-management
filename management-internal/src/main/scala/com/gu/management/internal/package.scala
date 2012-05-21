package com.gu.management.internal

import io.Source
import com.gu.management._
import com.sun.net.httpserver.{ HttpServer, HttpsServer, HttpExchange }

object `package` extends FormParameterParsing {
  implicit def integer2In(integer: Int) = new {
    def in(range: Range) = range contains integer
  }

  implicit def httpExchange2GetParameters(exchange: HttpExchange) = new {
    lazy val getParameters: Map[String, List[String]] = {
      getGetParameters addBindings getPostParameters
    }

    lazy val getGetParameters: ListMultiMap[String, String] = {
      val getQueryString = exchange.getRequestURI.getQuery
      if (getQueryString == null || getQueryString.isEmpty) {
        Map.empty[String, List[String]]
      } else {
        getParametersFrom(getQueryString)
      }
    }

    lazy val getPostParameters: ListMultiMap[String, String] = {
      if (!(exchange.getRequestMethod equalsIgnoreCase "POST") ||
        !(exchange.getRequestHeaders.get("Content-Type").contains("application/x-www-form-urlencoded"))) {
        Map.empty[String, List[String]]
      } else {
        val postQueryString = Source.fromInputStream(exchange.getRequestBody, "UTF-8").mkString("")
        getParametersFrom(postQueryString)
      }
    }

    lazy val getURIProtocol: String = {
      exchange.getHttpContext.getServer match {
        case _: HttpsServer => "https"
        case _: HttpServer => "http"
      }
    }
  }
}