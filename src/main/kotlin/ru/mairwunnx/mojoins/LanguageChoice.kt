package ru.mairwunnx.mojoins

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

/**
 * The language a player chose with `/mojoins language`, kept in the player's own data so it survives
 * restarts. It beats the country and the game language; a choice whose file is gone counts as none.
 */
class LanguageChoice(private val key: NamespacedKey) {
  fun get(player: Player): String? = player.persistentDataContainer.get(key, PersistentDataType.STRING)

  /** Sets the player's language; null hands the choice back to the plugin. */
  fun set(player: Player, code: String?) {
    if (code != null) player.persistentDataContainer.set(key, PersistentDataType.STRING, code) else player.persistentDataContainer.remove(key)
  }
}
