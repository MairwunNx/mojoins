package ru.mairwunnx.mojoins

import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.io.File

class MoJoinsPlugin : JavaPlugin() {
  @Volatile var settings = Settings(); private set
  @Volatile lateinit var languages: Languages; private set
  @Volatile lateinit var resolver: LocaleResolver; private set
  @Volatile lateinit var effects: JoinEffects; private set

  val log = Log(logger) { settings.debug }
  val geoIp by lazy { GeoIp(dataFolder.resolve("geoip"), log) }
  lateinit var listener: JoinListener; private set
  val silent by lazy { Silent(NamespacedKey(this, "silent")) }
  val choice by lazy { LanguageChoice(NamespacedKey(this, "language")) }

  private val fireworkKey by lazy { NamespacedKey(this, "join_firework") }
  private var geoIpTask: BukkitTask? = null
  private var miniPlaceholders: AutoCloseable? = null

  override fun onEnable() {
    if (!reload()) log.warn("Started with the default settings; fix config.yml and run /mojoins reload")
    listener = JoinListener(this)
    server.pluginManager.registerEvents(listener, this)
    registerCommand("mojoins", "Choose your language for Mo’Joins, reload it, check a player’s language, or join silently", MoJoinsCommand(this))
    log.info("Mo’Joins ${pluginMeta.version} is on: ${languages.codes.sorted().joinToString()}, default ${resolver.default}")
    if (Placeholders.available) log.info("PlaceholderAPI found: <papi:…> tags work in messages")
    // Players online during a reload have no join to fill in what Plan reads.
    server.onlinePlayers.forEach(listener::remember)
    if (PlanData.available) PlanData.register(this)
    if (MiniPlaceholderTags.available) {
      miniPlaceholders = MiniPlaceholderTags.register(this)
      log.info("MiniPlaceholders found: its tags work in messages, <mojoins_…> works elsewhere")
    }
  }

  override fun onDisable() {
    miniPlaceholders?.close()
    geoIpTask?.cancel()
    geoIp.close()
  }

  /** Reads config.yml and the language files again. On a broken file, keeps what it can and returns false. */
  fun reload(): Boolean {
    var ok = true
    settings = runCatching { Settings.parse(file("config.yml").readText()) }
      .onFailure { ok = false; log.error("config.yml has an error, using the defaults: ${it.message}") }
      .getOrDefault(Settings())

    val texts = loadLanguages { ok = false }
    languages = Languages(texts, LocaleResolver.normalize(settings.locales.default))
    resolver = LocaleResolver(settings.locales, texts.keys)
    effects = JoinEffects(settings, log, fireworkKey)
    if (LocaleResolver.normalize(settings.locales.default) !in texts) log.warn("locales.default: no lang/${settings.locales.default}.yml, using ${resolver.default}")

    setUpGeoIp()
    return ok
  }

  /** The bundled languages, overlaid key by key with the files in `lang/`, plus any extra languages found there. */
  private fun loadLanguages(onError: () -> Unit): Map<String, Map<String, String>> {
    val folder = dataFolder.resolve("lang")
    Languages.BUNDLED.forEach { file("lang/$it.yml") }

    val bundled = Languages.BUNDLED.associateWith { code ->
      getResource("lang/$code.yml")!!.use { Settings.parseMessages(it.readBytes().toString(Charsets.UTF_8)) }
    }
    val custom = folder.listFiles { f -> f.isFile && f.extension == "yml" }.orEmpty().associate { f ->
      LocaleResolver.normalize(f.nameWithoutExtension) to runCatching { Settings.parseMessages(f.readText()) }
        .onFailure { onError(); log.error("lang/${f.name} has an error, skipping it: ${it.message}") }
        .getOrDefault(emptyMap())
    }
    return (bundled.keys + custom.keys).associateWith { bundled[it].orEmpty() + custom[it].orEmpty() }
  }

  private fun setUpGeoIp() {
    geoIpTask?.cancel()
    val config = settings.geoip
    if (!config.enabled || Settings.Source.IP !in settings.locales.sources) {
      geoIp.close()
      return
    }
    geoIp.open()
    // Checks twice a day; downloads only when the file on disk has aged past refresh_days.
    geoIpTask = server.scheduler.runTaskTimerAsynchronously(this, Runnable { if (geoIp.isStale(config)) geoIp.refresh(config) }, 0L, 20L * 60 * 60 * 12)
  }

  /** A file in the data folder, copied from the jar first when it isn't there yet. */
  private fun file(path: String): File {
    val file = dataFolder.resolve(path)
    if (!file.exists()) saveResource(path, false)
    return file
  }
}
