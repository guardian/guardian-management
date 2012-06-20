package com.gu.management

import java.util.concurrent.atomic.AtomicBoolean

/**
 * This trait should be used by anything that wants to read
 * the state of a switch
 */
trait Switch {
  def isSwitchedOn: Boolean
  def isSwitchedOff: Boolean = !isSwitchedOn

  def opt[T](block: => T): Option[T] = if (isSwitchedOn) Some(block) else None
}

object Switch {
  object On {
    def unapply(switch: Switch): Boolean = switch.isSwitchedOn
  }

  object Off {
    def unapply(switch: Switch): Boolean = switch.isSwitchedOff
  }
}

/**
 * This trait should be used by anything that wants to
 * mutate the state of a switch
 */
trait Switchable extends Switch {
  def switchOn()
  def switchOff()

  /**
   * @return a single url-safe word that can be used to construct urls
   * for this switch.
   */
  def name: String

  /**
   * @return a sentence that describes, in websys understandable terms, the
   * effect of switching this switch
   */
  def description: String
}

/**
 * A simple implementation of Switchable that does the right thing in most cases
 */
case class DefaultSwitch(name: String, description: String, initiallyOn: Boolean = true) extends Switchable with Loggable {
  private val isOn = new AtomicBoolean(initiallyOn)

  def isSwitchedOn = isOn.get

  def switchOn() {
    logger.info("Switching on " + name)
    isOn set true
  }

  def switchOff() {
    logger.info("Switching off " + name)
    isOn set false
  }
}

class Switchboard(val applicationName: String, switches: Seq[Switchable]) extends HtmlManagementPage with Postable {
  val title = "Switchboard"
  val path = "/management/switchboard"

  def body(r: HttpRequest) = {
    val switchToShow = r getParameter "switch"
    def shouldShow(s: Switchable) = switchToShow.map(s.name ==).getOrElse(true)

    <form method="POST">
      <table border="1">
        <tr><th>Switch name</th><th>Description</th><th>State</th></tr>
        { for (switch <- switches.filter(shouldShow(_)).sortBy(_.name)) yield renderSwitch(switch) }
      </table>
    </form>
  }

  private def renderSwitch(s: Switchable) =
    <tr>
      <td><a href={ "?switch=" + s.name }>{ s.name }</a></td>
      <td>{ s.description }</td>
      <td style="width: 100px; text-align: center;">{ renderButtons(s) }</td>
    </tr>

  private def renderButtons(s: Switchable) =
    if (s.isSwitchedOn)
      <xml:group><span style="color: ForestGreen"> ON </span><input type="submit" name={ s.name } value="OFF"/></xml:group>
    else
      <xml:group><input type="submit" name={ s.name } value="ON"/><span style="color: DarkRed"> OFF </span></xml:group>

  def post(r: HttpRequest) {
    for (switch <- switches) {
      r.getParameter(switch.name) match {
        case Some("ON") => switch.switchOn()
        case Some("OFF") => switch.switchOff()
        case Some(other) => sys.error("Expected ON or OFF as value for " + switch.name + " parameter")
        case _ => // ignore
      }
    }
  }
}
