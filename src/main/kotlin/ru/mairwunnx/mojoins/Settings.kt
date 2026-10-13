package ru.mairwunnx.mojoins

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

/**
 * `config.yml`. Every field has a default, so a config from an older version, or one that sets only
 * a few keys, still loads. Game ids (sounds, particles, effects) stay strings here and resolve
 * against the server's registries in [JoinEffects], where a bad id turns into a warning.
 */
@Serializable
data class Settings(
  val enabled: Boolean = true,
  val debug: Boolean = false,
  val locales: Locales = Locales(),
  val geoip: GeoIpSettings = GeoIpSettings(),
  val broadcast: Broadcast = Broadcast(),
  val sounds: Sounds = Sounds(),
  val particles: Particles = Particles(),
  val firework: Firework = Firework(),
  val effects: List<Effect> = emptyList(),
) {
  @Serializable
  data class Locales(
    /** The language for players nothing else matches, and for the console. */
    val default: String = "en",
    /** Where a player's language comes from, tried in order: `ip` (country by address), `client` (game language). */
    val sources: List<Source> = listOf(Source.IP, Source.CLIENT),
    /** Language → the ISO 3166 country codes that speak it. Countries not listed fall through to the next source. */
    val countries: Map<String, List<String>> = DEFAULT_COUNTRIES,
  )

  @Serializable
  enum class Source {
    @SerialName("ip") IP,
    @SerialName("client") CLIENT,
  }

  @Serializable
  data class GeoIpSettings(
    val enabled: Boolean = true,
    /** A MaxMind-format country database, plain or gzipped. `{year}` and `{month}` fill in the current month. */
    val url: String = "https://download.db-ip.com/free/dbip-country-lite-{year}-{month}.mmdb.gz",
    @SerialName("refresh_days") val refreshDays: Int = 30,
  )

  @Serializable
  data class Broadcast(
    val join: Boolean = true,
    @SerialName("first_join") val firstJoin: Boolean = true,
    val quit: Boolean = true,
    /** Writes the vanilla “joined the game” and “left the game” lines to the server log, for tools that read it. */
    @SerialName("vanilla_log") val vanillaLog: Boolean = true,
  )

  @Serializable
  data class Sounds(
    val join: SoundSpec = SoundSpec(key = "minecraft:entity.experience_orb.pickup"),
    @SerialName("first_join") val firstJoin: SoundSpec = SoundSpec(key = "minecraft:entity.player.levelup"),
  )

  @Serializable
  data class SoundSpec(
    val enabled: Boolean = true,
    val key: String,
    val volume: Float = 1f,
    val pitch: Float = 1f,
  )

  @Serializable
  data class Particles(
    val enabled: Boolean = true,
    val type: String = "minecraft:happy_villager",
    val radius: Double = 1.5,
    val points: Int = 12,
  )

  @Serializable
  data class Firework(
    val enabled: Boolean = false,
    val power: Int = 1,
    /** BALL, BALL_LARGE, STAR, BURST, or CREEPER. */
    val type: String = "BALL",
    val flicker: Boolean = true,
    val trail: Boolean = true,
    /** `#RRGGBB`, `r,g,b`, or a name: white, red, gold, lime, sky, and others in [Colors]. */
    val colors: List<String> = listOf("#FFD54F", "#FFFFFF"),
    @SerialName("fade_colors") val fadeColors: List<String> = listOf("#FFA000"),
    /** Whether players near the rocket feel its blast. Off by default: a welcome shouldn't hurt. */
    val damage: Boolean = false,
  )

  @Serializable
  data class Effect(
    val name: String,
    /** Seconds. */
    val duration: Int,
    /** 0 is level I. */
    val amplifier: Int = 0,
  )

  companion object {
    private val yaml = Yaml(configuration = YamlConfiguration(strictMode = false))

    fun parse(text: String): Settings = yaml.decodeFromString(serializer(), text)

    /** A language file: message key → MiniMessage text. */
    fun parseMessages(text: String): Map<String, String> =
      if (text.isBlank()) emptyMap() else yaml.decodeFromString(MapSerializer(String.serializer(), String.serializer()), text)

    val DEFAULT_COUNTRIES = mapOf(
      "ru" to listOf("RU", "BY", "KZ", "KG", "TJ", "UZ", "AM", "AZ", "MD", "GE", "TM"),
      "es" to listOf(
        "ES", "MX", "AR", "CO", "CL", "PE", "VE", "EC", "GT", "CU", "BO", "DO", "HN",
        "PY", "SV", "NI", "CR", "PA", "UY", "PR", "GQ",
      ),
    )
  }
}
