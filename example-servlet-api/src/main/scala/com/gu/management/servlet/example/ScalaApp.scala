package com.gu.management.servlet.example

import javax.servlet.http.{ HttpServletResponse, HttpServletRequest, HttpServlet }
import com.gu.management.Switch.On
import util.Random

class ScalaApp extends HttpServlet {
  protected override def doPost(request: HttpServletRequest, response: HttpServletResponse) {
  }

  protected override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    TimingMetrics.requests measure {
      Thread.sleep(Random.nextInt(8) ^ 2)
      Switches.takeItDown match {
        case On() => response.sendError(500, "Temporarily switched off!")
        case _ => response.getWriter.println("Thank you for invoking this app!")
      }
    }
  }
}
