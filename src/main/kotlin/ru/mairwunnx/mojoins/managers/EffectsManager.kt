package ru.mairwunnx.mojoins.managers

import org.bukkit.event.Listener
import ru.mairwunnx.mojoins.PluginUnit
import java.io.Closeable

class EffectsManager(private val plugin: PluginUnit) : Listener, Closeable {
  override fun close() {
  }
}