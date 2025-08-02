package ru.mairwunnx.mojoins.managers

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import ru.mairwunnx.mojoins.PluginUnit
import ru.mairwunnx.mojoins.models.GeneralConfigurationModel
import java.io.Closeable

class CommandManager(private val plugin: PluginUnit) : Closeable {
  init {
    runCatching {
      plugin.registerCommand("mojoins", "Mo'Joins admin", MoJoinsCommand())
    }.onFailure {
      plugin.logger.warning({ "Failed to register Mo'Joins command, skipping\n${it.stackTraceToString()}" })
    }
  }

  private inner class MoJoinsCommand : BasicCommand {
    override fun permission(): String? = null

    override fun execute(css: CommandSourceStack, args: Array<String>) {
      val sender = css.sender
      val cfg = plugin.configuration[GeneralConfigurationModel::class.java]

      if (args.isEmpty()) {
        sender.sendMessage(cfg.messages.system.incorrect)
        return
      }

      when (args[0].lowercase()) {
        "reload" -> {
          if (!sender.hasPermission("mojoins.reload")) {
            sender.sendMessage(cfg.messages.system.permission)
            return
          }
          plugin.logger.info { "🔄 Reloading Mo'Joins plugin" }
          runCatching {
            plugin.onDisable()
            plugin.onEnable()
          }.onSuccess {
            plugin.logger.info { "✅ Mo'Joins reloaded" }
            sender.sendMessage(cfg.messages.system.reloaded)
          }.onFailure {
            plugin.logger.error({ "Mo'Joins reload failed" }, it)
            sender.sendMessage(cfg.messages.system.reloadFailed)
          }
        }
        else -> sender.sendMessage(cfg.messages.system.incorrect)
      }
    }

    override fun suggest(css: CommandSourceStack, args: Array<String>): Collection<String> {
      if (args.size == 1) {
        return listOf("reload").filter { it.startsWith(args[0], ignoreCase = true) }
      }
      return emptyList()
    }
  }

  override fun close() = Unit
}