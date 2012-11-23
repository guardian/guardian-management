package com.gu.management.play

import play.api.GlobalSettings
import com.gu.management.{ StopWatch, CountMetric, TimingMetric }
import play.api.mvc._
import AsyncResultUnwrapper._

trait StatusCounters extends ErrorCounter with OkCounter with NotFoundCounter with RedirectCounter

trait RequestTimer extends GlobalSettings {

  val requestTimer: TimingMetric

  override def onRouteRequest(req: RequestHeader): Option[play.api.mvc.Handler] = {
    super.onRouteRequest(req) map {
      case a: Action[AnyContent] =>
        Action { request: Request[AnyContent] =>
          val s = new StopWatch
          val result = a(request)
          onFinalResult(result) { _ => requestTimer.recordTimeSpent(s.elapsed) }
          result
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
          val result = a(request)
          onFinalResult(result) {
            case simpleResult: SimpleResult[AnyContent] =>
              val status = simpleResult.header.status
              if (f.isDefinedAt(status)) f(status)
            case _ =>
          }
          result
      }
    case o => o
  }
}

private object AsyncResultUnwrapper {
  /**
   * AsyncResult can - theoretically- return another AsyncResult, which we don't want. Redeem only when have a
   * non-async result.
   */
  def onFinalResult(result: Result)(k: Result => Unit) {
    result match {
      case AsyncResult(promise) => promise.onRedeem(nestedResult => onFinalResult(nestedResult)(k))
      case nonAsyncResult => k(nonAsyncResult)
    }
  }
}
