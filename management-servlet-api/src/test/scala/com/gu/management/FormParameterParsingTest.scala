package com.gu.management

import org.specs2.mutable.Specification

class FormParameterParsingTest extends Specification {

  object FormParameterParsing extends FormParameterParsing

  "FormParameterParsing" should {

    "get no parameters from empty form body" in {
      val body = ""
      val params = FormParameterParsing getParametersFrom body

      params must have size (0)
    }

    "get parameters from single parameter form body" in {
      val body = "key1=val1"

      val params = FormParameterParsing getParametersFrom body

      params must have size (1)
      params must havePair("key1" -> List("val1"))
    }

    "get parameters from multiple parameter form body" in {
      val body = "key1=val1&key2=val2"

      val params = FormParameterParsing getParametersFrom body

      params must have size (2)
      params must havePair("key1" -> List("val1"))
      params must havePair("key2" -> List("val2"))
    }

    "get multiparameters from form body in order of appearance" in {
      val body = "key1=val1&key1=val2"

      val params = FormParameterParsing getParametersFrom body

      params must have size (1)
      params must havePair("key1" -> List("val1", "val2"))
    }

    "get nonascii parameters " in {
      val body = "key1=" + "विकास करने किएलोग स्वतंत्रता अत्यंत".urlencode("UTF-8")

      val params = FormParameterParsing getParametersFrom body

      params must have size (1)
      params must havePair("key1" -> List("विकास करने किएलोग स्वतंत्रता अत्यंत"))
    }

    "get parameters for non UTF-8 encoding" in {
      val body = "key1=" + "विकास करने किएलोग स्वतंत्रता अत्यंत".urlencode("UTF-16")

      val params = FormParameterParsing.getParametersFrom(body, "UTF-16")

      params must have size (1)
      params must havePair("key1" -> List("विकास करने किएलोग स्वतंत्रता अत्यंत"))
    }
  }
}