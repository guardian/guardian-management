package com.gu.management.play

import play.api.mvc.{ AnyContent, Request }
import com.gu.management.ListMultiMaps

object `package` extends ListMultiMaps {

  implicit def request2Parameters[A](request: Request[A]) = new {
    lazy val parameters: ListMultiMap[String, String] = {
      val queryStringParameters = request.queryString mapValues { _.toList }
      val bodyFormParameters: Map[String, List[String]] = request.body match {
        case body: AnyContent if body.asFormUrlEncoded.isDefined => body.asFormUrlEncoded.get mapValues { _.toList }
        case _ => Map()
      }

      queryStringParameters addBindings bodyFormParameters
    }
  }

  implicit def request2RequestURI[A](request: Request[A]) = new {
    lazy val requestURI: String = request.uri.replaceAll("\\?.*", "")
  }
}
