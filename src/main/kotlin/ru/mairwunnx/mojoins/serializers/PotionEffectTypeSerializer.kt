package ru.mairwunnx.mojoins.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.potion.PotionEffectType

object PotionEffectTypeSerializer : KSerializer<PotionEffectType> {
  override val descriptor = PrimitiveSerialDescriptor("PotionEffectType", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: PotionEffectType) {
    val key: NamespacedKey? = runCatching { Registry.POTION_EFFECT_TYPE.getKey(value) }.getOrNull()
    val out = key?.asString() ?: runCatching { value.key.asString() }.getOrDefault("minecraft:resistance")
    encoder.encodeString(out)
  }

  override fun deserialize(decoder: Decoder): PotionEffectType {
    val raw = decoder.decodeString().trim()
    val key = NamespacedKey.fromString(raw)
    return if (key != null) {
      Registry.POTION_EFFECT_TYPE.get(key) ?: PotionEffectType.RESISTANCE
    } else {
      val fallbackKey = NamespacedKey.minecraft(raw.lowercase())
      Registry.POTION_EFFECT_TYPE.get(fallbackKey) ?: PotionEffectType.RESISTANCE
    }
  }
}