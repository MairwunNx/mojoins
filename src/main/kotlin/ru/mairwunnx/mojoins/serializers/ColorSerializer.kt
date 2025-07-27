package ru.mairwunnx.mojoins.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Color

object ColorSerializer : KSerializer<Color> {
  override val descriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Color) {
    val rgb = (value.red shl 16) or (value.green shl 8) or value.blue
    encoder.encodeString(String.format("#%06X", rgb))
  }

  override fun deserialize(decoder: Decoder): Color {
    val s = decoder.decodeString().trim()
    // #RRGGBB
    if (s.startsWith("#") && s.length == 7) {
      return Color.fromRGB(s.substring(1).toInt(16))
    }
    // rgb(r,g,b)
    if (s.startsWith("rgb", ignoreCase = true)) {
      val inside = s.substringAfter("(").substringBefore(")")
      val (r,g,b) = inside.rgbparts()
      return Color.fromRGB(r, g, b)
    }
    // именованные
    named[s.lowercase()]?.let { return it }

    // попытка «R,G,B»
    val (r,g,b) = s.rgbparts()
    return Color.fromRGB(r, g, b)
  }

  private fun String.rgbparts() = split(",").map(String::trim).map(String::toIntOrNull).mapNotNull { it?.coerceIn(0, 255) }

  private val named = mapOf(
    "white" to Color.fromRGB(0xFFFFFF),
    "black" to Color.fromRGB(0x000000),
    "red" to Color.fromRGB(0xFF0000),
    "green" to Color.fromRGB(0x00FF00),
    "blue" to Color.fromRGB(0x0000FF),
    "yellow" to Color.fromRGB(0xFFFF00),
    "aqua" to Color.fromRGB(0x00FFFF),
    "fuchsia" to Color.fromRGB(0xFF00FF),
    "orange" to Color.fromRGB(0xFFA500),
    "lime" to Color.fromRGB(0x32CD32),
    "pink" to Color.fromRGB(0xFFC0CB),
    "purple" to Color.fromRGB(0x800080),
    "navy" to Color.fromRGB(0x000080),
    "teal" to Color.fromRGB(0x008080),
    "sky" to Color.fromRGB(0x87CEEB),
    "gold" to Color.fromRGB(0xFFD700)
  )
}