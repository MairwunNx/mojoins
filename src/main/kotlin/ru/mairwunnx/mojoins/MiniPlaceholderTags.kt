package ru.mairwunnx.mojoins

import io.github.miniplaceholders.api.Expansion
import io.github.miniplaceholders.api.MiniPlaceholders
import io.github.miniplaceholders.api.resolver.AudienceTagResolver
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * MiniPlaceholders both ways, when it's there. Join and quit lines resolve other plugins' tags, like
 * `<luckperms_prefix>`, for the player who joined or left: the message renders with that player as its
 * target. And `<mojoins_language>`, `<mojoins_country>`, `<mojoins_silent>` tell TAB, scoreboards, and
 * other plugins what Mo’Joins knows about a player. Those may be asked from any thread, so they read only
 * what [JoinListener] keeps for players online.
 */
object MiniPlaceholderTags {
  val available: Boolean get() = Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")

  /** Other plugins' global and per-player tags, for the message's target; nothing without MiniPlaceholders. */
  fun resolver(): TagResolver =
    // Only touch MiniPlaceholders' classes when it's there; they aren't on the classpath otherwise.
    if (available) MiniPlaceholders.audienceGlobalPlaceholders() else TagResolver.empty()

  /** Registers `<mojoins_…>`. Call only when [available]. Returns a handle that unregisters it. */
  fun register(plugin: MoJoinsPlugin): AutoCloseable {
    fun known(value: (JoinListener.Seen) -> String?) = AudienceTagResolver<Player> { player, _, _ ->
      Tag.selfClosingInserting(Component.text(plugin.listener.seen(player.uniqueId)?.let(value).orEmpty()))
    }
    val expansion = Expansion.builder("mojoins")
      .author("MairwunNx")
      .version(plugin.pluginMeta.version)
      .audiencePlaceholder(Player::class.java, "language", known { it.language })
      .audiencePlaceholder(Player::class.java, "country", known { it.country })
      .audiencePlaceholder(Player::class.java, "silent", known { it.silent.toString() })
      .build()
    expansion.register()
    return AutoCloseable { expansion.unregister() }
  }
}
