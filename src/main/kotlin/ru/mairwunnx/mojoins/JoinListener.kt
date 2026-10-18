package ru.mairwunnx.mojoins

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLocaleChangeEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Replaces the vanilla join and quit lines with the configured ones, each shown to every player in
 * their own language. The country lookup runs during login, off the server thread. Players with
 * silent mode on get none of it: no lines, sounds, or particles, only a note to themselves.
 */
class JoinListener(private val plugin: MoJoinsPlugin) : Listener {
  private val countries = ConcurrentHashMap<UUID, String>()
  /** What Plan shows about each player online. Written on the server thread, read from Plan's. */
  private val seen = ConcurrentHashMap<UUID, Seen>()
  /** Players whose quit began while vanished; the server thread only. */
  private val leavingVanished = HashSet<UUID>()

  data class Seen(val language: String, val country: String?, val silent: Boolean)

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  fun onPreLogin(event: AsyncPlayerPreLoginEvent) {
    if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) return
    plugin.geoIp.country(event.address)?.let { countries[event.uniqueId] = it }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  fun onJoin(event: PlayerJoinEvent) {
    remember(event.player)
    if (plugin.settings.enabled) event.joinMessage(null)
  }

  /**
   * The lines, sounds, and particles go out last, at MONITOR: vanish plugins mark a player during the
   * join (SuperVanish at HIGH, Essentials at HIGHEST), and only now has each of them had its turn. This
   * handler doesn't touch the event; it only sends.
   */
  @EventHandler(priority = EventPriority.MONITOR)
  fun onJoined(event: PlayerJoinEvent) {
    if (!plugin.settings.enabled) return
    val player = event.player
    val firstJoin = !player.hasPlayedBefore()
    val broadcast = plugin.settings.broadcast

    if (plugin.silent.isOn(player)) {
      send(player, Message.SILENT_JOIN, player)
      if (broadcast.vanillaLog) Bukkit.getServer().logger.info("${player.name} joined the game")
      plugin.log.debug { "${player.name} joined silently" }
      return
    }
    if (Vanish.isOn(player)) {
      if (broadcast.vanillaLog) Bukkit.getServer().logger.info("${player.name} joined the game")
      plugin.log.debug { "${player.name} joined vanished" }
      return
    }

    if (firstJoin && broadcast.firstJoin) {
      send(player, Message.FIRST_JOIN, player)
      announce(Message.FIRST_JOIN_BROADCAST, player, except = player)
    } else if (broadcast.join) {
      announce(Message.JOIN, player)
    }
    if (broadcast.vanillaLog) Bukkit.getServer().logger.info("${player.name} joined the game")
    plugin.log.debug { "${player.name} joined: first=$firstJoin, country=${countries[player.uniqueId]}, language=${languageOf(player)}" }

    plugin.effects.play(player, firstJoin)
  }

  /** Essentials drops its vanish mark during the quit, at HIGHEST; read the mark before anyone else runs. */
  @EventHandler(priority = EventPriority.LOWEST)
  fun onQuitting(event: PlayerQuitEvent) {
    if (Vanish.isOn(event.player)) leavingVanished += event.player.uniqueId
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  fun onQuit(event: PlayerQuitEvent) {
    val player = event.player
    val vanished = leavingVanished.remove(player.uniqueId) || Vanish.isOn(player)
    if (plugin.settings.enabled) {
      event.quitMessage(null)
      if (plugin.settings.broadcast.quit && !plugin.silent.isOn(player) && !vanished) announce(Message.QUIT, player, except = player)
      if (plugin.settings.broadcast.vanillaLog) Bukkit.getServer().logger.info("${player.name} left the game")
    }
    countries.remove(player.uniqueId)
    seen.remove(player.uniqueId)
  }

  /** The game sends its language a moment after the join; with no country to go by, that changes the pick. */
  @EventHandler(priority = EventPriority.MONITOR)
  fun onLocaleChange(event: PlayerLocaleChangeEvent) {
    remember(event.player, event.locale())
    if (PlanData.available) PlanData.update(event.player)
  }

  /** Join rockets are for show: their blast hurts nobody unless the config says so. */
  @EventHandler(ignoreCancelled = true)
  fun onFireworkDamage(event: EntityDamageByEntityEvent) {
    val rocket = event.damager as? Firework ?: return
    if (!plugin.effects.fireworkDamage && rocket.persistentDataContainer.has(plugin.effects.fireworkKey, PersistentDataType.BOOLEAN)) {
      event.isCancelled = true
    }
  }

  fun country(player: Player): String? = countries[player.uniqueId]

  /** The language a player reads: their own choice, else the one picked by country and game language. The server thread only. */
  fun languageOf(player: Player, locale: Locale = player.locale()): String =
    plugin.resolver.resolve(countries[player.uniqueId], locale, plugin.choice.get(player))

  fun seen(player: UUID): Seen? = seen[player]

  fun silentOnline(): Int = seen.values.count { it.silent }

  /** Keeps what Plan reads about an online player; call on the server thread whenever it may have changed. */
  fun remember(player: Player, locale: Locale = player.locale()) {
    seen[player.uniqueId] = Seen(languageOf(player, locale), countries[player.uniqueId], plugin.silent.isOn(player))
  }

  /** Sends a message to everyone online, each in their language. The console gets it too, unless it gets the vanilla line instead. */
  private fun announce(message: Message, subject: Player, except: Player? = null) {
    Bukkit.getOnlinePlayers().forEach { if (it != except) send(it, message, subject) }
    if (plugin.settings.broadcast.vanillaLog) return
    plugin.languages.render(plugin.resolver.default, message, *placeholders(subject), target = subject)?.let { Bukkit.getConsoleSender().sendMessage(it) }
  }

  private fun send(to: Player, message: Message, subject: Player) {
    plugin.languages.render(languageOf(to), message, *placeholders(subject), target = subject)?.let(to::sendMessage)
  }

  private fun placeholders(subject: Player): Array<TagResolver> = arrayOf(
    Placeholder.unparsed("player", subject.name),
    Placeholder.component("display_name", subject.displayName()),
    // Vanished players stay out of the count, or it would give them away.
    Placeholder.unparsed("online", Bukkit.getOnlinePlayers().count { !Vanish.isOn(it) }.toString()),
    Placeholders.resolver(subject),
    MiniPlaceholderTags.resolver(),
  )
}
