package com.gu.management.play

import play.api.GlobalSettings
import com.gu.management.{ CountMetric, TimingMetric }
import play.api.mvc._

trait StatusCounters extends ErrorCounter with OkCounter with NotFoundCounter with RedirectCounter

trait RequestTimer extends GlobalSettings {

  val requestTimer: TimingMetric

  override def onRouteRequest(req: RequestHeader): Option[play.api.mvc.Handler] = {
    super.onRouteRequest(req) map {
      case a: Action[AnyContent] =>
        Action { request: Request[AnyContent] =>
          requestTimer.measure(a(request))
        }
      case o => o
    }
  }
}

trait OkCounter extends GlobalSettings {
  val okCounter: CountMetric
  override def onRouteRequest(req: RequestHeader): Option[play.api.mvc.Handler] = {
    super.onRouteRequest(req) map {
      HandleStatusCode(_) { case 200 => okCounter.recordCount(1) }
    }
  }
}

trait RedirectCounter extends GlobalSettings {
  val redirectCounter: CountMetric
  override def onRouteRequest(req: RequestHeader): Option[play.api.mvc.Handler] = {
    super.onRouteRequest(req) map {
      HandleStatusCode(_) { case 301 | 302 => redirectCounter.recordCount(1) }
    }
  }
}

trait NotFoundCounter extends GlobalSettings {
  val notFoundCounter: CountMetric
  override def onRouteRequest(req: RequestHeader): Option[play.api.mvc.Handler] = {
    super.onRouteRequest(req) map {
      HandleStatusCode(_) { case 404 => notFoundCounter.recordCount(1) }
    }
  }
}

trait ErrorCounter extends GlobalSettings {
  val errorCounter: CountMetric
  override def onError(request: RequestHeader, ex: Throwable): Result = {
    errorCounter.recordCount(1)
    super.onError(request, ex)
  }
}

private object HandleStatusCode {
  def apply(handler: Handler)(f: PartialFunction[Int, Unit]): Handler = handler match {
    case a: Action[AnyContent] =>
      Action {
        request: Request[AnyContent] =>
          a(request) match {
            case result: SimpleResult[AnyContent] =>
              val status = result.header.status
              if (f.isDefinedAt(status)) f(status)
              result
            case result => result
          }
      }
    case o => o
  }
}

