package ru.mairwunnx.mojoins

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * `/mojoins language [code|auto]` lets any player choose the language they read; `/mojoins reload`
 * reloads the config and languages; `/mojoins locale [player]` shows the language a player gets and
 * why; `/mojoins silent` switches silent joins. Reload and locale need `mojoins.admin`, silent
 * `mojoins.silent`.
 */
class MoJoinsCommand(private val plugin: MoJoinsPlugin) : BasicCommand {
  override fun canUse(sender: CommandSender) = sender is Player || sender.hasPermission(ADMIN) || sender.hasPermission(Silent.PERMISSION)

  override fun execute(source: CommandSourceStack, args: Array<String>) {
    val sender = source.sender
    val sub = args.firstOrNull()?.lowercase()
    val needed = when (sub) {
      "silent" -> Silent.PERMISSION
      "reload", "locale" -> ADMIN
      else -> null
    }
    if (needed != null && !sender.hasPermission(needed)) return reply(sender, Message.NO_PERMISSION)

    when (sub) {
      "language" -> {
        val player = sender as? Player ?: return reply(sender, Message.PLAYERS_ONLY)
        language(player, args.getOrNull(1))
      }
      "silent" -> {
        val player = sender as? Player ?: return reply(sender, Message.PLAYERS_ONLY)
        reply(sender, if (plugin.silent.toggle(player)) Message.SILENT_ON else Message.SILENT_OFF)
        plugin.listener.remember(player)
        if (PlanData.available) PlanData.update(player)
      }
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

  /** With a code sets the player's language, with `auto` hands it back to the plugin, with nothing lists the languages. */
  private fun language(player: Player, typed: String?) {
    val languages = plugin.languages
    if (typed == null) return reply(player, Message.LANGUAGE_LIST, Placeholder.unparsed("language", languages.name(plugin.listener.languageOf(player))), Placeholder.component("languages", options(player)))

    val code = if (typed.equals(AUTO, ignoreCase = true)) null else languages.match(typed)
      ?: return reply(player, Message.LANGUAGE_UNKNOWN, Placeholder.unparsed("code", typed), Placeholder.component("languages", options(player)))
    plugin.choice.set(player, code)
    plugin.listener.remember(player)
    if (PlanData.available) PlanData.update(player)
    // The reply already comes in the language the player gets now.
    reply(player, if (code == null) Message.LANGUAGE_AUTO else Message.LANGUAGE_SET, Placeholder.unparsed("language", languages.name(plugin.listener.languageOf(player))))
  }

  /** Every language and “automatic”, each a click away; the player's current pick stands out. */
  private fun options(player: Player): Component {
    val chosen = plugin.choice.get(player)?.takeIf(plugin.languages::has)
    val reader = plugin.listener.languageOf(player)
    val auto = plugin.languages.text(reader, Message.LANGUAGE_AUTO_NAME)?.takeIf { it.isNotBlank() } ?: AUTO
    val entries = plugin.languages.choices.map { it to plugin.languages.name(it) } + (AUTO to auto)
    return Component.join(JoinConfiguration.separator(Component.text(", ", NamedTextColor.GRAY)), entries.map { (code, name) ->
      val current = code == (chosen ?: AUTO)
      Component.text(name, if (current) NamedTextColor.GREEN else NamedTextColor.WHITE)
        .decoration(TextDecoration.BOLD, current)
        .hoverEvent(HoverEvent.showText(Component.text("/mojoins language $code", NamedTextColor.GRAY)))
        .clickEvent(ClickEvent.runCommand("/mojoins language $code"))
    })
  }

  override fun suggest(source: CommandSourceStack, args: Array<String>): Collection<String> = when (args.size) {
    0, 1 -> listOfNotNull(
      "language".takeIf { source.sender is Player },
      "reload".takeIf { source.sender.hasPermission(ADMIN) },
      "locale".takeIf { source.sender.hasPermission(ADMIN) },
      "silent".takeIf { source.sender.hasPermission(Silent.PERMISSION) },
    ).filter { it.startsWith(args.firstOrNull().orEmpty(), ignoreCase = true) }
    2 if args[0].equals("language", ignoreCase = true) && source.sender is Player ->
      (plugin.languages.choices + AUTO).filter { it.startsWith(args[1], ignoreCase = true) }
    2 if args[0].equals("locale", ignoreCase = true) && source.sender.hasPermission(ADMIN) ->
      Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[1], ignoreCase = true) }
    else -> emptyList()
  }

  private fun reply(sender: CommandSender, message: Message, vararg placeholders: TagResolver) {
    val language = (sender as? Player)?.let(plugin.listener::languageOf) ?: plugin.resolver.default
    plugin.languages.render(language, message, *placeholders)?.let(sender::sendMessage)
  }

  private companion object {
    const val ADMIN = "mojoins.admin"
    const val AUTO = "auto"
  }
}
