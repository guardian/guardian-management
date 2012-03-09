package com.gu.management

import javax.servlet.http.{ HttpServletResponse, HttpServletRequest }
import javax.servlet.FilterChain
import org.slf4j.MDC

class UniqueIdFilter extends AbstractHttpFilter {
  val uniqueIdHeaderName = "X-GU-UniqueId"
  val uniqueIdKey = "GUUID"
  val defaultId = "None"

  def doHttpFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
    val value = (request getHeaderOption uniqueIdHeaderName) getOrElse defaultId

    MDC.put(uniqueIdKey, value)
    chain.doFilter(request, response)
  }

}