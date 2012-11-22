package controllers

import play.api.mvc._
import com.gu.management.Switch.On
import play.api.libs.concurrent.Akka

object ScalaApp extends Controller {
  def apply() = Action {
    conf.Switches.takeItDown match {
      case On() => InternalServerError("Temporarily switched off!")
      case _ => Ok("Thank you for invoking this app!")
    }
  }

  def exception() = Action {
    throw new Exception("Expected exception.")
    InternalServerError("Unreachable")
  }

  def long() = Action {
    Thread.sleep(2000)
    Ok("Slept OK")
  }

  def async() = Action {
    Async {
      import play.api.Play.current
      Akka.future {
        Thread.sleep(2000)
        Ok("Slept OK")
      }
    }
  }
}