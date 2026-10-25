# Mo’Joins

🎉 **Mo’Joins** greets players on Paper servers in their own language. It replaces the vanilla join and quit lines with your text, welcomes newcomers personally, and marks each join with a sound and a ring of particles.

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

1. Put the jar in `plugins/` on Paper 26.3 or a fork, like Purpur, with Java 25. With [itzg/minecraft-server](https://docker-minecraft-server.readthedocs.io), add `mojoins` to `MODRINTH_PROJECTS` instead.
2. Restart the server, join, and see your line greet you.
3. Change it in `plugins/MoJoins/lang/en.yml` and run `/mojoins reload`.

## Documentation

The [wiki](https://github.com/MairwunNx/mojoins/wiki) walks you through everything: [Installation](https://github.com/MairwunNx/mojoins/wiki/Installation), [First steps](https://github.com/MairwunNx/mojoins/wiki/First-steps), [How a player gets their language](https://github.com/MairwunNx/mojoins/wiki/How-a-player-gets-their-language), [Adding a language](https://github.com/MairwunNx/mojoins/wiki/Adding-a-language), [Commands and permissions](https://github.com/MairwunNx/mojoins/wiki/Commands-and-permissions), [Messages](https://github.com/MairwunNx/mojoins/wiki/Messages), [Configuration](https://github.com/MairwunNx/mojoins/wiki/Configuration), [PlaceholderAPI](https://github.com/MairwunNx/mojoins/wiki/PlaceholderAPI), [MiniPlaceholders](https://github.com/MairwunNx/mojoins/wiki/MiniPlaceholders), [Plan](https://github.com/MairwunNx/mojoins/wiki/Plan), and the [FAQ](https://github.com/MairwunNx/mojoins/wiki/FAQ). Source code and issues live on [GitHub](https://github.com/MairwunNx/mojoins).

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
