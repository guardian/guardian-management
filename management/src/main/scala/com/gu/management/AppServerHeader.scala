package com.gu.management

import java.net.{ UnknownHostException, InetAddress }
import io.Source
import util.control.Exception._

trait AppServerHeader {
  def appServerHeader = "X-GU-jas" -> (hostIdentifier + "!" + threadname)

  lazy val hostIdentifier = hostHash getOrElse hostname.takeRight(2)

  lazy val hostname = handling(classOf[UnknownHostException]) by (_ => "????") apply {
    InetAddress.getLocalHost.getHostName.toLowerCase
  }

  def threadname = Thread.currentThread().getName

  lazy val hostHash = installVarsMap.get("HOST_HASH")

  private def installVarsMap =
    (installVarsContent map {
      _.split("=")
    } collect {
      case Array(k, v) => k -> v
    }).toMap

  protected def installVarsContent = handling(classOf[Exception]) by (_ => Nil) apply {
    Source.fromFile("/etc/gu/install_vars").getLines().toList
  }
}

object AppServerHeader extends AppServerHeader {
  def apply() = appServerHeader
}

