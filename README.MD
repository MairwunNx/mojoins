# Mo’Joins

🎉 **Mo’Joins** greets players on Paper servers in their own language. It replaces the vanilla join and quit lines with your own MiniMessage text, welcomes newcomers with a personal message, and plays a sound and a ring of particles when someone joins. A firework and potion effects are there if you want them.

[Русская версия](https://github.com/MairwunNx/mojoins/blob/master/README.RU.md)

## What you get

- 🌍 **Every player reads their own language.** The plugin finds the player’s country by IP and picks a language for it: English, Russian, Spanish, Portuguese, and German come bundled. Everyone in the chat sees the same join line, each in their own language.
- 📨 **Messages for every moment:** a join line, a personal welcome on the first join, a “say hi” line to everyone else, and a quit line. Placeholders: `<player>`, `<display_name>`, `<online>`, and PlaceholderAPI’s through `<papi:…>`.
- 🔊 **Sounds** for a join and for a first join.
- ✨ **A ring of particles** around the player who joined, and an optional **firework** whose blast hurts nobody.
- 🛡️ **Potion effects on join**, off by default: a few seconds of resistance, say.
- 📜 **The server log keeps the vanilla “joined the game” lines**, so tools that read the log still work.
- 🤫 **Silent joins for admins:** `/mojoins silent` hides your joins and quits from everyone else.
- 👻 **Vanish-aware:** a player hidden by SuperVanish, PremiumVanish, Essentials, or any plugin that marks them `vanished` joins and leaves without a line, sound, or particle, and `<online>` doesn’t count them.
- 🧩 **PlaceholderAPI**, if you have it: `<papi:player_health>` in a message shows `%player_health%`.
- 🧩 **MiniPlaceholders**, if you have it: its tags work in messages, and `<mojoins_language>`, `<mojoins_country>`, `<mojoins_silent>` work in TAB and elsewhere.
- 📊 **Plan**, if you have it: each player’s language and silent mode on their page, and a pie of languages on the server’s.
- ⚙️ **`/mojoins reload`** applies changes without a restart.

## Try it live

Mo’Joins runs on Moonlands, my hard survival server with friends. Come in and see it greet you.

[![Play on Moonlands: pure vanilla, hard mobs, AI, claims. minecraft.mairwunnx.com](https://raw.githubusercontent.com/MairwunNx/mojoins/master/media/og-adv-image.png)](https://minecraft.mairwunnx.com)

Join at `minecraft.mairwunnx.com`. The site has the live map, player stats, and the hall of fame.

## Requirements

- Paper 26.3 or a fork of it, like Purpur
- Java 25

## Install

1. Download the jar from [Releases](https://github.com/MairwunNx/mojoins/releases).
2. Put it in your server’s `plugins/` folder and restart the server.
3. Edit `plugins/MoJoins/config.yml` and the files in `plugins/MoJoins/lang/`.
4. Run `/mojoins reload`.

## How a player gets their language

The plugin tries the sources in `locales.sources`, in order:

1. **`ip`:** the country of the player’s IP address. `locales.countries` maps languages to country codes: Russia, Belarus, and Kazakhstan get Russian; Spain and Latin America get Spanish; Brazil and Portugal get Portuguese; Germany, Austria, and Switzerland get German.
2. **`client`:** the language set in the player’s game, when a file in `lang/` matches it.

When nothing matches, the player gets `locales.default`. Check what a player got with `/mojoins locale <player>`.

The country lookup needs no account and sends no address anywhere. On the first start, the plugin downloads DB-IP’s free country database (4 MB, 8 MB unpacked) into `plugins/MoJoins/geoip/` and refreshes it every 30 days. Players who connect through a proxy or a relay show up with its address, so they get the relay’s country.

### Add a language

1. Copy `lang/en.yml` to, say, `lang/it.yml` and translate it.
2. Add a line to `locales.countries`: `it: [ IT, SM, VA ]`.
3. Run `/mojoins reload`.

A key missing from a language file comes from the default language. An empty string turns a message off for that language.

## Commands and permissions

| Command | What it does |
|---|---|
| `/mojoins reload` | Reloads `config.yml` and the language files |
| `/mojoins locale [player]` | Shows a player’s language, country, and game language |
| `/mojoins silent` | Turns silent mode on or off for you |

The first two need `mojoins.admin`, the last `mojoins.silent`; operators have both by default. Silent mode stays on across restarts and works only while you hold `mojoins.silent`. It hides the chat lines, sounds, and particles; you still show up in the player list.

## PlaceholderAPI

With [PlaceholderAPI](https://modrinth.com/plugin/placeholderapi) installed, write `<papi:name>` in any message to show `%name%` for the player who joined or left: `<papi:luckperms_prefix><player>`. Mo’Joins doesn’t look for `%…%` in your text, so a percent sign stays a percent sign. Without PlaceholderAPI the tags show nothing.

## MiniPlaceholders

With [MiniPlaceholders](https://modrinth.com/plugin/miniplaceholders) installed, join and quit lines take its tags as they are, for the player who joined or left: `<luckperms_prefix><player>`. Mo’Joins also adds three tags for other plugins, each about the player they’re shown for:

| Tag | What it shows |
|---|---|
| `<mojoins_language>` | The language their messages come in, like `ru` |
| `<mojoins_country>` | The country of their IP address, or nothing |
| `<mojoins_silent>` | `true` while their joins go unannounced |

## Plan

With [Plan](https://modrinth.com/plugin/plan) installed, Mo’Joins adds its section to Plan’s pages. A player’s “Plugins” tab shows the language their messages come in, the country that picked it, and whether they join silently; the language is also a column in the players table. The server’s “Plugins Overview” shows a pie of languages and how many silent players are online. Plan stores the values when a player joins and keeps the last ones while they’re away. The IP address never reaches Plan.

## Build

You need JDK 25; Gradle downloads it when it’s missing.

```shell
git clone https://github.com/MairwunNx/mojoins
cd mojoins
./gradlew build
```

The jar lands in `build/libs/`. Releases come from the “Release” workflow on GitHub, which takes the version as input.

## Stack

- Kotlin and Gradle Kotlin DSL
- Paper API 26.3 and Adventure MiniMessage
- kotlinx-serialization with kaml for the config
- MaxMind DB reader for the country database

## AI involvement

AI wrote parts of the documentation and some of the code: the serializers and the effects at first, then the 2.0 rewrite with languages by IP. AI also writes the commit messages from the changes.

## Related projects

[MCBuddy Server](https://github.com/mcbuddy-ai/mcbuddy-server)—🛠️⚡ the backend for MCBuddy, an AI assistant for Minecraft, built on OpenRouter.

[MCBuddy Spigot](https://github.com/mcbuddy-ai/mcbuddy-spigot)—💬 brings MCBuddy into the game: ask the AI anything with `/ask`, right in the chat. 🎮

## From the same author

[Mo’Bosses](https://github.com/mairwunnx/mobosses)—🏆 an RPG plugin that turns ordinary mobs into epic bosses, with a deep player progression system. Every fight matters, and every level opens something new. ⚔

## License

MIT, see [LICENSE](https://github.com/MairwunNx/mojoins/blob/master/LICENSE).

## Credits

[IP Geolocation by DB-IP](https://db-ip.com), licensed under CC BY 4.0.

> 🫡 Made by Pavel Erokhin (Павел Ерохин), aka mairwunnx.
