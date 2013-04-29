package com.gu.management

import java.util.concurrent.atomic.{ AtomicReference, AtomicLong }
import java.util.concurrent.Callable
import collection.mutable

case class Definition(group: String, name: String)

case class StatusMetric(
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
  def json: Seq[StatusMetric] = List(asJson)

  lazy val definition: Definition = Definition(group, name)
}

trait AbstractMetric[T] extends Metric {
  val `type`: String
  val group: String
  val name: String
  val title: String
  val description: String
  val master: Option[Metric] = None

  val getValue: () => T

  def asJson: StatusMetric = StatusMetric(group, master map { _.definition }, name, `type`, title, description)
}

class GaugeMetric[T](
    val group: String, val name: String, val title: String, val description: String,
    val getValue: () => T, override val master: Option[Metric] = None) extends AbstractMetric[T] {
  val `type`: String = "gauge"
  override def asJson: StatusMetric = super.asJson.copy(value = Some(getValue().toString))
}

class TextMetric(
    val group: String, val name: String, val title: String, val description: String,
    val getValue: () => String, override val master: Option[Metric] = None) extends AbstractMetric[String] {
  override val `type`: String = "text"
  override def asJson: StatusMetric = super.asJson.copy(value = Some(getValue().toString))
}

class CountMetric(
    val group: String, val name: String, val title: String, val description: String,
    override val master: Option[Metric] = None) extends AbstractMetric[Long] {
  val `type`: String = "counter"

  private val _count = new AtomicLong()
  def recordCount(count: Long): Long = _count.addAndGet(count)
  def increment(): Long = recordCount(1)

  def count = _count.get
  val getValue = () => count

  override def asJson: StatusMetric = super.asJson.copy(count = Some(getValue().toString))
}

class TimingMetric(
    val group: String, val name: String, val title: String, val description: String,
    override val master: Option[Metric] = None) extends AbstractMetric[Long] {
  val `type` = "timer"

  private val _totalTimeInMillis = new AtomicLong()
  private val _count = new AtomicLong()

  def recordTimeSpent(durationInMillis: Long) {
    _totalTimeInMillis.addAndGet(durationInMillis)
    _count.incrementAndGet
  }

  def totalTimeInMillis = _totalTimeInMillis.get
  def count = _count.get
  val getValue = () => totalTimeInMillis

  override def asJson: StatusMetric = super.asJson.copy(
    count = Some(count.toString),
    totalTime = Some(totalTimeInMillis.toString)
  )

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
  def empty = new TimingMetric("application", "Empty", "Empty", "Empty")
}

class ExtendedTimingMetric(override val group: String,
    override val name: String,
    override val title: String,
    override val description: String,
    override val master: Option[Metric] = None,
    val percentiles: List[Int]) extends TimingMetric(group, name, title, description, master) {
  val masterMetric = new TimingMetric(group, name, title, description)
  val submetrics: Map[String, TimingMetric] = (percentiles map { pct: Int =>
    val subname = name + "_" + pct
    val subdesc = description + " (" + pct + "% percentile)"
    subname -> new TimingMetric(group, subname, title, subdesc, Some(masterMetric))
  }).toMap + (name -> masterMetric)

  var storedMetrics = new AtomicReference(new mutable.MutableList[Long]())

  /* For extended metrics, this is actually going to do the work */
  //  def asJson = ???
  override def recordTimeSpent(durationInMillis: Long) {
    storedMetrics.get += durationInMillis
    masterMetric.recordTimeSpent(durationInMillis)
  }

  def processMetrics() {
    val metricList = storedMetrics.getAndSet(new mutable.MutableList[Long]())
    println("Total " + metricList.size)
    val sortedMetrics = metricList.sorted

    percentiles foreach { pct =>
      val offset = math.round(sortedMetrics.size * (pct / 100.0))
      println("Recording only " + offset + " values")
      val metric = submetrics(name + "_" + pct)
      sortedMetrics.take(offset.toInt).foreach(metric.recordTimeSpent(_))
    }
  }

  override def asJson = masterMetric.asJson

  override def json = {
    // Side Effect, update the submetrics appropriately
    processMetrics()
    List(masterMetric.asJson) ::: submetrics.values.map(_.asJson).toList
  }
}
