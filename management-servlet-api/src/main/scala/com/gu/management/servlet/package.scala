package com.gu.management.servlet

import javax.servlet.http.{ HttpServletResponse, HttpServletRequest }
import scala.collection.JavaConversions._
import scalax.io.Resource

object `package` {

  implicit def string2SplitAtFirst(s: String) = new {
    def splitAtFirst(regex: String): Option[(String, String)] = s.split(regex) match {
      case Array() => None
      case Array(_) => Some(s -> "")
      case Array(prefix, rest) => Some(prefix -> rest)
      case splits =>
        Some(splits(0) -> s.replaceFirst(splits(0), "").replaceFirst(regex, ""))
    }
  }

  implicit def string2kv(s: String) = new {
    def kv(delimiter: String): Option[(String, String)] = {
      for {
        (k, v) <- s.splitAtFirst(delimiter)
      } yield k.trim -> v.trim
    }
  }

  implicit def base64decodedstring(s: String) = new {
    def base64Decoded: String = new String(javax.xml.bind.DatatypeConverter.parseBase64Binary(s), "UTF-8")
  }

  implicit def httpServletResponseToSendAuth(r: HttpServletResponse) = new {
    def sendNeedsAuthorisation(realm: String) {
      r.addHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"")
      r.sendError(401, "Needs Authorisation")
    }
  }

  implicit def list2ZipWith[A](l: List[A]) = new {
    def zipWith[B](f: A => B): List[(A, B)] = l map { a => (a, f(a)) }
  }

  implicit def httpServletRequest2Parameters(request: HttpServletRequest) = new {
    lazy val paramNames: List[String] = {
      // Sigh, the servlet spec uses a raw type...
      val enumeration: java.util.Enumeration[_] = request.getParameterNames
      enumeration.toList map { _.toString }
    }

    lazy val parameters: Map[String, List[String]] =
      (paramNames zipWith { name => (request getParameterValues name).toList }).toMap
  }

  implicit def httpServletRequest2Path(request: HttpServletRequest) = new {
    lazy val path = Option(request.getServletPath).getOrElse("") + Option(request.getPathInfo).getOrElse("")
  }

  implicit def httpServletRequest2GetHeaderOption(request: HttpServletRequest) = new {
    def getHeaderOption(name: String): Option[String] = Option(request getHeader name)
  }

  implicit def httpServletRequest2GetCharacterEncodingOption(request: HttpServletRequest) = new {
    def getCharacterEncodingOption(): Option[String] = Option(request.getCharacterEncoding)
  }

  implicit def httpServletRequest2GetBody(request: HttpServletRequest) = new {
    def getBody(): Array[Byte] = Resource.fromInputStream(request.getInputStream).bytes.toArray
  }
}