package ru.mairwunnx.mojoins

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LanguagesTest {
  private val languages = Languages(listOf("it", "de", "ru", "pt_br", "en").associateWith { emptyMap() }, "en")

  @Test fun `bundled languages come first, in their order`() {
    assertEquals(listOf("en", "ru", "de", "it", "pt_br"), languages.choices)
  }

  @Test fun `a typed code finds its file`() {
    assertEquals("ru", languages.match("RU"))
    assertEquals("pt_br", languages.match("pt-BR"))
    assertEquals("de", languages.match("de_AT"))
    assertNull(languages.match("fr"))
  }

  @Test fun `languages show their own names`() {
    assertEquals("Русский", languages.name("ru"))
    assertEquals("it", languages.name("it"))
  }
}
