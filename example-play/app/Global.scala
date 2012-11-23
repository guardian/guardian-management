import com.gu.management.play.{ StatusCounters, RequestTimer }
import play.api.GlobalSettings

object Global extends GlobalSettings with RequestTimer with StatusCounters {
  import conf.RequestMetrics._

  override val okCounter = Request200s
  override val errorCounter = Request50xs
  override val notFoundCounter = Request404s
  override val redirectCounter = Request30xs
  override val requestTimer = conf.TimingMetrics.requests
}
