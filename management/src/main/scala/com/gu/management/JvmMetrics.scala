package com.gu.management

import management.ManagementFactory
import collection.JavaConversions._
import org.slf4j.{ Logger, LoggerFactory }

object JvmMetrics {
  private val logger = LoggerFactory.getLogger("com.gu.management.JvmMetrics")

  lazy val all = numThreads.toList ::: totalThreads.toList ::: gcRates

  lazy val numThreads =
    try {
      ManagementFactory.getThreadMXBean.getThreadCount
      Some(
        new GaugeMetric(
          group = "jvm",
          name = "num_threads",
          title = "Number of active threads",
          description = "Number of threads currently active as reported by the jvm",
          getValue = () => ManagementFactory.getThreadMXBean.getThreadCount
        )
      )
    } catch {
      case e: Exception =>
        logger.debug("Failed to initialise active threads metric")
        None
    }

  lazy val totalThreads =
    try {
      ManagementFactory.getThreadMXBean.getTotalStartedThreadCount
      Some(
        new GaugeMetric(
          group = "jvm",
          name = "total_threads",
          title = "Thread started threads",
          description = "Threads started since the application started as reported by the jvm",
          getValue = () => ManagementFactory.getThreadMXBean.getTotalStartedThreadCount
        )
      )
    } catch {
      case e: Exception =>
        logger.debug("Failed to initialise total threads metric")
        None
    }

  lazy val gcRates =
    try {
      ManagementFactory.getGarbageCollectorMXBeans.toList map { gc =>
        new TimingMetric(
          group = "jvm",
          name = "gc_" + gc.getName.toLowerCase.replace(' ', '_'),
          title = "GC " + gc.getName,
          description = "Collection rates for the " + gc.getName + " garbage collector"
        ) {
          override def count = gc.getCollectionCount
          override def totalTimeInMillis = gc.getCollectionTime
        }
      }
    } catch {
      case e: Exception =>
        logger.trace("Failed to initialise gc metrics", e)
        Nil
    }
}
