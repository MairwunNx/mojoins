package ru.mairwunnx.mojoins

import com.djrapitops.plan.capability.CapabilityService
import com.djrapitops.plan.extension.CallEvents
import com.djrapitops.plan.extension.Caller
import com.djrapitops.plan.extension.DataExtension
import com.djrapitops.plan.extension.ExtensionService
import com.djrapitops.plan.extension.NotReadyException
import com.djrapitops.plan.extension.annotation.BooleanProvider
import com.djrapitops.plan.extension.annotation.Conditional
import com.djrapitops.plan.extension.annotation.GroupProvider
import com.djrapitops.plan.extension.annotation.NumberProvider
import com.djrapitops.plan.extension.annotation.PluginInfo
import com.djrapitops.plan.extension.annotation.StringProvider
import com.djrapitops.plan.extension.icon.Color
import com.djrapitops.plan.extension.icon.Family
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

/**
 * Mo’Joins in Plan, when Plan is there. A player's page shows their language, the country that picked
 * it, and whether they join silently; the server page shows a pie of languages and the silent players
 * online. Plan asks from its own threads, so the extension reads only what [JoinListener] keeps for
 * players online. For everyone else Plan keeps the values it stored last. The IP address never leaves
 * the plugin.
 */
object PlanData {
  private val NEEDS = listOf("DATA_EXTENSION_VALUES", "DATA_EXTENSION_GROUPS", "DATA_EXTENSION_SHOW_IN_PLAYER_TABLE")

  val available: Boolean get() = Bukkit.getPluginManager().isPluginEnabled("Plan")

  @Volatile private var caller: Caller? = null

  /** Registers the extension, now and again whenever Plan reloads. Call only when [available]. */
  fun register(plugin: MoJoinsPlugin) {
    val capabilities = CapabilityService.getInstance()
    val missing = NEEDS.filterNot(capabilities::hasCapability)
    if (missing.isNotEmpty()) {
      plugin.log.warn("Plan is too old for Mo’Joins' section (it lacks ${missing.joinToString()}); update Plan to see it")
      return
    }
    val extension = Extension(plugin)
    // Plan finishes enabling after the plugins that load before it start, then calls the listener; a Plan
    // that's already up (a later /mojoins reload, a plugin manager) takes the extension right away.
    capabilities.registerEnableListener { enabled -> if (enabled) add(plugin, extension, quiet = false) }
    add(plugin, extension, quiet = true)
  }

  private fun add(plugin: MoJoinsPlugin, extension: Extension, quiet: Boolean) {
    runCatching { ExtensionService.getInstance().register(extension).orElse(null) }
      .onSuccess { caller = it; if (it != null) plugin.log.info("Plan found: languages and silent joins show in its Plugins sections") }
      .onFailure { if (!quiet) plugin.log.warn("Couldn't add Mo’Joins to Plan: ${it.message}") }
  }

  /** Tells Plan a player's values changed, like a new game language or silent mode switched. */
  fun update(player: Player) {
    caller?.updatePlayerData(player.uniqueId, player.name)
  }

  @PluginInfo(name = "Mo’Joins", iconName = "door-open", iconFamily = Family.SOLID, color = Color.TEAL)
  class Extension(private val plugin: MoJoinsPlugin) : DataExtension {
    override fun callExtensionMethodsOn(): Array<CallEvents> = arrayOf(CallEvents.PLAYER_JOIN, CallEvents.SERVER_EXTENSION_REGISTER, CallEvents.SERVER_PERIODICAL)

    private fun seen(player: UUID): JoinListener.Seen = plugin.listener.seen(player) ?: throw NotReadyException()

    @StringProvider(
      text = "Language", description = "The language Mo’Joins shows this player messages in", priority = 100,
      iconName = "language", iconColor = Color.TEAL, showInPlayerTable = true,
    )
    fun language(player: UUID): String = seen(player).language

    @BooleanProvider(text = "Has a country", conditionName = "hasCountry", hidden = true)
    fun hasCountry(player: UUID): Boolean = seen(player).country != null

    @Conditional("hasCountry")
    @StringProvider(
      text = "Country", description = "The country of their IP address, which picks the language first", priority = 90,
      iconName = "globe", iconColor = Color.BLUE,
    )
    fun country(player: UUID): String = seen(player).country.orEmpty()

    @BooleanProvider(
      text = "Silent joins", description = "Joins and quits go unannounced, after /mojoins silent", priority = 80,
      iconName = "eye-slash", iconColor = Color.GREY,
    )
    fun silent(player: UUID): Boolean = seen(player).silent

    @GroupProvider(text = "Language", groupColor = Color.TEAL, iconName = "language")
    fun languages(player: UUID): Array<String> = arrayOf(seen(player).language)

    @NumberProvider(
      text = "Silent players online", description = "Players online whose joins and quits go unannounced", priority = 100,
      iconName = "eye-slash", iconColor = Color.GREY,
    )
    fun silentOnline(): Long = plugin.listener.silentOnline().toLong()
  }
}
