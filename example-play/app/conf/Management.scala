package conf

import com.gu.management._
import com.gu.management.logback._

// example of creating your own new page type
class DummyPage extends ManagementPage {
  val path = "/management/dummy"
  def get(request: HttpRequest) = PlainTextResponse("Hello dummy!")
}

// switches
object Switches {
  val omniture = new DefaultSwitch("omniture", "enables omniture java script")
  val takeItDown = new DefaultSwitch("take-it-down", "enable this switch to take the site down", initiallyOn = false)

  val all = List(omniture, takeItDown, Healthcheck.switch)
}

object RequestMetrics {
  object Request200s extends CountMetric("request-status", "200_ok", "200 Ok", "number of pages that responded 200")
  object Request50xs extends CountMetric("request-status", "50x_error", "50x Error", "number of pages that responded 50x")
  object Request404s extends CountMetric("request-status", "404_not_found", "404 Not found", "number of pages that responded 404")
  object Request30xs extends CountMetric("request-status", "30x_redirect", "30x Redirect", "number of pages that responded with a redirect")
  object RequestOther extends CountMetric("request-status", "other", "Other", "number of pages that responded with an unexpected status code")

  val all = List(Request200s, Request50xs, Request404s, RequestOther, Request30xs)
}

// timing stuff
object TimingMetrics {
  val downtime = new TimingMetric("example", "downtime", "downtime", "Amount of downtime")
  val requests = new TimingMetric("example", "requests", "requests", "Number of requests recieved")

  val all = List(downtime, requests)
}

// properties
object Properties {
  // If I were using com.gu.configuration I'd comment out the following line
  // val all = new ConfigurationFactory getConfiguration ("music", "conf/arts_music").toString
  val all = "key1=value1\nkey2=value2"
}

object Management extends com.gu.management.play.Management {
  val applicationName: String = "Example Play App"
  lazy val pages = List(
    new DummyPage(),
    new ManifestPage(),
    new Switchboard(applicationName, Switches.all),
    StatusPage(applicationName, ExceptionCountMetric :: ServerErrorCounter :: ClientErrorCounter :: TimingMetrics.all ::: RequestMetrics.all),
    new HealthcheckManagementPage(),
    new PropertiesPage(Properties.all),
    new LogbackLevelPage(applicationName)
  )
}
