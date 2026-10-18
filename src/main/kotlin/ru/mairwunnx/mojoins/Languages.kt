package ru.mairwunnx.mojoins

import net.kyori.adventure.text.Component
import net.kyori.adventure.pointer.Pointered
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
  LANGUAGE_LIST("language_list"),
  LANGUAGE_AUTO_NAME("language_auto_name"),
  LANGUAGE_SET("language_set"),
  LANGUAGE_AUTO("language_auto"),
  LANGUAGE_UNKNOWN("language_unknown"),
}

/**
 * The messages of every language, by code (`en`, `ru`, `pt_br`). Parsing happens once per load;
 * rendering only fills the placeholders.
 */
class Languages(private val texts: Map<String, Map<String, String>>, val default: String) {
  private val mini = MiniMessage.miniMessage()

  val codes: Set<String> get() = texts.keys

  fun has(code: String) = code in texts

  /** The languages to choose from, the bundled ones first, in their order. */
  val choices: List<String> get() = texts.keys.sortedWith(compareBy({ it !in BUNDLED }, { BUNDLED.indexOf(it) }, { it }))

  /** A language as its speakers call it; a language a server added shows its code. */
  fun name(code: String): String = NAMES[code] ?: code

  /** The language a player typed: `pt_BR` finds `pt_br`, then `pt`. Null when there's no such file. */
  fun match(typed: String): String? {
    val wanted = LocaleResolver.normalize(typed)
    return wanted.takeIf { it in texts } ?: wanted.substringBefore('_').takeIf { it in texts }
  }

  /** The raw text, or null when no language sets it, which means the message is off. */
  fun text(code: String, message: Message): String? =
    texts[code]?.get(message.key) ?: texts[default]?.get(message.key) ?: texts[FALLBACK]?.get(message.key)

  /** An empty text turns a message off, so a server can drop, say, quit messages for one language. */
  /** The message in a language; [target] is the player it's about, for tags that read a player. */
  fun render(code: String, message: Message, vararg resolvers: TagResolver, target: Pointered? = null): Component? =
    text(code, message)?.takeIf { it.isNotBlank() }?.let { if (target != null) mini.deserialize(it, target, *resolvers) else mini.deserialize(it, *resolvers) }

  companion object {
    const val FALLBACK = "en"
    val BUNDLED = listOf("en", "ru", "es", "pt", "de")
    val NAMES = mapOf("en" to "English", "ru" to "Русский", "es" to "Español", "pt" to "Português", "de" to "Deutsch")
  }
}

/** Picks a player's language: the one they chose, if any, then by their country and their game's language setting. */
class LocaleResolver(private val settings: Settings.Locales, private val available: Set<String>) {
  private val byCountry: Map<String, String> = buildMap {
    settings.countries.forEach { (code, countries) -> countries.forEach { putIfAbsent(it.uppercase(), normalize(code)) } }
  }

  val default: String = normalize(settings.default).takeIf { it in available } ?: Languages.FALLBACK

  fun resolve(country: String?, client: Locale?, chosen: String? = null): String {
    if (chosen != null && chosen in available) return chosen
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
