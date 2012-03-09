package com.gu.management.servlet

import com.gu.management._
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import scala.collection.JavaConverters._

/*
 * If you want to change the behaviour of this class, derive a version
 * and call super with the parameters you want to set
 */
class RequestLoggingFilter(
    metric: TimingMetric = HttpRequestsTimingMetric,
    shouldLogParametersOnNonGetRequests: Boolean = false,
    parametersToSuppressInLogs: Set[String] = Set.empty,
    pathPrefixesToLogAtTrace: Set[String] = Set("/management"),
    maximumSizeForPostParameters: Int = 32,
    logRequestBodySwitch: Switch = LogRequestBodySwitch,
    maxRequstBodyLength: Int = 1024) extends AbstractHttpFilter with Loggable {

  protected lazy val restrictedParamsRegexp = parametersToSuppressInLogs.mkString("(?:", ")|(?:", ")").r

  // Default constructor for use in web.xml as opposed to dependency injection frameworks
  def this() = this(HttpRequestsTimingMetric, false, Set.empty, Set("/management"), 32)

  class Request(request: HttpServletRequest) {
    lazy val servletPath = Option(request.getServletPath) getOrElse ""
    lazy val pathInfo = Option(request.getPathInfo) getOrElse ""
    lazy val fullPath = servletPath + pathInfo

    lazy val method = request.getMethod

    lazy val params = request.parameters mapValues {
      _.headOption map {
        _.toString
      } getOrElse ""
    }

    lazy val loggableParams = {
      if (shouldLogParametersOnNonGetRequests || method == "GET") {
        params map filterParamForLogging
      } else {
        Nil
      }
    }

    lazy val loggableParamString = loggableParams match {
      case Nil => ""
      case l => l.map { case (k, v) => k + "=" + v.urlencode("UTF-8") } mkString ("?", "&", "")
    }

    def filterParamForLogging(p: (String, String)) = p match {
      case (k, v) if (parametersToSuppressInLogs contains k) =>
        k -> "*****"
      case (k, v) if ("POST" == method && v.length > maximumSizeForPostParameters) =>
        k -> (v.take(maximumSizeForPostParameters) + "...")
      case other =>
        other
    }

    lazy val requestBody = {
      try {
        (request match {
          case request: BodyCachingRequestWrapper =>
            val bodyAsString = new String(request.body, request.encoding)
            if (restrictedParamsRegexp.findFirstIn(bodyAsString).isDefined) "<<restricted>>" else bodyAsString
          case _ => "<<wont display>>"
        }).take(maxRequstBodyLength)
      } catch {
        case e =>
          logger.trace("Failed to display request body", e)
          "<<binary>>"
      }
    }

    lazy val shouldLog = !(pathPrefixesToLogAtTrace exists {
      fullPath.startsWith
    })
  }

  def doHttpFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
    val logPostData = logRequestBodySwitch.isSwitchedOn && List("POST", "PUT").contains(request.getMethod)
    val wrappedRequest = if (logPostData) BodyCachingRequestWrapper(request) else request

    val req = new Request(wrappedRequest)

    val activity = req.method + " " + req.fullPath + req.loggableParamString + (if (logPostData) " " + req.requestBody else "")

    if (req.shouldLog) {
      logger.trace(activity)
    }

    val stopWatch = new StopWatch
    try {
      val (key, value) = AppServerHeader()
      response.setHeader(key, value)

      chain.doFilter(wrappedRequest, response)

      if (req.shouldLog) {
        logger.info(activity + " completed in " + stopWatch.elapsed + " ms")
        metric.recordTimeSpent(stopWatch.elapsed)
      }
    } catch {
      case t: Throwable =>
        logger.warn(activity + " caused exception after " + stopWatch.elapsed + " ms", t)
        throw t
    }
  }

}

object LogRequestBodySwitch extends DefaultSwitch("log-post-data", "Switches request body logging by the request logging filter", false)
