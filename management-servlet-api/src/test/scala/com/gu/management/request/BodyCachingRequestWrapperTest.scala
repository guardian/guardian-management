package com.gu.management.request

import java.io.ByteArrayInputStream
import net.liftweb.mocks.{ MockHttpServletRequest, MockServletInputStream }
import java.net.URLEncoder._
import org.specs2.mutable.Specification
import scala.collection.JavaConversions._

class BodyCachingRequestWrapperTest extends Specification {

  "body caching request wrapper" should {
    "get parameters from parsed response body" in {
      val wrapper = createWrapperForBodyString("key1=val1&key2=" + encode("विकास करने किएलोग स्वतंत्रता अत्यंत", "UTF-8"))

      wrapper.getParameter("key1") must equalTo("val1")
      wrapper.getParameter("key2") must equalTo("विकास करने किएलोग स्वतंत्रता अत्यंत")
    }

    "get multiple parameters from parsed response body" in {
      val wrapper = createWrapperForBodyString("key1=val1&key1=" + encode("विकास करने किएलोग स्वतंत्रता अत्यंत", "UTF-8"))

      wrapper.getParameterValues("key1") must equalTo(Array("val1", "विकास करने किएलोग स्वतंत्रता अत्यंत"))
    }

    //in theory this should exclude the parameters from the body as the body is read before getParams is called
    //however this may vary between container implementations
    "merge params from wrapped request" in {
      val wrapper = BodyCachingRequestWrapper(new MockHttpServletRequest("/foo") {
        override def getInputStream = new MockServletInputStream(
          new ByteArrayInputStream("key1=val1".getBytes("UTF-8"))
        )

        contentType = "application/x-www-form-urlencoded"
        charEncoding = "UTF-8"
        parameters = List("key1" -> "val2", "key2" -> "val2")
      })

      wrapper.getParameterValues("key1") must equalTo(Array("val1", "val2"))
      wrapper.getParameterValues("key2") must equalTo(Array("val2"))
    }

    "return null if parameter doesnt exist" in {
      val wrapper = createWrapperForBodyString("key1=val1");

      wrapper.getParameter("key2") must equalTo(null)
    }

    "not return parameters if content type is not application/x-www-form-urlencoded" in {
      val wrapper = BodyCachingRequestWrapper(new MockHttpServletRequest("/foo") {
        override def getInputStream = new MockServletInputStream(new ByteArrayInputStream("key1=val1".getBytes("UTF8")))

        charEncoding = "UTF-8"
        contentType = "application/json"
      });

      wrapper.getParameter("key1") must equalTo(null)
    }

    "return null if parameters dont exist" in {
      val wrapper = createWrapperForBodyString("key1=val1");

      wrapper.getParameterValues("key2") must equalTo(null)
    }

    "get parameter map" in {
      val wrapper = createWrapperForBodyString("key1=val1&key2=val2&key1=" + encode("विकास करने किएलोग स्वतंत्रता अत्यंत", "UTF-8"))

      wrapper.getParameterMap.size must equalTo(2)
      wrapper.getParameterValues("key1") must equalTo(Array("val1", "विकास करने किएलोग स्वतंत्रता अत्यंत"))
      wrapper.getParameterValues("key2") must equalTo(Array("val2"))
    }

    "get parameters from parsed response body for non UTF-8 encoding" in {
      val wrapper = BodyCachingRequestWrapper(new MockHttpServletRequest("/foo") {
        override def getInputStream = new MockServletInputStream(new ByteArrayInputStream(("key1=val1&key2=" + encode("विकास करने किएलोग स्वतंत्रता अत्यंत", "UTF-16")).getBytes("UTF-16")))

        contentType = "application/x-www-form-urlencoded"
        charEncoding = "UTF-16"
      });

      wrapper.getParameter("key1") must equalTo("val1")
      wrapper.getParameter("key2") must equalTo("विकास करने किएलोग स्वतंत्रता अत्यंत")
    }

    "Should return enumeration!! of key names" in {
      val wrapper = createWrapperForBodyString("key1=val1&key2=val2");

      val names = wrapper.getParameterNames
      names.nextElement() must equalTo("key1")
      names.nextElement() must equalTo("key2")
      names.hasMoreElements must equalTo(false)
    }

  }

  def createWrapperForBodyString(requestBody: String) = {
    BodyCachingRequestWrapper(new MockHttpServletRequest("/foo") {
      override def getInputStream = new MockServletInputStream(new ByteArrayInputStream(requestBody.getBytes("UTF-8")))

      charEncoding = "UTF-8"
      contentType = "application/x-www-form-urlencoded"
    });
  }

}