package com.gu.management

import java.net.URLDecoder
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

  implicit def string2Decode(s: String) = new {
    def urldecode(encoding: String): String = URLDecoder.decode(s, encoding)
  }

  trait MultimapHandling {
    def addToMultiMap[K, V](multiMap: Map[K, List[V]], kv: (K, V)): Map[K, List[V]] = {
      val (key, value) = kv
      val update = key -> (value :: multiMap.get(key).getOrElse(List[V]()))
      multiMap + update
    }

    def addToMultiMap[K, V](multiMap: Map[K, List[V]], kvs: Map[K, List[V]]): Map[K, List[V]] = {
      var result = multiMap
      kvs foreach {
        case (key, values) =>
          values foreach { value =>
            result = addToMultiMap(result, (key -> value))
          }
      }

      result
    }
  }

  trait FormParameterParsing extends MultimapHandling {
    def getParameterFrom(param: String, encoding: String): Option[(String, String)] = {
      (param split '=').toList match {
        case List(key, value) => Some(key urldecode encoding, value urldecode encoding)
        case _ => None
      }
    }

    def getParametersFrom(body: String, encoding: String): Map[String, List[String]] = {
      val split: List[String] = (body split '&').toList

      var params = Map[String, List[String]]()
      split foreach { keyValueString =>
        getParameterFrom(keyValueString, encoding) foreach { kv =>
          params = addToMultiMap(params, kv)
        }
      }

      params
    }
  }

}
