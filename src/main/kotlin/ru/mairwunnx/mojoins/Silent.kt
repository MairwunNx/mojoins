package ru.mairwunnx.mojoins

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

/**
 * Silent mode, switched by `/mojoins silent`: the player's joins and quits go unannounced. Stored in
 * the player's own data, so it survives restarts, and honored only while they still hold `mojoins.silent`.
 * It hides chat lines and effects; the player still shows up in the player list.
 */
class Silent(private val key: NamespacedKey) {
  fun isOn(player: Player): Boolean =
    player.hasPermission(PERMISSION) && player.persistentDataContainer.getOrDefault(key, PersistentDataType.BOOLEAN, false)

  /** Flips the mode and returns the new state. */
  fun toggle(player: Player): Boolean {
    val on = !player.persistentDataContainer.getOrDefault(key, PersistentDataType.BOOLEAN, false)
    if (on) player.persistentDataContainer.set(key, PersistentDataType.BOOLEAN, true) else player.persistentDataContainer.remove(key)
    return on
  }

  companion object {
    const val PERMISSION = "mojoins.silent"
  }
}
