package ru.mairwunnx.mojoins.managers

import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import ru.mairwunnx.mojoins.PluginUnit
import ru.mairwunnx.mojoins.models.GeneralConfigurationModel
import java.io.Closeable
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class EffectsManager(private val plugin: PluginUnit) : Closeable {
  fun apply(player: Player, isFirstJoin: Boolean) {
    val cfg = plugin.configuration[GeneralConfigurationModel::class.java]

    if (isFirstJoin && cfg.sounds.firstJoin.enabled) {
      player.playSound(player.location, cfg.sounds.firstJoin.key, cfg.sounds.firstJoin.volume.toFloat(), cfg.sounds.firstJoin.pitch.toFloat())
      plugin.logger.debug { "Play first-join sound for ${player.name}" }
    } else if (cfg.sounds.join.enabled) {
      player.playSound(player.location, cfg.sounds.join.key, cfg.sounds.join.volume.toFloat(), cfg.sounds.join.pitch.toFloat())
      plugin.logger.debug { "Play join sound for ${player.name}" }
    }

    if (cfg.visuals.particles.enabled) {
      particles(player.location.clone().add(0.0, 1.0, 0.0), cfg)
      plugin.logger.debug { "Spawn join particles for ${player.name}" }
    }

    if (cfg.visuals.firework.enabled) {
      firework(player.location, cfg)
      plugin.logger.debug { "Launch firework for ${player.name}" }
    }

    effects(player, cfg)
  }

  private fun particles(center: Location, cfg: GeneralConfigurationModel) {
    val world = center.world ?: return
    val r = cfg.visuals.particles.ring.radius
    val n = cfg.visuals.particles.ring.points.coerceAtLeast(4)
    for (i in 0 until n) {
      val a = 2.0 * PI * i / n
      val spot = center.clone().add(cos(a) * r, 0.0, sin(a) * r)
      world.spawnParticle(cfg.visuals.particles.type, spot, 1, 0.05, 0.05, 0.05, 0.0)
    }
  }

  private fun firework(center: Location, cfg: GeneralConfigurationModel) {
    val world = center.world ?: return
    val fw = world.spawnEntity(center.clone().add(0.0, 1.0, 0.0), EntityType.FIREWORK_ROCKET) as Firework
    fw.isSilent = true
    val meta = fw.fireworkMeta
    meta.power = cfg.visuals.firework.power

    val effect = org.bukkit.FireworkEffect.builder()
      .with(cfg.visuals.firework.type)
      .flicker(cfg.visuals.firework.flicker)
      .trail(cfg.visuals.firework.trail)
      .withColor(cfg.visuals.firework.colors)
      .withFade(cfg.visuals.firework.fadeColors)
      .build()

    meta.clearEffects()
    meta.addEffect(effect)
    fw.fireworkMeta = meta
  }

  private fun effects(player: Player, cfg: GeneralConfigurationModel) {
    cfg.effects.forEach { e ->
      val durationTicks = (e.duration.coerceAtLeast(0)) * 20
      val amplifier = e.amplifier.coerceAtLeast(0)

      val applied = player.addPotionEffect(PotionEffect(e.name, durationTicks, amplifier, false, false, true))
      if (applied) {
        plugin.logger.debug { "Applied effect ${e.name.key.key} x${amplifier + 1} for ${e.duration}s to ${player.name}" }
      } else {
        plugin.logger.debug { "Effect ${e.name.key.key} not applied (conflict/stronger exists) for ${player.name}" }
      }
    }
  }

  override fun close() = Unit
}