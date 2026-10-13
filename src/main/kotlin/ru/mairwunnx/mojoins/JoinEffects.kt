package ru.mairwunnx.mojoins

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Sounds, particles, a firework, and potion effects for a joining player. Built from [Settings] on
 * every load: ids resolve against the server's registries once, and a bad one logs a warning and
 * drops out instead of failing on each join.
 */
class JoinEffects(settings: Settings, private val log: Log, val fireworkKey: NamespacedKey) {
  private val joinSound = settings.sounds.join.toSound("sounds.join")
  private val firstJoinSound = settings.sounds.firstJoin.toSound("sounds.first_join")

  private val particle: Particle? = settings.particles.takeIf { it.enabled }?.let { spec ->
    entry(RegistryKey.PARTICLE_TYPE, spec.type, "particles.type")?.takeIf { particle ->
      // Particles like dust need extra data this ring doesn't have.
      (particle.dataType == Void::class.java).also { if (!it) log.warn("particles.type: ${spec.type} needs extra data; pick another particle") }
    }
  }
  private val radius = settings.particles.radius
  private val points = settings.particles.points.coerceIn(4, 64)

  private val firework = settings.firework.takeIf { it.enabled }
  private val fireworkEffect: FireworkEffect? = firework?.let { spec ->
    val type = FireworkEffect.Type.entries.firstOrNull { it.name.equals(spec.type.trim(), ignoreCase = true) }
      ?: FireworkEffect.Type.BALL.also { log.warn("firework.type: unknown type ${spec.type}, using BALL") }
    FireworkEffect.builder().with(type).flicker(spec.flicker).trail(spec.trail)
      .withColor(colors(spec.colors, "firework.colors").ifEmpty { listOf(Color.WHITE) })
      .withFade(colors(spec.fadeColors, "firework.fade_colors"))
      .build()
  }

  private val potions: List<PotionEffect> = settings.effects.mapIndexedNotNull { i, effect ->
    entry(RegistryKey.MOB_EFFECT, effect.name, "effects[$i].name")?.let {
      PotionEffect(it, effect.duration.coerceAtLeast(0) * 20, effect.amplifier.coerceAtLeast(0), false, false, true)
    }
  }

  /** Whether players near the rocket get hurt by its blast. */
  val fireworkDamage = firework?.damage ?: false

  fun play(player: Player, firstJoin: Boolean) {
    (if (firstJoin) firstJoinSound else joinSound)?.let { player.playSound(it, Sound.Emitter.self()) }
    particle?.let { ring(player, it) }
    fireworkEffect?.let { launch(player, it) }
    potions.forEach { player.addPotionEffect(it) }
  }

  private fun ring(player: Player, particle: Particle) {
    val center = player.location.add(0.0, 1.0, 0.0)
    for (i in 0 until points) {
      val angle = 2 * PI * i / points
      player.world.spawnParticle(particle, center.clone().add(cos(angle) * radius, 0.0, sin(angle) * radius), 1, 0.05, 0.05, 0.05, 0.0)
    }
  }

  private fun launch(player: Player, effect: FireworkEffect) {
    player.world.spawn(player.location.add(0.0, 1.0, 0.0), Firework::class.java) { rocket ->
      rocket.isSilent = true
      rocket.persistentDataContainer.set(fireworkKey, PersistentDataType.BOOLEAN, true)
      rocket.fireworkMeta = rocket.fireworkMeta.apply {
        power = firework!!.power.coerceIn(0, 3)
        clearEffects()
        addEffect(effect)
      }
    }
  }

  private fun Settings.SoundSpec.toSound(path: String): Sound? {
    if (!enabled) return null
    val key = runCatching { Key.key(key.trim().lowercase()) }.getOrNull()
      ?: return null.also { log.warn("$path.key: \"$key\" isn't a valid sound id") }
    return Sound.sound(key, Sound.Source.MASTER, volume, pitch)
  }

  private fun colors(values: List<String>, path: String) = values.mapNotNull { value ->
    Colors.parse(value)?.let(Color::fromRGB) ?: null.also { log.warn("$path: can't read the color \"$value\"") }
  }

  private fun <T : Keyed> entry(registry: RegistryKey<T>, id: String, path: String): T? {
    val key = NamespacedKey.fromString(id.trim().lowercase())
    return key?.let { RegistryAccess.registryAccess().getRegistry(registry).get(it) }
      ?: null.also { log.warn("$path: unknown id \"$id\"") }
  }
}
