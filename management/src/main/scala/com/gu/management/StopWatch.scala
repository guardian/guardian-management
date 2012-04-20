package com.gu.management

import org.slf4j.Logger
import java.util.concurrent.Callable

class StopWatch {
  val startTime = System.currentTimeMillis
  def elapsed = System.currentTimeMillis - startTime
}

object Timing {

  def debug[T](logger: Logger, activity: String): (=> T) => T =
    time(activity, logger.debug, logger.debug, None)

  def debug[T](logger: Logger, activity: String, metric: TimingMetric): (=> T) => T =
    time(activity, logger.debug, logger.debug, Some(metric))

  def info[T](logger: Logger, activity: String): (=> T) => T =
    time(activity, logger.info, logger.info, None)

  def info[T](logger: Logger, activity: String, metric: TimingMetric): (=> T) => T =
    time(activity, logger.info, logger.info, Some(metric))

  def time[T](activity: String,
    onSuccess: String => Unit,
    onFailure: (String, Throwable) => Unit,
    metric: Option[TimingMetric])(block: => T): T = {
    val stopWatch = new StopWatch
    try {
      val result = block
      metric foreach (_.recordTimeSpent(stopWatch.elapsed))
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
