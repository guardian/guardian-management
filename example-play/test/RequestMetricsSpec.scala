import com.gu.management.{ Metric, TimingMetric, CountMetric }
import conf.PlayExampleRequestMetrics
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class RequestMetricsSpec extends Specification {

  sequential

  "request metrics" should {
    "correctly count and time requests for performance" in running(FakeApplication()) {
      val metric = getMetric[TimingMetric]("request_duration")

      val originalTimingCount = metric.count
      val startTimeTotal = metric.totalTimeInMillis

      status(route(FakeRequest(GET, "/scala-app/long")).get) must equalTo(OK)

      val counted = metric.count - originalTimingCount
      val measuredDuration = metric.totalTimeInMillis - startTimeTotal

      counted must be equalTo (1)
      measuredDuration must be >= 2000L
    }

    "correctly count OK requests" in running(FakeApplication()) {
      val metric = getMetric[CountMetric]("200_ok")

      val originalOkCount = metric.count

      status(route(FakeRequest(GET, "/scala-app")).get) must equalTo(OK)

      val counted = metric.count - originalOkCount

      counted must be equalTo (1)
    }
  }

  def getMetric[A <: Metric](metricName: String) = PlayExampleRequestMetrics.asMetrics.find(_.name == metricName).get.asInstanceOf[A]
}