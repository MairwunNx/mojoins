# Mo’Joins: notes for AI agents

A Paper 26.3 plugin that replaces join and quit lines with configured MiniMessage text, each player reading it in their own language, picked by IP. README.md explains it for players and server owners; README.RU.md is its Russian copy. Moonlands, the owner's server (MairwunNx/minecraft), runs it.

## Talking to the owner

- Reply in Russian, address the owner as «вы». Quotes «ёлочки», nested „лапки“, an em dash with spaces « — ».
- Code, comments, commit messages, README.md, and the English language file are in English: curly quotes, an em dash without spaces or an en dash with spaces, never a hyphen as a dash.
- README.RU.md and `lang/ru.yml` follow Russian typography. Change both READMEs and MODRINTH.md together.

## How it fits together

| File | What it does |
|---|---|
| `MoJoinsPlugin.kt` | Startup and `reload()`: reads `config.yml` and `lang/`, builds the resolver and the effects, schedules GeoIP refreshes |
| `Settings.kt` | `config.yml` through kaml, not strict: every field has a default, so old and partial configs load |
| `Languages.kt` | `Message` keys, the language texts, and `LocaleResolver`, which picks a language from country and game language |
| `GeoIp.kt` | Downloads the DB-IP country database into `geoip/` and looks up countries; lookups never touch the network |
| `JoinListener.kt` | Country lookup at login, then the join, first join, and quit messages, rendered per recipient |
| `JoinEffects.kt` | Sounds, the particle ring, the firework, potion effects; game ids resolve against registries on load |
| `MoJoinsCommand.kt` | `/mojoins reload` and `/mojoins locale [player]` |

## Rules that keep it working

- Game ids stay strings in `Settings`; `JoinEffects` resolves them once per load and logs a warning for a bad one. A bad id never fails the whole config.
- A new message gets a `Message` entry and a line in every bundled language (`src/main/resources/lang/`); `ResourcesTest` fails otherwise. Moonlands keeps its own copies in `minecraft/config/plugins/MoJoins/lang/`, so add the line there too.
- A new config key gets a default in `Settings` and the same default in `src/main/resources/config.yml`; `ResourcesTest` checks they match.
- Render messages with `Placeholder.unparsed` for player-provided text, so a name can't inject tags.
- The GeoIP download and the country lookup run off the server thread (async scheduler, `AsyncPlayerPreLoginEvent`).
- With `vanilla_log` on, the plugin writes “<name> joined the game” to the server log. Moonlands' site-api reads those lines; don't drop them.

## Building and testing

- `./gradlew build` runs the tests and builds `build/libs/mojoins-<version>.jar`, shaded and relocated under `ru.mairwunnx.mojoins.libs`. JDK 25; Gradle provisions it through the foojay resolver.
- There is no MockBukkit for Paper 26.3; tests cover the pure parts. For the rest, run a real Paper: download the jar from `https://fill.papermc.io/v3/projects/paper/versions/26.3/builds/latest`, put the plugin in `plugins/`, set `online-mode=false`, and drive the console from a script in the scratchpad.

## Releasing and Moonlands

1. Run the “Release” workflow with the new version: `gh workflow run release.yml -R MairwunNx/mojoins -f version=X.Y.Z`. It builds with `-PreleaseVersion`, tags `vX.Y.Z`, and attaches `mojoins-X.Y.Z.jar` to a GitHub release. Nothing in the repo holds the version; local and CI builds are `dev`.
2. To put it on Moonlands, point the Mo’Joins line in `minecraft/extras/jars.txt` (MairwunNx/minecraft) at the release jar, push, and deploy as its CLAUDE.md says. The server restarts only with the owner's go-ahead.

## Modrinth

The project is `mojoins` (id `dV7GQTnC`), MIT. The “Release” workflow has a `modrinth` switch, off by default: on, it runs `./gradlew modrinth`, which uploads the jar as a version and syncs MODRINTH.md as the description. MODRINTH.md is for server owners: no build, stack, or related projects, and no Moonlands banner. A change to what the plugin does goes in README.md, README.RU.md, and MODRINTH.md. It uses the `MODRINTH_TOKEN` secret; locally the same token is in `~/.secrets/cloud.env`. The icon is `media/icon.png`. Never put the token in the repo.

## Commits

Short one-line imperative English messages, one logical change per commit: “Pick each player's language by IP”.
