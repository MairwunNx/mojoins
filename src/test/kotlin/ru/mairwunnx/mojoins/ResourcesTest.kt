package ru.mairwunnx.mojoins

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResourcesTest {
  private fun resource(path: String) = javaClass.classLoader.getResource(path)!!.readText()

  @Test fun `the bundled config matches the defaults`() {
    assertEquals(Settings(), Settings.parse(resource("config.yml")))
  }

  @Test fun `an old or partial config still loads`() {
    val settings = Settings.parse("debug: true\nmessages: { join: hi }\nfirework: { enabled: true }")
    assertTrue(settings.debug)
    assertTrue(settings.firework.enabled)
    assertEquals("en", settings.locales.default)
  }

  @Test fun `every bundled language sets every message`() {
    Languages.BUNDLED.forEach { code ->
      val messages = Settings.parseMessages(resource("lang/$code.yml"))
      val missing = Message.entries.map { it.key }.filter { it !in messages }
      assertEquals(emptyList(), missing, "lang/$code.yml misses keys")
    }
  }
}
