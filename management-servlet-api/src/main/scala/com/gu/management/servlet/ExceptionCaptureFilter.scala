package com.gu.management.servlet

import javax.servlet.http.{ HttpServletResponse, HttpServletRequest }
import javax.servlet.FilterChain
import com.gu.management.{ ExceptionCountMetric, CountMetric }

class ExceptionCaptureFilter(counterMetric: CountMetric = ExceptionCountMetric) extends AbstractHttpFilter {

  // Default constructor for use in web.xml as opposed to dependency injection frameworks
  def this() = this(ExceptionCountMetric)

  def doHttpFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
    try {
      chain.doFilter(request, response)
    } catch {
      case t: Throwable =>
        // NB: No need to log the exception as you're using RequestLoggingFilter, right?
        counterMetric.recordCount(1)
        throw t
    }
  }

}