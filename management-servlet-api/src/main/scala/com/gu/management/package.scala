package com.gu.management

import java.net.{ URLEncoder, URLDecoder }
import javax.servlet.http.HttpServletRequest
import scala.collection.JavaConversions._
import scalax.io.Resource

trait ListMultiMaps {
  type ListMultiMap[A, B] = Map[A, List[B]]

  implicit def listMultiMap2ListMultiMapOperations[A, B](map: ListMultiMap[A, B]) = new ListMultiMapOperations(map)

  object ListMultiMap {
    def apply[A, B](): ListMultiMap[A, B] = Map[A, List[B]]()
    def apply[A, B](kvs: List[(A, B)]): ListMultiMap[A, B] = {
      var rst = apply[A, B]()
      kvs foreach { kv =>
        rst = rst addBinding kv
      }

      rst
    }
  }

  class ListMultiMapOperations[A, B](map: ListMultiMap[A, B]) {
    def addBinding(key: A, value: B): ListMultiMap[A, B] = {
      val current = (map get key) getOrElse Nil
      map + (key -> (current ++ List(value)))
    }

    def addBinding(kv: (A, B)): ListMultiMap[A, B] = addBinding(kv._1, kv._2)

    def addBindings(key: A, values: List[B]): ListMultiMap[A, B] = {
      val current = (map get key) getOrElse Nil
      map + (key -> (current ++ values))
    }
    def addBindings(kv: (A, List[B])): ListMultiMap[A, B] = addBindings(kv._1, kv._2)
    def addBindings(that: ListMultiMap[A, B]): ListMultiMap[A, B] = {
      var rst = map
      that foreach { kvs =>
        rst = rst.addBindings(kvs)
      }

      rst
    }

    def removeBinding(key: A, value: B): ListMultiMap[A, B] = {
      val current = (map get key) getOrElse Nil
      current filterNot { _ == value } match {
        case Nil => map - key
        case updated => map + (key -> updated)
      }
    }

    def removeBinding(kv: (A, B)): ListMultiMap[A, B] = removeBinding(kv._1, kv._2)

    def removeBindings(key: A, values: List[B]): ListMultiMap[A, B] = {
      val current = (map get key) getOrElse Nil
      current filterNot { values contains _ } match {
        case Nil => map - key
        case updated => map + (key -> updated)
      }
    }
    def removeBindings(kv: (A, List[B])): ListMultiMap[A, B] = removeBindings(kv._1, kv._2)
    def removeBindings(that: ListMultiMap[A, B]): ListMultiMap[A, B] = {
      var rst = map
      that foreach { kvs =>
        rst = rst.removeBindings(kvs)
      }

      rst
    }

    def entryExists(key: A, p: B => Boolean): Boolean = (map get key) match {
      case Some(list) => list exists p
      case _ => false
    }
  }
}

object `package` extends ListMultiMaps {

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

  implicit def string2UrlCoding(s: String) = new {
    def urldecode(encoding: String): String = URLDecoder.decode(s, encoding)
    def urlencode(encoding: String): String = URLEncoder.encode(s, encoding)
  }

  trait FormParameterParsing {
    def getParameterFrom(param: String, encoding: String = "UTF-8"): Option[(String, String)] = {
      (param split '=').toList match {
        case List(key, value) => Some(key urldecode encoding, value urldecode encoding)
        case _ => None
      }
    }

    def getParametersFrom(body: String, encoding: String = "UTF-8"): ListMultiMap[String, String] = {
      val split: List[String] = (body split '&').toList
      val params = split flatMap { param => getParameterFrom(param, encoding).toList }

      ListMultiMap(params)
    }
  }

}
