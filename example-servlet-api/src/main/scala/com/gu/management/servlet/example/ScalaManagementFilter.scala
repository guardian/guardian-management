package com.gu.management.servlet.example

import com.gu.management._
import com.gu.management.logback._
import com.gu.management.servlet.ManagementFilter

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
  val userProvider = new UserProvider {
    // This is a very bad user provider, looking it up in a db or Properties would be better
    // Note you can just check password or username, it's up to you
    def isValid(credentials: UserCredentials) = credentials.username == credentials.password
    val realm = "ScalaManagementFilter"
  }
  val applicationName = "Example Servlet API Application"
  lazy val pages =
    new DummyPage() ::
      new ManifestPage() ::
      new Switchboard(applicationName, Switches.all) ::
      StatusPage(applicationName, TimingMetrics.all) ::
      new HealthcheckManagementPage() ::
      new PropertiesPage(Properties.all) ::
      new LogbackLevelPage(applicationName) ::
      Nil
}