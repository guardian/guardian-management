package com.gu.management.servlet

import javax.servlet.http.HttpServletRequest
import scala.collection.JavaConversions._
import scalax.io.Resource

object `package` {
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