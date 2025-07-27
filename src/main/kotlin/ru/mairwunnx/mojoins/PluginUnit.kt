package ru.mairwunnx.mojoins

import kotlinx.coroutines.runBlocking
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.mairwunnx.mojoins.managers.CommandManager
import ru.mairwunnx.mojoins.managers.ConfigurationManager
import ru.mairwunnx.mojoins.managers.EffectsManager
import ru.mairwunnx.mojoins.managers.PlayerEventManager
import ru.mairwunnx.mojoins.models.GeneralConfigurationModel
import ru.mairwunnx.mojoins.platform.PaperLogger
import ru.mairwunnx.mojoins.platform.PaperScheduler

class PluginUnit : JavaPlugin() {
  lateinit var logger: PaperLogger private set
  lateinit var scheduler: PaperScheduler private set

  lateinit var configuration: ConfigurationManager private set
  val isConfigurationDefined get() = ::configuration.isInitialized

  lateinit var commands: CommandManager private set
  lateinit var effects: EffectsManager private set
  lateinit var players: PlayerEventManager private set

  override fun onEnable() {
    logger = PaperLogger(this)
    scheduler = PaperScheduler(this)

    logger.info { "🔄 Loading Mo'Joins plugin" }

    configuration = ConfigurationManager(this)
    runBlocking { configuration.initialize() }

    if (!configuration[GeneralConfigurationModel::class.java].enabled) {
      logger.info { "⛔️ Mo'Joins is disabled in the config. Plugin will not be enabled." }
      onDisable()
      return
    }

    commands = CommandManager(this)
    effects = EffectsManager(this)
    players = PlayerEventManager(this)

    server.pluginManager.registerEvents(players, this)

    logger.info { "✅ Plugin Mo'Joins loaded" }
  }

  override fun onDisable() {
    HandlerList.unregisterAll(this)

    if (::scheduler.isInitialized) scheduler.close()
    if (::configuration.isInitialized) configuration.close()
    if (::commands.isInitialized) commands.close()
    if (::effects.isInitialized) effects.close()
    if (::players.isInitialized) players.close()

    logger.info { "✅ Plugin Mo'Joins unloaded" }
  }
}
