package com.gu.management.metrics

import com.gu.management.GaugeMetric
import java.lang.management.ManagementFactory

object SystemMetrics {

  // divide by 1048576 to convert bytes to MB

  object MaxHeapMemoryMetric extends GaugeMetric("system", "max-heap-memory", "Max heap memory (MB)", "Max heap memory (MB)",
    () => ManagementFactory.getMemoryMXBean.getHeapMemoryUsage.getMax / 1048576
  )

  object UsedHeapMemoryMetric extends GaugeMetric("system", "used-heap-memory", "Used heap memory (MB)", "Used heap memory (MB)",
    () => ManagementFactory.getMemoryMXBean.getHeapMemoryUsage.getUsed / 1048576
  )

  object MaxNonHeapMemoryMetric extends GaugeMetric("system", "max-non-heap-memory", "Max non heap memory (MB)", "Max non heap memory (MB)",
    () => ManagementFactory.getMemoryMXBean.getNonHeapMemoryUsage.getMax / 1048576
  )

  object UsedNonHeapMemoryMetric extends GaugeMetric("system", "used-non-heap-memory", "Used non heap memory (MB)", "Used non heap memory (MB)",
    () => ManagementFactory.getMemoryMXBean.getNonHeapMemoryUsage.getUsed / 1048576
  )

  //  http://docs.oracle.com/javase/6/docs/api/java/lang/management/OperatingSystemMXBean.html()
  object LoadAverageMetric extends GaugeMetric("system", "load-average", "Load average", "Load average",
    () => ManagementFactory.getOperatingSystemMXBean.getSystemLoadAverage
  )

  object AvailableProcessorsMetric extends GaugeMetric("system", "available-processors", "Available processors", "Available processors",
    () => ManagementFactory.getOperatingSystemMXBean.getAvailableProcessors
  )

  // yeah, casting to com.sun.. ain't too pretty
  object TotalPhysicalMemoryMetric extends GaugeMetric("system", "total-physical-memory", "Total physical memory", "Total physical memory",
    () => ManagementFactory.getOperatingSystemMXBean match {
      case b: com.sun.management.OperatingSystemMXBean => b.getTotalPhysicalMemorySize
      case _ => -1
    }
  )

  object FreePhysicalMemoryMetric extends GaugeMetric("system", "free-physical-memory", "Free physical memory", "Free physical memory",
    () => ManagementFactory.getOperatingSystemMXBean match {
      case b: com.sun.management.OperatingSystemMXBean => b.getFreePhysicalMemorySize
      case _ => -1
    }
  )

  val all = Seq(MaxHeapMemoryMetric, UsedHeapMemoryMetric, MaxNonHeapMemoryMetric, UsedNonHeapMemoryMetric,
    LoadAverageMetric, AvailableProcessorsMetric,
    TotalPhysicalMemoryMetric, FreePhysicalMemoryMetric
  )

}
