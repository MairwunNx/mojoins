package ru.mairwunnx.mojoins

/** Parses firework colors: `#RRGGBB`, `rgb(r, g, b)`, `r,g,b`, or a name. Returns 0xRRGGBB, or null for anything else. */
object Colors {
  private val names = mapOf(
    "white" to 0xFFFFFF, "black" to 0x000000, "red" to 0xFF0000, "green" to 0x00FF00,
    "blue" to 0x0000FF, "yellow" to 0xFFFF00, "aqua" to 0x00FFFF, "fuchsia" to 0xFF00FF,
    "orange" to 0xFFA500, "lime" to 0x32CD32, "pink" to 0xFFC0CB, "purple" to 0x800080,
    "navy" to 0x000080, "teal" to 0x008080, "sky" to 0x87CEEB, "gold" to 0xFFD700,
  )

  fun parse(value: String): Int? {
    val text = value.trim().lowercase()
    names[text]?.let { return it }
    if (text.startsWith("#")) return text.drop(1).takeIf { it.length == 6 }?.toIntOrNull(16)

    val parts = text.removePrefix("rgb").trim().removeSurrounding("(", ")").split(",").map { it.trim().toIntOrNull() }
    if (parts.size != 3 || parts.any { it == null || it !in 0..255 }) return null
    val (r, g, b) = parts.map { it!! }
    return (r shl 16) or (g shl 8) or b
  }
}
