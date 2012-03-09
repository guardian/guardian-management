package com.gu.management

object HttpRequestsTimingMetric extends TimingMetric(
  group = "application",
  name = "http-requests",
  title = "HTTP requests",
  description = "HTTP requests as determined by the request logging filter")

object ExceptionCountMetric extends CountMetric(
  group = "application",
  name = "exception-count",
  title = "exception-count",
  description = "Counts the number of uncaught exceptions being sent to the client from the application"
)

object ServerErrorCounter extends CountMetric(
  group = "application",
  name = "server-error",
  title = "server-error",
  description = "The number of 5XX errors returned by the application")

object ClientErrorCounter extends CountMetric(
  group = "application",
  name = "client-error",
  title = "client-error",
  description = "The number of 4XX errors returned by the application")
