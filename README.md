# Mo’Joins

<p align="center">
  <a href="https://modrinth.com/plugin/mojoins"><img alt="Modrinth" src="https://img.shields.io/modrinth/dt/mojoins?logo=modrinth&label=Modrinth&color=00AF5C"></a>
  <a href="https://hangar.papermc.io/MairwunNx/MoJoins"><img alt="Hangar" src="https://img.shields.io/hangar/dt/MoJoins?label=Hangar&color=1E88E5"></a>
  <a href="https://github.com/MairwunNx/mojoins/releases/latest"><img alt="Release" src="https://img.shields.io/github/v/release/MairwunNx/mojoins?label=release"></a>
  <img alt="Paper 26.3" src="https://img.shields.io/badge/Paper-26.3-F7CF0D">
  <img alt="Java 25" src="https://img.shields.io/badge/Java-25-E76F00">
  <a href="https://github.com/MairwunNx/mojoins/actions/workflows/build.yml"><img alt="Build" src="https://img.shields.io/github/actions/workflow/status/MairwunNx/mojoins/build.yml?branch=master&label=build"></a>
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/github/license/MairwunNx/mojoins"></a>
</p>

🎉 **Mo’Joins** greets players on Paper servers in their own language. It replaces the vanilla join and quit lines with your text, welcomes newcomers personally, and marks each join with a sound and a ring of particles.

[Русская версия](https://github.com/MairwunNx/mojoins/blob/master/README.RU.md)

[![Play on Moonlands: pure vanilla, hard mobs, AI, claims. minecraft.mairwunnx.com](https://raw.githubusercontent.com/MairwunNx/mojoins/master/media/og-adv-image.png)](https://minecraft.mairwunnx.com)

## What you get

- 🌍 **Every player reads their own language.** Mo’Joins picks it by IP: English, Russian, Spanish, Portuguese, and German come bundled, and you can add more.
- 📨 **A line for every moment:** a join line, a personal welcome on the first join, a “say hi” line for everyone else, and a quit line, all in MiniMessage.
- ✨ **Sounds, a ring of particles, and an optional firework** whose blast hurts nobody.
- 🗣️ **Players choose for themselves:** `/mojoins language` lists the languages, each a click away.
- 🤫 **Silent joins for admins,** and vanished players join and leave without a trace.
- 🧩 **Works with PlaceholderAPI, MiniPlaceholders, and Plan,** all optional.
- 🔒 **No account, no data sent out:** the country database lives on your server.
- ⚙️ **`/mojoins reload`** applies changes without a restart.

## Get started

1. Download the jar from [Modrinth](https://modrinth.com/plugin/mojoins), [Hangar](https://hangar.papermc.io/MairwunNx/MoJoins), or [GitHub](https://github.com/MairwunNx/mojoins/releases/latest/download/mojoins.jar), or add `mojoins` to `MODRINTH_PROJECTS` in [itzg/minecraft-server](https://docker-minecraft-server.readthedocs.io).
2. Put it in `plugins/` on Paper 26.3 with Java 25, and restart the server.
3. Join, and see your line greet you. [First steps](https://github.com/MairwunNx/mojoins/wiki/First-steps) shows how to change it.

## Documentation

The [wiki](https://github.com/MairwunNx/mojoins/wiki) has the manual.

**For players and server owners:** [Installation](https://github.com/MairwunNx/mojoins/wiki/Installation) · [First steps](https://github.com/MairwunNx/mojoins/wiki/First-steps) · [How a player gets their language](https://github.com/MairwunNx/mojoins/wiki/How-a-player-gets-their-language) · [Adding a language](https://github.com/MairwunNx/mojoins/wiki/Adding-a-language) · [Commands and permissions](https://github.com/MairwunNx/mojoins/wiki/Commands-and-permissions) · [Messages](https://github.com/MairwunNx/mojoins/wiki/Messages) · [Configuration](https://github.com/MairwunNx/mojoins/wiki/Configuration) · [PlaceholderAPI](https://github.com/MairwunNx/mojoins/wiki/PlaceholderAPI) · [MiniPlaceholders](https://github.com/MairwunNx/mojoins/wiki/MiniPlaceholders) · [Plan](https://github.com/MairwunNx/mojoins/wiki/Plan) · [FAQ](https://github.com/MairwunNx/mojoins/wiki/FAQ)

**For developers:** [Building from source](https://github.com/MairwunNx/mojoins/wiki/Building-from-source) · [Stack and architecture](https://github.com/MairwunNx/mojoins/wiki/Stack-and-architecture) · [AI involvement](https://github.com/MairwunNx/mojoins/wiki/AI-involvement) · [Releasing](https://github.com/MairwunNx/mojoins/wiki/Releasing)

## Related projects

- **[Moonlands](https://minecraft.mairwunnx.com)** – the hard-survival server where all of these run.
- **[WorldAPI](https://github.com/MairwunNx/minecraft-worldapi)** – REST and WebSocket API to the world, players, and stats of a Paper, Fabric, or NeoForge server.
- **[Minecraft MCP](https://github.com/MairwunNx/minecraft-mcp)** – lets an AI client read and run a server through WorldAPI.
- **[Allay](https://github.com/allay-ai/minecraft)** – an AI helper in the game: ask with `/ask`, right in the chat.
- **[Allay Server](https://github.com/allay-ai/server)** – the backend behind Allay.
- **[Mo’Afks](https://github.com/MairwunNx/moafks)** – a safe AFK mode that doesn’t block the night.
- **[Mo’Bosses](https://github.com/MairwunNx/mobosses)** – monsters that spawn as bosses with classes, abilities, and loot.
- **[minecraft-exporter](https://github.com/MairwunNx/minecraft-exporter)** – Prometheus metrics from WorldAPI.
- **[minecraft-grafana](https://github.com/MairwunNx/minecraft-grafana)** – the Grafana dashboard for WorldAPI.

---

[IP Geolocation by DB-IP](https://db-ip.com), licensed under CC BY 4.0.

> 🫡 Made by Pavel Erokhin (Павел Ерохин), aka mairwunnx.
