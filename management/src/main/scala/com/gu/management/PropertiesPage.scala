package com.gu.management

/**
 * This is a simple placeholder for a properties page.
 * This does not include dependencies to com.gu.configuration but takes a string to be printed, any dependency
 * should be handled by the calling application appropriately. This placeholder is to avoid confusion about
 * an applications responsibility about where this should be included. The configuration to be printed here
 * should already have passwords and keys masked and new line characters included.
 *
 * @param config to be printed which should be obfuscated where appropriate and include newline characters
 */
class PropertiesPage(config: String) extends ManagementPage {
  val path = "/management/properties"
  def get(request: HttpRequest) = PlainTextResponse(config)
}
