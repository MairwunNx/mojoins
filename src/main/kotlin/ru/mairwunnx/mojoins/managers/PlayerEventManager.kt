package ru.mairwunnx.mojoins.managers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit.getOnlinePlayers
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.HIGHEST
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.mairwunnx.mojoins.PluginUnit
import ru.mairwunnx.mojoins.models.GeneralConfigurationModel
import java.io.Closeable

class PlayerEventManager(private val plugin: PluginUnit) : Listener, Closeable {
  private val mm = MiniMessage.miniMessage()

  @EventHandler(priority = HIGHEST)
  fun onJoin(e: PlayerJoinEvent) {
    val p = e.player
    val cfg = plugin.configuration[GeneralConfigurationModel::class.java]
    val first = !p.hasPlayedBefore()

    e.joinMessage(null)

    if ((first && cfg.broadcast.firstJoinEnabled) || (!first && cfg.broadcast.joinEnabled)) {
      val msg = cfg.messages.join.render(p)
      val recipients = audienceAll()
      recipients.forEach { it.sendMessage(msg) }
      plugin.logger.debug { "Join broadcast sent (first=$first) to ${recipients.size} recipients" }
    } else {
      plugin.logger.debug { "Join broadcast skipped (first=$first)" }
    }

    if (first && cfg.broadcast.firstJoinEnabled) {
      p.sendMessage(cfg.messages.firstJoin.render(p))
      plugin.logger.debug { "First join personal message sent to ${p.name}" }
    }

    plugin.effects.apply(p, first)
  }

  @EventHandler(priority = HIGHEST)
  fun onQuit(e: PlayerQuitEvent) {
    val p = e.player
    val cfg = plugin.configuration[GeneralConfigurationModel::class.java]

    e.quitMessage(null)

    if (cfg.broadcast.quitEnabled) {
      val quitMsg = cfg.messages.quit.render(p)
      val recipients = audienceOthers(p)
      recipients.forEach { it.sendMessage(quitMsg) }
      plugin.logger.debug { "Quit broadcast sent to ${recipients.size} recipients (excluding ${p.name})" }
    } else {
      plugin.logger.debug { "Quit broadcast skipped (due disabled in configuration)" }
    }

    plugin.effects.cancel(p)
  }

  private fun Component.render(player: Player): Component {
    val source = mm.serialize(this)
    return mm.deserialize(source, Placeholder.unparsed("player", player.name))
  }

  private fun audienceAll() = getOnlinePlayers()
  private fun audienceOthers(except: Player) = getOnlinePlayers().filter { it.uniqueId != except.uniqueId }

  override fun close() = Unit
}