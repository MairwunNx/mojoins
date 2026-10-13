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
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Replaces the vanilla join and quit lines with the configured ones, each shown to every player in
 * their own language. The country lookup runs during login, off the server thread.
 */
class JoinListener(private val plugin: MoJoinsPlugin) : Listener {
  private val countries = ConcurrentHashMap<UUID, String>()

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  fun onPreLogin(event: AsyncPlayerPreLoginEvent) {
    if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) return
    plugin.geoIp.country(event.address)?.let { countries[event.uniqueId] = it }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  fun onJoin(event: PlayerJoinEvent) {
    if (!plugin.settings.enabled) return
    val player = event.player
    val firstJoin = !player.hasPlayedBefore()
    val broadcast = plugin.settings.broadcast
    event.joinMessage(null)

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

  @EventHandler(priority = EventPriority.HIGHEST)
  fun onQuit(event: PlayerQuitEvent) {
    val player = event.player
    if (plugin.settings.enabled) {
      event.quitMessage(null)
      if (plugin.settings.broadcast.quit) announce(Message.QUIT, player, except = player)
      if (plugin.settings.broadcast.vanillaLog) Bukkit.getServer().logger.info("${player.name} left the game")
    }
    countries.remove(player.uniqueId)
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

  fun languageOf(player: Player): String = plugin.resolver.resolve(countries[player.uniqueId], player.locale())

  /** Sends a message to everyone online, each in their language. The console gets it too, unless it gets the vanilla line instead. */
  private fun announce(message: Message, subject: Player, except: Player? = null) {
    Bukkit.getOnlinePlayers().forEach { if (it != except) send(it, message, subject) }
    if (plugin.settings.broadcast.vanillaLog) return
    plugin.languages.render(plugin.resolver.default, message, *placeholders(subject))?.let { Bukkit.getConsoleSender().sendMessage(it) }
  }

  private fun send(to: Player, message: Message, subject: Player) {
    plugin.languages.render(languageOf(to), message, *placeholders(subject))?.let(to::sendMessage)
  }

  private fun placeholders(subject: Player): Array<TagResolver> = arrayOf(
    Placeholder.unparsed("player", subject.name),
    Placeholder.component("display_name", subject.displayName()),
    Placeholder.unparsed("online", Bukkit.getOnlinePlayers().size.toString()),
  )
}
