@file:UseSerializers(SoundSerializer::class, ParticleSerializer::class, ComponentSerializer::class, ColorSerializer::class, PotionEffectTypeSerializer::class)

package ru.mairwunnx.mojoins.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Color
import org.bukkit.Color.fromRGB
import org.bukkit.FireworkEffect
import org.bukkit.FireworkEffect.Type.BALL
import org.bukkit.Particle
import org.bukkit.Particle.HAPPY_VILLAGER
import org.bukkit.Sound
import org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP
import org.bukkit.Sound.ENTITY_PLAYER_LEVELUP
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionEffectType.RESISTANCE
import ru.mairwunnx.mojoins.models.GeneralConfigurationModel.Messages.System
import ru.mairwunnx.mojoins.models.GeneralConfigurationModel.Sounds.SoundSpec
import ru.mairwunnx.mojoins.models.GeneralConfigurationModel.Visuals.Firework
import ru.mairwunnx.mojoins.models.GeneralConfigurationModel.Visuals.Particles
import ru.mairwunnx.mojoins.models.GeneralConfigurationModel.Visuals.Particles.Ring
import ru.mairwunnx.mojoins.serializers.ColorSerializer
import ru.mairwunnx.mojoins.serializers.ComponentSerializer
import ru.mairwunnx.mojoins.serializers.ParticleSerializer
import ru.mairwunnx.mojoins.serializers.PotionEffectTypeSerializer
import ru.mairwunnx.mojoins.serializers.SoundSerializer

private val mm = MiniMessage.miniMessage()
private fun c(s: String): Component = mm.deserialize(s)

@Serializable class GeneralConfigurationModel(
  @SerialName("enabled") val enabled: Boolean,
  @SerialName("debug") val debug: Boolean,
  @SerialName("messages") val messages: Messages,
  @SerialName("effects") val effects: List<JoinEffect>,
  @SerialName("sounds") val sounds: Sounds,
  @SerialName("visuals") val visuals: Visuals,
  @SerialName("broadcast") val broadcast: Broadcast
) {
  @Serializable class Messages(
    @SerialName("join") val join: Component,
    @SerialName("first_join") val firstJoin: Component,
    @SerialName("quit") val quit: Component,
    @SerialName("system") val system: System,
  ) {
    @Serializable class System(
      @SerialName("permission") val permission: Component,
      @SerialName("incorrect") val incorrect: Component,
      @SerialName("reloaded") val reloaded: Component,
      @SerialName("reload_failed") val reloadFailed: Component
    )
  }

  @Serializable class JoinEffect(
    @SerialName("name") val name: PotionEffectType,
    @SerialName("duration") val duration: Int,
    @SerialName("amplifier") val amplifier: Int
  )

  @Serializable class Sounds(
    @SerialName("join") val join: SoundSpec,
    @SerialName("first_join") val firstJoin: SoundSpec
  ) {
    @Serializable class SoundSpec(
      @SerialName("enabled") val enabled: Boolean,
      @SerialName("key") val key: Sound,
      @SerialName("volume") val volume: Double,
      @SerialName("pitch") val pitch: Double
    )
  }

  @Serializable class Visuals(
    @SerialName("particles") val particles: Particles,
    @SerialName("firework") val firework: Firework
  ) {
    @Serializable class Particles(
      @SerialName("enabled") val enabled: Boolean,
      @SerialName("type") val type: Particle,
      @SerialName("ring") val ring: Ring
    ) {
      @Serializable class Ring(
        @SerialName("radius") val radius: Double,
        @SerialName("points") val points: Int
      )
    }

    @Serializable class Firework(
      @SerialName("enabled") val enabled: Boolean,
      @SerialName("power") val power: Int,
      @SerialName("type") val type: FireworkEffect.Type,
      @SerialName("flicker") val flicker: Boolean,
      @SerialName("trail") val trail: Boolean,
      @SerialName("colors") val colors: List<Color>,
      @SerialName("fade_colors") val fadeColors: List<Color>
    )
  }

  @Serializable class Broadcast(
    @SerialName("join_enabled") val joinEnabled: Boolean,
    @SerialName("first_join_enabled") val firstJoinEnabled: Boolean,
    @SerialName("quit_enabled") val quitEnabled: Boolean
  )

  companion object {
    fun default() = GeneralConfigurationModel(
      enabled = true,
      debug = false,
      messages = Messages(
        join = c("<green>➕</green> <white><player></white> <gray>зашёл на сервер.</gray>"),
        firstJoin = c(
          """
          <gradient:#ffd54f:#ffa000>✦ Добро пожаловать, <white><player></white>!</gradient>
          <gray>Мы рады видеть вас на сервере — приятной игры и ярких приключений.</gray>
          <gray>— Уважайте других игроков.</gray>
          <gray>— Стройте с умом и бережно относитесь к миру.</gray>
          <gray>— Если что-то непонятно — задайте вопрос в чате.</gray>
          <gradient:#ffa000:#ffd54f>✦ Удачи и веселья!</gradient>
          """.trimIndent()
        ),
        quit = c("<red>➖</red> <white><player></white> <gray>покинул сервер.</gray>"),
        system = System(
          permission = c("<red>Недостаточно прав.</red>"),
          incorrect = c("<yellow>Неверная команда.</yellow> <gray>Используйте:</gray> <white>/mojoins reload</white>"),
          reloaded = c("<green>Настройки перезагружены.</green>"),
          reloadFailed = c("<red>Перезагрузка не удалась.</red> <gray>Проверьте логи сервера.</gray>")
        ),
      ),
      effects = listOf(
        JoinEffect(name = RESISTANCE, duration = 10, amplifier = 0)
      ),
      sounds = Sounds(
        join = SoundSpec(true, ENTITY_EXPERIENCE_ORB_PICKUP, 1.0, 1.0),
        firstJoin = SoundSpec(true, ENTITY_PLAYER_LEVELUP, 1.0, 1.0)
      ),
      visuals = Visuals(
        particles = Particles(
          enabled = true,
          type = HAPPY_VILLAGER,
          ring = Ring(radius = 1.5, points = 12)
        ),
        firework = Firework(
          enabled = true, power = 1, type = BALL,
          flicker = true, trail = true,
          colors = listOf(fromRGB(0xFF, 0xD5, 0x4F), fromRGB(0xFF, 0xFF, 0xFF)),
          fadeColors = listOf(fromRGB(0xFF, 0xA0, 0x00))
        )
      ),
      broadcast = Broadcast(joinEnabled = true, firstJoinEnabled = true, quitEnabled = true)
    )
  }
}

