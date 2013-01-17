import conf.PlayExampleRequestMetrics
import play.api.mvc.WithFilters

object Global extends WithFilters(PlayExampleRequestMetrics.asFilters: _*)
