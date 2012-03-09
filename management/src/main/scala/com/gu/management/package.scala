package com.gu.management

import java.net.{ URLEncoder, URLDecoder }

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
