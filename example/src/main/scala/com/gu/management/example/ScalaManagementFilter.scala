package com.gu.management.example

import com.gu.management._
import logback._

// example of creating your own new page type
class DummyPage extends ManagementPage {
  val path = "/management/dummy"
  def get(req: HttpRequest) = PlainTextResponse("Hello dummy!")
}

// switches
object Switches {
  val omniture = new DefaultSwitch("omniture", "enables omniture java script")
  val takeItDown = new DefaultSwitch("take-it-down", "enable this switch to take the site down", initiallyOn = false)

  val all = omniture :: takeItDown :: Healthcheck.switch :: Nil
}

// timing stuff
object TimingMetrics {
  val downtime = new TimingMetric("example", "downtime", "downtime", "Amount of downtime")
  val requests = new TimingMetric("example", "requests", "requests", "Number of requests recieved")

  val all = downtime :: requests :: Nil
}

// properties
object Properties {
  // If I were using com.gu.configuration I'd comment out the following line
  // val all = new ConfigurationFactory getConfiguration ("music", "conf/arts_music").toString
  val all = "key1=value1\nkey2=value2"
}

class ScalaManagementFilter extends ManagementFilter {
  lazy val pages =
    new DummyPage() ::
      new ManifestPage() ::
      new Switchboard(Switches.all) ::
      StatusPage("Example", TimingMetrics.all) ::
      new HealthcheckManagementPage() ::
      new PropertiesPage(Properties.all) ::
      new LogbackLevelPage() ::
      Nil
}