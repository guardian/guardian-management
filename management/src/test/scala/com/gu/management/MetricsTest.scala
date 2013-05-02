package com.gu.management

import org.specs2.mutable.Specification

class MetricsTest extends Specification {
  "CountMetrics" should {
    "have type count" in {
      val metric = new CountMetric("group", "name", "title", "description", master = None)

      metric.asJson.`type` must_== "counter"
    }

    "have initial value 0" in {
      val metric = new CountMetric("group", "name", "title", "description", master = None)

      metric.getValue() must_== 0
      metric.count must_== 0
      metric.asJson.count must_== Some("0")
    }

    "be incrementable" in {
      val metric = new CountMetric("group", "name", "title", "description", master = None)

      metric.increment()

      metric.getValue() must_== 1
      metric.count must_== 1
      metric.asJson.count must_== Some("1")
    }

    "be updatable" in {
      val metric = new CountMetric("group", "name", "title", "description", master = None)

      metric.recordCount(100)

      metric.getValue() must_== 100
      metric.count must_== 100
      metric.asJson.count must_== Some("100")
    }
  }

  "TextMetrics" should {
    "have type count" in {
      val metric = new TextMetric(
        "group", "name", "title", "description", getValue = () => "value", master = None
      )

      metric.asJson.`type` must_== "text"
    }

    "have the specified value" in {
      val metric = new TextMetric(
        "group", "name", "title", "description", getValue = () => "value", master = None
      )

      metric.getValue() must_== "value"
      metric.asJson.value must_== Some("value")
    }
  }

  "TimingMetrics" should {
    "have type timer" in {
      val metric = new TimingMetric("group", "name", "title", "description", master = None)

      metric.asJson.`type` must_== "timer"
    }

    "have initial value 0" in {
      val metric = new TimingMetric("group", "name", "title", "description", master = None)

      metric.getValue() must_== 0
      metric.count must_== 0
      metric.totalTimeInMillis must_== 0
      metric.asJson.count must_== Some("0")
      metric.asJson.totalTime must_== Some("0")
    }

    "record time spent" in {
      val metric = new TimingMetric("group", "name", "title", "description", master = None)
      metric.recordTimeSpent(250)
      metric.getValue() must_== 250
      metric.count must_== 1
      metric.totalTimeInMillis must_== 250
      metric.asJson.count must_== Some("1")
      metric.asJson.totalTime must_== Some("250")

    }
  }

  "ExtendedTimingMetrics" should {
    def newMetric() = new ExtendedTimingMetric("group", "name", "title", "description", master = None,
      percentiles = List(50, 75))
    "have several submetrics" in {
      val metric = newMetric()
      metric.submetrics must haveKeys("name_50", "name_75", "name")
    }

    "record time in all submetrics" in {
      val metric = newMetric()
      // We will record 4 values
      // These should be stored as [20,30,40,150]
      // The main metric sum is 240, count 4, mean 60
      // The 50 percentile sum is 50, count 2, mean 25
      // the 75 percentile sum is 90, count 3, mean 30
      metric.recordTimeSpent(40)
      metric.recordTimeSpent(30)
      metric.recordTimeSpent(150)
      metric.recordTimeSpent(20)

      metric.processMetrics()

      metric.submetrics("name").count mustEqual 4
      metric.submetrics("name").totalTimeInMillis mustEqual 240
      metric.submetrics("name_50").count mustEqual 2
      metric.submetrics("name_50").totalTimeInMillis mustEqual 50
      metric.submetrics("name_75").count mustEqual 3
      metric.submetrics("name_75").totalTimeInMillis mustEqual 90
    }
  }

}
