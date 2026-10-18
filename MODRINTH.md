# Mo’Joins

🎉 **Mo’Joins** greets players in their own language. It replaces the vanilla join and quit lines with your own text, welcomes newcomers with a personal message, and plays a sound and a ring of particles when someone joins.

## What you get

- 🌍 **Every player reads their own language.** The plugin finds the player’s country by IP and picks a language for it: English, Russian, Spanish, Portuguese, and German come bundled. Everyone in the chat sees the same join line, each in their own language.
- 📨 **Messages for every moment:** a join line, a personal welcome on the first join, a “say hi” line to everyone else, and a quit line. Write them in [MiniMessage](https://docs.advntr.dev/minimessage/format): colors, gradients, clickable links.
- 🔊 **Sounds** for a join and for a first join.
- ✨ **A ring of particles** around the player who joined, and an optional **firework** whose blast hurts nobody.
- 🛡️ **Potion effects on join**, off by default: a few seconds of resistance, say.
- 📜 **The server log keeps the vanilla “joined the game” lines**, so tools that read the log still work.
- 🗣️ **Players choose their own language:** `/mojoins language` lists the languages, `/mojoins language ru` picks one, `/mojoins language auto` hands the choice back to the plugin.
- 🤫 **Silent joins for admins:** `/mojoins silent` hides your joins and quits from everyone else.
- 👻 **Vanish-aware:** a player hidden by SuperVanish, PremiumVanish, Essentials, or any plugin that marks them `vanished` joins and leaves without a line, sound, or particle, and `<online>` doesn’t count them.
- 🧩 **PlaceholderAPI**, if you have it: `<papi:player_health>` in a message shows `%player_health%`.
- 🧩 **MiniPlaceholders**, if you have it: its tags work in messages, and `<mojoins_language>`, `<mojoins_country>`, `<mojoins_silent>` work in TAB and elsewhere.
- 📊 **Plan**, if you have it: each player’s language and silent mode on their page, and a pie of languages on the server’s.
- ⚙️ **`/mojoins reload`** applies changes without a restart.

## Install

1. Put the jar in your server’s `plugins/` folder and restart the server.
2. Edit `plugins/MoJoins/config.yml` and the messages in `plugins/MoJoins/lang/`.
3. Run `/mojoins reload`.

Mo’Joins needs Paper 26.3 or a fork of it, like Purpur, on Java 25.

## How a player gets their language

1. **By IP.** The plugin looks up the player’s country and checks `locales.countries`: Russia gets Russian, Brazil gets Portuguese, Germany gets German, and so on. You can change the lists.
2. **By game language.** When the country isn’t listed, the plugin takes the language set in the player’s game, if a file in `lang/` matches it.
3. **Default.** Everyone else gets `locales.default`, English out of the box.

A player who chose a language with `/mojoins language` gets it before any of these rules.

The country lookup needs no account and sends no address anywhere. On the first start, the plugin downloads a free country database (4 MB) and refreshes it every month. Players who connect through a proxy show up with the proxy’s address, so they get the proxy’s country.

**Add a language:** copy `lang/en.yml` to, say, `lang/it.yml`, translate it, add `it: [ IT, SM, VA ]` to `locales.countries`, and run `/mojoins reload`.

## Placeholders

`<player>` is the player’s name, `<display_name>` their display name, `<online>` the number of players online. With [PlaceholderAPI](https://modrinth.com/plugin/placeholderapi) installed, `<papi:name>` shows `%name%` for that player, like `<papi:luckperms_prefix><player>`. Mo’Joins doesn’t look for `%…%` in your text, so a percent sign stays a percent sign. An empty message turns it off for that language.

## Commands and permissions

| Command | What it does |
|---|---|
| `/mojoins language [code\|auto]` | Lists the languages, sets yours, or with `auto` lets the plugin pick again |
| `/mojoins reload` | Reloads the config and the messages |
| `/mojoins locale [player]` | Shows a player’s language, country, and game language |
| `/mojoins silent` | Turns silent mode on or off for you |

Every player can use `/mojoins language`; the choice stays across restarts. `reload` and `locale` need `mojoins.admin`, `silent` needs `mojoins.silent`; operators have both by default. Silent mode stays on across restarts. It hides the chat lines, sounds, and particles; you still show up in the player list.

## Links

- Source code, issues, and the full config: [GitHub](https://github.com/MairwunNx/mojoins)
- From the same author: [Mo’Bosses](https://github.com/mairwunnx/mobosses), an RPG plugin that turns ordinary mobs into epic bosses

[IP Geolocation by DB-IP](https://db-ip.com), licensed under CC BY 4.0.
