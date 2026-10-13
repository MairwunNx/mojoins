package ru.mairwunnx.mojoins

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ColorsTest {
  @Test fun `reads hex, rgb, and names`() {
    assertEquals(0xFFD54F, Colors.parse("#FFD54F"))
    assertEquals(0x0A141E, Colors.parse("10, 20, 30"))
    assertEquals(0x0A141E, Colors.parse("rgb(10,20,30)"))
    assertEquals(0x87CEEB, Colors.parse(" Sky "))
  }

  @Test fun `rejects broken colors instead of throwing`() {
    assertNull(Colors.parse("#FFF"))
    assertNull(Colors.parse("10,20"))
    assertNull(Colors.parse("10,20,300"))
    assertNull(Colors.parse("mauve"))
  }
}
