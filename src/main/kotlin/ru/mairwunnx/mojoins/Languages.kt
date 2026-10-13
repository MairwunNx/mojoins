package ru.mairwunnx.mojoins

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.Locale

/** The keys a language file can set. A key missing from one language comes from the default language, then from English. */
enum class Message(val key: String) {
  JOIN("join"),
  FIRST_JOIN("first_join"),
  FIRST_JOIN_BROADCAST("first_join_broadcast"),
  QUIT("quit"),
  RELOADED("reloaded"),
  RELOAD_FAILED("reload_failed"),
  USAGE("usage"),
  LOCALE_INFO("locale_info"),
  PLAYER_NOT_FOUND("player_not_found"),
  NO_PERMISSION("no_permission"),
  SILENT_ON("silent_on"),
  SILENT_OFF("silent_off"),
  SILENT_JOIN("silent_join"),
  PLAYERS_ONLY("players_only"),
}

/**
 * The messages of every language, by code (`en`, `ru`, `pt_br`). Parsing happens once per load;
 * rendering only fills the placeholders.
 */
class Languages(private val texts: Map<String, Map<String, String>>, val default: String) {
  private val mini = MiniMessage.miniMessage()

  val codes: Set<String> get() = texts.keys

  fun has(code: String) = code in texts

  /** The raw text, or null when no language sets it, which means the message is off. */
  fun text(code: String, message: Message): String? =
    texts[code]?.get(message.key) ?: texts[default]?.get(message.key) ?: texts[FALLBACK]?.get(message.key)

  /** An empty text turns a message off, so a server can drop, say, quit messages for one language. */
  fun render(code: String, message: Message, vararg resolvers: TagResolver): Component? =
    text(code, message)?.takeIf { it.isNotBlank() }?.let { mini.deserialize(it, *resolvers) }

  companion object {
    const val FALLBACK = "en"
    val BUNDLED = listOf("en", "ru", "es", "pt", "de")
  }
}

/** Picks a player's language from their country and their game's language setting. */
class LocaleResolver(private val settings: Settings.Locales, private val available: Set<String>) {
  private val byCountry: Map<String, String> = buildMap {
    settings.countries.forEach { (code, countries) -> countries.forEach { putIfAbsent(it.uppercase(), normalize(code)) } }
  }

  val default: String = normalize(settings.default).takeIf { it in available } ?: Languages.FALLBACK

  fun resolve(country: String?, client: Locale?): String {
    for (source in settings.sources) {
      val code = when (source) {
        Settings.Source.IP -> country?.let { byCountry[it.uppercase()] }
        Settings.Source.CLIENT -> client?.let(::fromClient)
      }
      if (code != null && code in available) return code
    }
    return default
  }

  /** `pt_BR` matches a `pt_br` file first, then `pt`. */
  private fun fromClient(locale: Locale): String? {
    val full = normalize(locale.toString())
    val language = normalize(locale.language)
    return when {
      full in available -> full
      language in available -> language
      else -> null
    }
  }

  companion object {
    fun normalize(code: String) = code.trim().lowercase().replace('-', '_')
  }
}
