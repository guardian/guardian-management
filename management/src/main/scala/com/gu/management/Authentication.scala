package com.gu.management

case class UserCredentials(username: String, password: String)
object UserCredentials {
  val missing = UserCredentials("", "")
}
trait UserProvider {
  def realm: String
  def isValid(credentials: UserCredentials): Boolean
}