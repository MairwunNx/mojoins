# Changelog

The “Release” workflow takes the notes for a version from its section here, unless you type them into its `changelog` field. The same notes go to GitHub and Modrinth.

## 2.3.0

- 🗣️ **Players choose their own language.** `/mojoins language` lists the languages by their own names, each a click away; `/mojoins language ru` picks one, and `/mojoins language auto` hands the choice back to the plugin. The choice beats the IP and game-language rules for every line the player reads, and it stays across restarts. Every player can use it; no permission needed.
- New messages in every language: `language_list`, `language_auto_name`, `language_set`, `language_auto`, `language_unknown`. `usage` now names `/mojoins language`. Files from 2.2 keep working; the new keys come from the bundled texts until you add them.

## 2.2.0

- 🧩 **MiniPlaceholders.** Join and quit lines take its tags, like `<luckperms_prefix>`, for the player who joined or left. Mo’Joins adds `<mojoins_language>`, `<mojoins_country>`, and `<mojoins_silent>` for TAB and other plugins. MiniPlaceholders is optional.
- 👻 **Vanish.** A player hidden by SuperVanish, PremiumVanish, Essentials, or any plugin that marks them `vanished` joins and leaves without a line, sound, or particle, as with silent joins but on its own. `<online>` doesn’t count vanished players.
- 📊 **Plan.** With Plan installed, a player’s page shows the language their messages come in, the country that picked it, and whether they join silently, and the players table gets a language column. The server page shows a pie of languages and the silent players online. Plan is optional, and the IP address never reaches it.

## 2.1.0

- 🤫 **Silent joins.** `/mojoins silent` hides your joins and quits: no chat lines, sounds, or particles, only a note to you. It needs `mojoins.silent`, which operators have, and stays on across restarts.
- 🧩 **PlaceholderAPI.** Write `<papi:name>` in any message to show `%name%` for the player who joined or left. PlaceholderAPI is optional; without it the tags show nothing. Your texts don’t change: Mo’Joins never rewrites `%…%` on its own.
- New messages in every language: `silent_on`, `silent_off`, `silent_join`, `no_permission`, `players_only`. Files from 2.0 keep working; the new keys come from the bundled texts until you add them.

## 2.0.1

- 🌍 **Portuguese and German.** Brazil, Portugal, and the Portuguese-speaking countries of Africa get Portuguese; Germany, Austria, Switzerland, Liechtenstein, and Luxembourg get German.
- Licensed under MIT.

## 2.0.0

A rewrite for Paper 26.3 and Java 25.

- 🌍 **Each player reads their own language.** The plugin finds the player’s country by IP, with DB-IP’s free database, and picks a language for it: English, Russian, or Spanish. When the country isn’t listed, it takes the language set in the game. Everyone sees the same join line, each in their own language.
- 📨 **Messages live in `lang/<language>.yml`.** New: a “say hi” line to everyone else when a newcomer joins, and the `<display_name>` and `<online>` placeholders.
- 📜 **The server log keeps the vanilla “joined the game” lines**, so tools that read the log still work.
- 🎆 **The firework no longer hurts** players near it, unless `firework.damage` is on. It’s off by default now, as are potion effects.
- ⚙️ **`/mojoins reload` reloads the files** instead of restarting the plugin, which used to lose the command. `/mojoins locale [player]` shows the language a player got and why.
- **A broken value no longer resets the config.** Every key has a default, an unknown sound, particle, or effect logs a warning, and a config from 1.x loads too. Its `messages` section is ignored: move your texts to `lang/`.
- English manifest and docs; the command needs `mojoins.admin`.

## 1.1.1

- Registering the command no longer fails the plugin when it can’t.

## 1.1.0

- New join, first-join, and quit messages with gradients.

## 1.0.0

- First release: join, first-join, and quit messages, sounds, a particle ring, a firework, and potion effects on join.
