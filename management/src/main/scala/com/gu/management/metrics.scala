package com.gu.management

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.Callable

private[management] case class Definition(group: String, name: String)

private[management] case class StatusMetric(
  group: String = "application",
  master: Option[Definition] = None,
  // name should be brief and underscored not camel case
  name: String,
  `type`: String,
  // a short (<40 chars) title for this metric
  title: String,
  // an as-long-as-you-like description of what this metric means
  // (used, e.g. on mouse over)
  description: String,
  // NB: these are deliberately strings - some json parsers have issues
  // with big numbers, see https://dev.twitter.com/docs/twitter-ids-json-and-snowflake
  value: Option[String] = None,
  count: Option[String] = None,
  totalTime: Option[String] = None,
  units: Option[String] = None)

trait Metric {
  def group: String
  def name: String
  def asJson: StatusMetric

  lazy val definition: Definition = Definition(group, name)
}

abstract class InstantaneousMetric[A](
    `type`: String,
    val group: String,
    val name: String,
    title: String,
    description: String,
    getValue: () => A,
    master: Option[Metric] = None) extends Metric {

  def asJson = StatusMetric(
    group = group,
    master = master map { _.definition },
    name = name,
    `type` = `type`,
    title = title,
    description = description,
    value = Some(getValue().toString)
  )
}

class GaugeMetric(group: String, name: String, title: String, description: String, getCount: () => Long,
  master: Option[Metric] = None)
    extends InstantaneousMetric[Long]("gauge", group, name, title, description, getCount, master) {

  def count: Long = getCount()
}

object GaugeMetric {

  def apply(group: String, name: String, title: String, description: String, getCount: () => Long): GaugeMetric =
    new GaugeMetric(group, name, title, description, getCount, None)

  def apply(group: String, name: String, title: String, description: String, getCount: () => Long, master: Metric): GaugeMetric =
    new GaugeMetric(group, name, title, description, getCount, Some(master))

}

class TextMetric(group: String, name: String, title: String, description: String, getValue: () => String,
  master: Option[Metric] = None)
    extends InstantaneousMetric[String]("text", group, name, title, description, getValue, master) {

  def value: String = getValue()

}

class CountMetric(
    val group: String,
    val name: String,
    title: String,
    description: String,
    master: Option[Metric] = None) extends Metric {
  private val _count = new AtomicLong()

  def recordCount(count: Int) {
    _count.addAndGet(count)
  }

  def count = _count.get

  def asJson = StatusMetric(
    group = group,
    master = master map { _.definition },
    name = name,
    `type` = "counter",
    title = title,
    description = description,
    count = Some(count.toString)
  )
}

object CountMetric {

  def apply(group: String, name: String, title: String, description: String): CountMetric =
    new CountMetric(group, name, title, description, None)

  def apply(group: String, name: String, title: String, description: String, master: Metric): CountMetric =
    new CountMetric(group, name, title, description, Some(master))

}

class TimingMetric(
    val group: String,
    val name: String,
    title: String,
    description: String,
    master: Option[Metric] = None) extends Metric {

  private val _totalTimeInMillis = new AtomicLong()
  private val _count = new AtomicLong()

  def recordTimeSpent(durationInMillis: Long) {
    _totalTimeInMillis.addAndGet(durationInMillis)
    _count.incrementAndGet
  }

  def asJson = StatusMetric(
    group = group,
    master = master map { _.definition },
    name = name,
    `type` = "timer",
    title = title,
    description = description,
    count = Some(count.toString),
    totalTime = Some(totalTimeInMillis.toString)
  )

  def totalTimeInMillis = _totalTimeInMillis.get

  def count = _count.get

  // to use this class, you can write your own wrappers
  // and call recordTimeSpent, or you may use this one
  // if you want.
  // val t = TimingMetric("example")
  // ...
  // t measure {
  //   code here
  // }
  def measure[T](block: => T) = {
    val s = new StopWatch
    val result = block
    recordTimeSpent(s.elapsed)
    result
  }

  // for java developers, these are easier to call
  def call[T](c: Callable[T]) = measure { c.call }
  def run(r: Runnable) { measure { r.run() } }
}

object TimingMetric {

  def apply(group: String, name: String, title: String, description: String): TimingMetric =
    new TimingMetric(group, name, title, description, None)

  def apply(group: String, name: String, title: String, description: String, master: Metric): TimingMetric =
    new TimingMetric(group, name, title, description, Some(master))

  def empty = new TimingMetric("application", "Empty", "Empty", "Empty")

}
