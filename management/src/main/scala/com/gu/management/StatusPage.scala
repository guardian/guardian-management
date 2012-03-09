package com.gu.management

import java.util.Date

case class StatusResponseJson(
  application: String,
  time: Long = new Date().getTime,
  metrics: Seq[StatusMetric] = Nil)

object StatusPage {
  def apply(application: String, metrics: Seq[Metric]) = new StatusPage(application, () => metrics)

  def apply(application: String, metricsCallback: () => Seq[Metric]) =
    new StatusPage(application, metricsCallback)
}

class StatusPage(application: String, metrics: () => Seq[Metric]) extends JsonManagementPage {
  val path = "/management/status"
  def jsonObj = StatusResponseJson(application = application, metrics = metrics() map (_.asJson))
}