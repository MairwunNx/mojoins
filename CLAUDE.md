# Mo’Joins: notes for AI agents

A Paper 26.3 plugin that replaces join and quit lines with configured MiniMessage text, each player reading it in their own language, picked by IP. README.md is the shop window (README.RU.md its Russian copy); the GitHub wiki (MairwunNx/mojoins.wiki) is the manual for server owners and developers. Moonlands, the owner's server (MairwunNx/minecraft), runs it.

## Talking to the owner

- Reply in Russian, address the owner as «вы». Quotes «ёлочки», nested „лапки“, an em dash with spaces « — ».
- Code, comments, commit messages, README.md, the wiki (English only), and the English language file are in English: curly quotes, an em dash without spaces or an en dash with spaces, never a hyphen as a dash.
- README.RU.md and `lang/ru.yml` follow Russian typography. Change both READMEs and MODRINTH.md together.
- README.md and MODRINTH.md only show the plugin off: the pitch, “What you get”, “Get started”, links into the wiki, “Related projects” (the family's shared list), and the DB-IP credit its license requires. No License, build, stack, or reference sections there: commands, config keys, integrations, building, and releasing live in the wiki. Edit the wiki by cloning `https://github.com/MairwunNx/mojoins.wiki.git`.

## How it fits together

| File | What it does |
|---|---|
| `MoJoinsPlugin.kt` | Startup and `reload()`: reads `config.yml` and `lang/`, builds the resolver and the effects, schedules GeoIP refreshes |
| `Settings.kt` | `config.yml` through kaml, not strict: every field has a default, so old and partial configs load |
| `Languages.kt` | `Message` keys, the language texts, their native names, and `LocaleResolver`, which picks a language: the player's choice first, then country and game language |
| `LanguageChoice.kt` | The language a player chose with `/mojoins language`, kept in their persistent data; a choice whose file is gone counts as none |
| `GeoIp.kt` | Downloads the DB-IP country database into `geoip/` and looks up countries; lookups never touch the network |
| `JoinListener.kt` | Country lookup at login, then the join, first join, and quit messages, rendered per recipient |
| `JoinEffects.kt` | Sounds, the particle ring, the firework, potion effects; game ids resolve against registries on load |
| `MoJoinsCommand.kt` | `/mojoins language [code\|auto]` for every player (no argument lists the languages, each clickable), `/mojoins reload`, `/mojoins locale [player]`, and `/mojoins silent` |
| `Vanish.kt` | Reads the `vanished` metadata vanish plugins set; a vanished player gets no lines or effects, and `<online>` skips them |
| `Silent.kt` | Silent mode, kept in the player's persistent data and honored only with `mojoins.silent` |
| `MiniPlaceholderTags.kt` | MiniPlaceholders, optional: messages render with the player they're about as their target, so its global and audience tags resolve for that player; `<mojoins_language>`, `<mojoins_country>`, `<mojoins_silent>` read `JoinListener.seen` |
| `PlanData.kt` | The Plan extension: language, country, and silent mode per player, a language group, silent players online. Plan is optional and calls it from its own threads, so it reads only `JoinListener.seen`, which the server thread fills on join, locale change, `/mojoins language`, and `/mojoins silent` |
| `Placeholders.kt` | The `<papi:name>` tag; PlaceholderAPI is an optional dependency, so its classes load only when it's there |

## Rules that keep it working

- Game ids stay strings in `Settings`; `JoinEffects` resolves them once per load and logs a warning for a bad one. A bad id never fails the whole config.
- A new message gets a `Message` entry and a line in every bundled language (`src/main/resources/lang/`); `ResourcesTest` fails otherwise. Moonlands keeps its own copies in `minecraft/config/plugins/MoJoins/lang/`, so add the line there too.
- A new config key gets a default in `Settings` and the same default in `src/main/resources/config.yml`; `ResourcesTest` checks they match.
- Render messages with `Placeholder.unparsed` for player-provided text, so a name can't inject tags.
- MiniPlaceholders' tags reach a message through `MiniPlaceholderTags.resolver()` and `render(…, target = subject)`; player names stay `Placeholder.unparsed`. Its tags may be asked from any thread: read only `JoinListener.seen` there.
- PlaceholderAPI goes through the `<papi:…>` tag only. Never run `%…%` replacement over whole texts: it would rewrite any percent sign in them, and its output carries legacy color codes that MiniMessage can't parse.
- Join lines and effects go out at MONITOR (`onJoined`), after vanish plugins mark the player (SuperVanish at HIGH, Essentials at HIGHEST); `onJoin` at HIGHEST only clears the vanilla line. On quit, `onQuitting` reads the mark at LOWEST, before Essentials clears it at HIGHEST. Keep both orders.
- The GeoIP download and the country lookup run off the server thread (async scheduler, `AsyncPlayerPreLoginEvent`).
- With `vanilla_log` on, the plugin writes “<name> joined the game” to the server log. Moonlands' site-api reads those lines; don't drop them.

## Building and testing

- `./gradlew build` runs the tests and builds `build/libs/mojoins-<version>.jar`, shaded and relocated under `ru.mairwunnx.mojoins.libs`. JDK 25; Gradle provisions it through the foojay resolver.
- There is no MockBukkit for Paper 26.3; tests cover the pure parts. The rest runs on a real Paper; see “Checking a change”.

## Checking a change

1. `./gradlew build`: the tests and the jar.
2. A real Paper with the jar, started and stopped by the script, which fails on any error about the plugin:
   ```sh
   python3 -I .claude/scripts/paper_smoke.py --game 26.3 --plugin build/libs/mojoins-dev.jar --modrinth placeholderapi --modrinth plan --jar https://cdn.modrinth.com/data/HQyibRsN/versions/N2WfJ0ll/MiniPlaceholders-Paper-3.2.0.jar --command "miniplaceholders expansions" --expect "PlaceholderAPI found" --expect "Plan found" --expect "MiniPlaceholders found" --ignore "Plan\] Failed to enable geolocation"
   ```
   Pass `--game` the version the project targets, and `--build` to pin a Paper build. The script downloads Paper and Modrinth plugins into `~/.cache/paper-smoke` and keeps the server folder when something fails.
3. Join, first-join, and quit lines need a game client: after a release reaches Moonlands, the owner joins and leaves once. `/mojoins` in the console shows the language picked for a player.

## Autopilot

`.github/workflows/autopilot.yml` runs Claude every Monday at 03:00 UTC: `/update-deps`, then `/update-minecraft`, a fix for whatever they break, a push to `master`, and the “Release” workflow with every store when what the project ships changed. The release's `deploy` job then starts Moonlands' `ship-plugin.yml` with the `FAMILY_ACTIONS_TOKEN` secret, and Claude puts the jar on the server there. The run itself needs the `CLAUDE_CODE_OAUTH_TOKEN` secret. “Autopilot” in `~/c/personal/minecraft/CLAUDE.md` explains the family's runs. A session with the owner still releases only when the owner asks.

## Versions

Where the versions live, for `/update-deps` and `/update-minecraft` (both in `.claude/skills/`, the same files in every project of the family):

- The game: `io.papermc.paper:paper-api` (twice: `compileOnly` and `testImplementation`), `gameVersions` (Modrinth) and `platformVersions` (Hangar) in `build.gradle.kts`, and `api-version` in `src/main/resources/paper-plugin.yml`.
- PlaceholderAPI: `compileOnly("me.clip:placeholderapi:…")`, from `repo.extendedclip.com`.
- MiniPlaceholders: `compileOnly("io.github.miniplaceholders:miniplaceholders-api:…")` from Maven Central. Its Paper jar on Modrinth lagged a game version behind (3.2.0 lists up to 26.2 and runs on 26.3), so the check passes the jar's URL with `--jar` instead of `--modrinth`.
- Plan: `compileOnly("com.djrapitops:plan-api:…")`, from `repo.playeranalytics.net`. The API can run ahead of the Plan servers run: `PlanData` checks capabilities, and graphs and statistics (API 5.9) stay unused until a released Plan has them.
- Java: `jvmToolchain` in `build.gradle.kts`, the foojay resolver in `settings.gradle.kts`, and CI's `setup-java`.
- Gradle in `gradle/wrapper/gradle-wrapper.properties`; Kotlin and the Gradle plugins in the `plugins` block; libraries in `dependencies`.

## Releasing and Moonlands

1. Add a `## X.Y.Z` section to CHANGELOG.md, written for server owners, and push it. After a change that reaches what the project ships, add it to CHANGELOG.md in the same push: under the topmost “## X.Y.Z” section while that version isn't released (`gh release view vX.Y.Z` fails), or under a new section with the next version: patch for fixes, minor for features, major for breaking changes. A release then needs only the workflow, and the workflow runs only when the owner asks.
2. Run the “Release” workflow with the new version: `gh workflow run release.yml -R MairwunNx/mojoins -f version=X.Y.Z -f modrinth=true -f hangar=true`. Its `changelog` field overrides the CHANGELOG.md section; one of the two must be there. It builds with `-PreleaseVersion`, tags `vX.Y.Z`, and attaches `mojoins-X.Y.Z.jar` to a GitHub release. Nothing in the repo holds the version; local and CI builds are `dev`.
3. To put it on Moonlands, point the Mo’Joins line in `minecraft/extras/jars.txt` (MairwunNx/minecraft) at the release jar, push, and deploy as its CLAUDE.md says, which also says when a restart may happen without asking.

## Modrinth

The project is `mojoins` (id `dV7GQTnC`), MIT. The “Release” workflow has a `modrinth` switch, off by default: on, it runs `./gradlew modrinth`, which uploads the jar as a version and syncs MODRINTH.md as the description. MODRINTH.md is for server owners: no build or stack, and no Moonlands banner; it links to the wiki for the rest. A change to what the plugin does goes in the wiki page it touches, and in README.md, README.RU.md, and MODRINTH.md when it changes “What you get”. It uses the `MODRINTH_TOKEN` secret; locally the same token is in `~/.secrets/cloud.env`. The icon is `media/icon.png`. Never put the token in the repo.

## Platforms

How the family ships a project to more than one platform. A project that builds only for Paper keeps its jar's present name until it gains a second platform, so links and Moonlands' `jars.txt` keep working.

- **Modules.** One Gradle module per platform, `paper/`, `fabric/`, `neoforge/`, next to a `core/` module with what they share: the rules as plain Kotlin without game imports, data, and assets. Every module builds from one version, and one release ships them all.
- **Jars.** `<name>-paper-X.Y.Z.jar`, `<name>-fabric-X.Y.Z.jar`, and `<name>-neoforge-X.Y.Z.jar`, each attached again without the version, so `releases/latest/download/<name>-<platform>.jar` always fetches the newest.
- **Modrinth.** One project for every platform. Each platform is its own version, numbered `X.Y.Z+paper`, `X.Y.Z+fabric`, or `X.Y.Z+neoforge`, with its own loaders and dependencies; a mod's version says it runs on the client and the server.
- **Hangar.** Paper only, numbered `X.Y.Z`.
- **Moonlands** runs the Paper jar only.

## Commits

Short one-line imperative English messages, one logical change per commit: “Pick each player's language by IP”.

## Hangar

The project is `MairwunNx/MoJoins` on hangar.papermc.io, MIT, created 2026-10-13. The “Release” workflow has a `hangar` switch, off by default: on, it runs `./gradlew publishPluginPublicationToHangar`, which uploads the jar to the Release channel for Paper 26.3. The project page came from MODRINTH.md when it was created; Hangar doesn't sync it, so edit the page on the site after a big change. It uses the `HANGAR_TOKEN` secret; locally the same key is in `~/.secrets/cloud.env`. The key (made 2026-10-13 with `create_version`) uploads versions; project settings, the page, and the icon are edited on the site. The first version of a new Hangar project can't go through the API (it answers 404 until the project has one), so it went through the website. Never put the key in the repo.
