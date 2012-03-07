package com.gu.management

import management.ManagementFactory
import collection.JavaConversions._

object JvmMetrics {

  lazy val all = List(numThreads, totalThreads) ::: gcRates

  lazy val numThreads = new Metric {
    val group = "jvm"
    val name = "num_threads"

    def asJson = StatusMetric(
      group = group,
      name = name,
      `type` = "gauge",
      title = "Number of active threads",
      description = "Number of threads currently active as reported by the jvm",
      value = Some(ManagementFactory.getThreadMXBean.getThreadCount.toString)
    )
  }

  lazy val totalThreads = new Metric {
    val group = "jvm"
    val name = "total_threads"

    def asJson = StatusMetric(
      group = group,
      name = name,
      `type` = "counter",
      title = "Total started threads",
      description = "Threads started since the application started as reported by the jvm",
      value = Some(ManagementFactory.getThreadMXBean.getTotalStartedThreadCount.toString)
    )
  }

  lazy val gcRates = ManagementFactory.getGarbageCollectorMXBeans.toList map { gc =>
    new Metric {
      val group = "jvm"
      val name = "gc_" + gc.getName.toLowerCase.replace(' ', '_')

      def asJson = StatusMetric(
        group = group,
        name = name,
        `type` = "timer",
        title = "GC " + gc.getName,
        description = "Collection rates for the " + gc.getName + " garbage collector",
        count = Some(gc.getCollectionCount.toString),
        totalTime = Some(gc.getCollectionTime.toString)
      )
    }
  }
}
