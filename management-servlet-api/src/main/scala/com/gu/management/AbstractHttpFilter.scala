package com.gu.management

import javax.servlet._
import http.{ HttpServletResponse, HttpServletRequest }

trait AbstractHttpFilter extends Filter {
  def init(filterConfig: FilterConfig) {}
  def destroy() {}

  final def doFilter(req: ServletRequest, resp: ServletResponse, chain: FilterChain) {
    (req, resp) match {
      case (httpReq: HttpServletRequest, httpResp: HttpServletResponse) =>
        doHttpFilter(httpReq, httpResp, chain)

      case _ => chain.doFilter(req, resp)
    }
  }

  def doHttpFilter(servletRequest: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain)
}

