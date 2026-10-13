package ru.mairwunnx.mojoins

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/** `/mojoins reload` reloads the config and languages; `/mojoins locale [player]` shows the language a player gets and why. */
class MoJoinsCommand(private val plugin: MoJoinsPlugin) : BasicCommand {
  override fun permission() = "mojoins.admin"

  override fun execute(source: CommandSourceStack, args: Array<String>) {
    val sender = source.sender
    when (args.firstOrNull()?.lowercase()) {
      "reload" -> {
        val ok = plugin.reload()
        reply(sender, if (ok) Message.RELOADED else Message.RELOAD_FAILED)
      }
      "locale" -> {
        val target = args.getOrNull(1)?.let(Bukkit::getPlayerExact) ?: sender as? Player
        if (target == null) return reply(sender, Message.PLAYER_NOT_FOUND, Placeholder.unparsed("player", args.getOrNull(1) ?: "?"))
        reply(
          sender, Message.LOCALE_INFO,
          Placeholder.unparsed("player", target.name),
          Placeholder.unparsed("language", plugin.listener.languageOf(target)),
          Placeholder.unparsed("country", plugin.listener.country(target) ?: "?"),
          Placeholder.unparsed("client", target.locale().toString()),
        )
      }
      else -> reply(sender, Message.USAGE)
    }
  }

  override fun suggest(source: CommandSourceStack, args: Array<String>): Collection<String> = when (args.size) {
    0, 1 -> listOf("reload", "locale").filter { it.startsWith(args.firstOrNull().orEmpty(), ignoreCase = true) }
    2 if args[0].equals("locale", ignoreCase = true) ->
      Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[1], ignoreCase = true) }
    else -> emptyList()
  }

  private fun reply(sender: CommandSender, message: Message, vararg placeholders: TagResolver) {
    val language = (sender as? Player)?.let(plugin.listener::languageOf) ?: plugin.resolver.default
    plugin.languages.render(language, message, *placeholders)?.let(sender::sendMessage)
  }
}
