package com.gu.management

import java.net.URLDecoder

object `package` {

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