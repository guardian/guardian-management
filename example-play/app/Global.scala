import com.gu.management.play.{ OkCounter, RequestTimer }
import com.gu.management.{ TimingMetric, CountMetric }
import play.api.GlobalSettings

object Global extends GlobalSettings with RequestTimer with OkCounter {
  override val okCounter = new CountMetric("status", "200_ok", "Status OK", "requests that return a status of 200")
  override val requestTimer = new TimingMetric("performance", "request", "Request", "time taken to serve requests")
}
