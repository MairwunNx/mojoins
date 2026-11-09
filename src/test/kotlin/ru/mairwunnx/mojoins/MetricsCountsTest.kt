package ru.mairwunnx.mojoins

import kotlin.test.Test
import kotlin.test.assertEquals

class MetricsCountsTest {
  @Test fun `a drain returns the period's counts and starts over`() {
    val counts = MetricsCounts()
    counts.pick("ru", "ip")
    counts.pick("ru", "ip")
    counts.pick("en", "default")
    counts.firstJoin()
    assertEquals(mapOf("ru" to 2, "en" to 1), counts.drainLanguages())
    assertEquals(mapOf("ip" to 2, "default" to 1), counts.drainSources())
    assertEquals(1, counts.drainFirstJoins())
    assertEquals(emptyMap(), counts.drainLanguages())
    assertEquals(emptyMap(), counts.drainSources())
    assertEquals(0, counts.drainFirstJoins())
  }

  @Test fun `only language codes go out`() {
    assertEquals("pt_br", MetricsCounts.safeCode("pt_br"))
    assertEquals("de", MetricsCounts.safeCode("de"))
    assertEquals("other", MetricsCounts.safeCode("my server's texts"))
  }

  @Test fun `a pick says how the language was picked`() {
    val resolver = LocaleResolver(Settings.Locales(), setOf("en", "ru", "de"))
    assertEquals(LocaleResolver.Pick("ru", "ip"), resolver.pick("RU", java.util.Locale.GERMAN))
    assertEquals(LocaleResolver.Pick("de", "client"), resolver.pick("US", java.util.Locale.GERMAN))
    assertEquals(LocaleResolver.Pick("en", "default"), resolver.pick(null, java.util.Locale.JAPANESE))
    assertEquals(LocaleResolver.Pick("de", "choice"), resolver.pick("RU", null, chosen = "de"))
  }
}
