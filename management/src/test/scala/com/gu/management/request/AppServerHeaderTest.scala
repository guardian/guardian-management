package com.gu.management

import org.specs2.mutable.Specification

class AppServerHeaderTest extends Specification {
  "app server header calculator" should {

    "read host hash key from /etc/gu/install_vars" in {
      object appServerHeader extends AppServerHeader {
        override def installVarsContent =
          "STAGE=DEV" ::
            "INT_SERVICE_DOMAIN=gudev.gnl" ::
            "EXT_SERVICE_DOMAIN=" ::
            "HOST_HASH=abcdefg" :: Nil
      }

      appServerHeader.hostHash must_== Some("abcdefg")
    }

    "not blow up if host hash not found" in {
      object appServerHeader extends AppServerHeader {
        override def installVarsContent =
          "STAGE=DEV" ::
            "INT_SERVICE_DOMAIN=gudev.gnl" ::
            "EXT_SERVICE_DOMAIN=" :: Nil
      }

      appServerHeader.hostHash must_== None
    }

    "use host hash to create host identifier if present" in {
      object appServerHeader extends AppServerHeader { override lazy val hostHash = Some("abc") }
      appServerHeader.hostIdentifier must_== "abc"
    }

    "use last two digits of host name if host identifier not present" in {
      object appServerHeader extends AppServerHeader {
        override lazy val hostHash = None
        override lazy val hostname = "abcdefgh"
      }
      appServerHeader.hostIdentifier must_== "gh"
    }

    "return hostname and thread in a string" in {

      // there's no point in mirroring the code here
      // so please test via visual inspection ;)
      val (key, value) = AppServerHeader()
      println("header is '" + value + "'")
      key must_== "X-GU-jas"
    }
  }

}