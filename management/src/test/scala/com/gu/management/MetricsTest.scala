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
  }

}
