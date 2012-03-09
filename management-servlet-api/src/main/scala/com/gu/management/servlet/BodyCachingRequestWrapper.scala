package com.gu.management.servlet

import com.gu.management._
import java.io.ByteArrayInputStream
import javax.servlet.ServletInputStream
import javax.servlet.http.{ HttpServletRequest, HttpServletRequestWrapper }
import scala.collection.JavaConversions._

class BodyCachingRequestWrapper(val request: HttpServletRequest) extends HttpServletRequestWrapper(request)
    //class BodyCachingRequestWrapper(private val request: HttpServletRequest) extends HttpServletRequestWrapper(request)
    with FormParameterParsing {
  val body = request.getBody()
  lazy val encoding = request.getCharacterEncodingOption() getOrElse "UTF-8"

  // In theory request.getParameterMap should not return parameters from the body as the body
  // has been read at this point, this may vary depending on container implementation or if there are other
  // body caching wrappers up stream
  lazy val params: ListMultiMap[String, String] = formParams addBindings request.parameters

  lazy val isForm: Boolean = request.getContentType() startsWith ("application/x-www-form-urlencoded")

  lazy val formParams: Map[String, List[String]] = isForm match {
    case true => getParametersFrom(new String(body, encoding), encoding)
    case _ => Map()
  }

  override def getParameter(name: String): String = params.getOrElse(name, List[String]()).headOption getOrElse null

  override def getParameterNames = asJavaEnumeration(params.keySet.iterator)

  override def getParameterValues(name: String): Array[String] = params.get(name) map {
    _.toArray
  } getOrElse null

  override def getParameterMap: java.util.Map[String, Array[String]] = params mapValues {
    _.toArray
  }

  override def getInputStream: ServletInputStream = {
    new ServletInputStream {
      val inputStream = new ByteArrayInputStream(body)

      def read() = inputStream.read()

      override def read(p1: Array[Byte]) = inputStream.read(p1)

      override def read(p1: Array[Byte], p2: Int, p3: Int) = inputStream.read(p1, p2, p3)

      override def skip(p1: Long) = inputStream.skip(p1)

      override def available() = inputStream.available()

      override def close() {
        inputStream.close()
      }

      override def mark(p1: Int) {
        inputStream.mark(p1)
      }

      override def reset() {
        inputStream.reset()
      }

      override def markSupported() = inputStream.markSupported()
    }
  }
}

object BodyCachingRequestWrapper {
  def apply(request: HttpServletRequest): BodyCachingRequestWrapper = request match {
    case request: BodyCachingRequestWrapper => request
    case request => new BodyCachingRequestWrapper(request)
  }
}