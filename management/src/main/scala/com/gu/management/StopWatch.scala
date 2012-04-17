package com.gu.management

import org.slf4j.Logger
import java.util.concurrent.Callable

class StopWatch {
  val startTime = System.currentTimeMillis
  def elapsed = System.currentTimeMillis - startTime
}

object Timing {

  def debug[T](logger: Logger, activity: String, metric: TimingMetric = TimingMetric._noOp)(block: => T): T =
    time(activity, logger.debug, logger.debug, metric)(block)

  def info[T](logger: Logger, activity: String, metric: TimingMetric = TimingMetric._noOp)(block: => T): T =
    time(activity, logger.info, logger.info, metric)(block)

  def time[T](
    activity: String,
    onSuccess: (String) => Unit,
    onFailure: (String, Throwable) => Unit,
    metric: TimingMetric = TimingMetric._noOp)(block: => T): T = {
    val stopWatch = new StopWatch
    try {
      val result = block
      metric recordTimeSpent stopWatch.elapsed
      onSuccess(activity + " completed in " + stopWatch.elapsed + " ms")
      result
    } catch {
      case t: Throwable =>
        onFailure(activity + " caused exception after " + stopWatch.elapsed + " ms", t)
        throw t
    }
  }

  def debug[T](logger: Logger, activity: String, metric: TimingMetric, callable: Callable[T]): T =
    debug(logger, activity, metric)(callable.call())

  def info[T](logger: Logger, activity: String, metric: TimingMetric, callable: Callable[T]): T =
    info(logger, activity, metric)(callable.call())

  def debug(logger: Logger, activity: String, metric: TimingMetric, callable: Runnable): Unit =
    debug(logger, activity, metric)(callable.run())

  def info(logger: Logger, activity: String, metric: TimingMetric, callable: Runnable): Unit =
    info(logger, activity, metric)(callable.run())
}
