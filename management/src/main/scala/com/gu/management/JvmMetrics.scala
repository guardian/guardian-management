package com.gu.management

import management.ManagementFactory
import collection.JavaConversions._

object JvmMetrics {

  lazy val all = numThreads :: totalThreads :: gcRates

  lazy val numThreads = new GaugeMetric(
    group = "jvm",
    name = "num_threads",
    title = "Number of active threads",
    description = "Number of threads currently active as reported by the jvm",
    getCount = () => ManagementFactory.getThreadMXBean.getThreadCount
  )

  lazy val totalThreads = new GaugeMetric(
    group = "jvm",
    name = "total_threads",
    title = "Thread started threads",
    description = "Threads started since the application started as reported by the jvm",
    getCount = () => ManagementFactory.getThreadMXBean.getTotalStartedThreadCount
  )

  lazy val gcRates = ManagementFactory.getGarbageCollectorMXBeans.toList map { gc =>
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
}
