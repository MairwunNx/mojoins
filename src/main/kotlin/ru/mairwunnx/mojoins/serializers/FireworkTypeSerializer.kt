package ru.mairwunnx.mojoins.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.FireworkEffect

object FireworkTypeSerializer : KSerializer<FireworkEffect.Type> {
  override val descriptor = PrimitiveSerialDescriptor("FireworkType", PrimitiveKind.STRING)
  override fun serialize(encoder: Encoder, value: FireworkEffect.Type) = encoder.encodeString(value.name)
  override fun deserialize(decoder: Decoder) = FireworkEffect.Type.valueOf(decoder.decodeString().trim().uppercase())
}