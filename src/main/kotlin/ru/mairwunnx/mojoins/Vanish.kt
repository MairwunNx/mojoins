package ru.mairwunnx.mojoins

import org.bukkit.entity.Player

/**
 * Vanish plugins (SuperVanish, PremiumVanish, Essentials, and others) share one convention: a `vanished`
 * metadata value that reads true. Mo’Joins announces nobody who carries it.
 */
object Vanish {
  fun isOn(player: Player): Boolean = player.getMetadata("vanished").any { it.asBoolean() }
}
