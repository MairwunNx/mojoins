# Mo’Joins

<p align="center">
  <a href="https://modrinth.com/plugin/mojoins"><img alt="Modrinth" src="https://img.shields.io/modrinth/dt/mojoins?logo=modrinth&label=Modrinth&color=00AF5C"></a>
  <a href="https://hangar.papermc.io/MairwunNx/MoJoins"><img alt="Hangar" src="https://img.shields.io/hangar/dt/MoJoins?label=Hangar&color=1E88E5"></a>
  <a href="https://www.curseforge.com/projects/1766153"><img alt="CurseForge" src="https://img.shields.io/curseforge/dt/1766153?logo=curseforge&label=CurseForge&color=F16436"></a>
  <a href="https://github.com/MairwunNx/mojoins/releases/latest"><img alt="Release" src="https://img.shields.io/github/v/release/MairwunNx/mojoins?label=release"></a>
  <img alt="Paper 26.3" src="https://img.shields.io/badge/Paper-26.3-F7CF0D">
  <img alt="Java 25" src="https://img.shields.io/badge/Java-25-E76F00">
  <a href="https://github.com/MairwunNx/mojoins/actions/workflows/build.yml"><img alt="Build" src="https://img.shields.io/github/actions/workflow/status/MairwunNx/mojoins/build.yml?branch=master&label=build"></a>
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/github/license/MairwunNx/mojoins"></a>
</p>

🎉 **Mo’Joins** приветствует игроков на серверах Paper на их родном языке. Плагин заменяет ванильные строки о входе и выходе вашим текстом, лично встречает новичков, а каждый вход отмечает звуком и кольцом частиц.

[English version](https://github.com/MairwunNx/mojoins/blob/master/README.md)

[![Играйте на Moonlands: чистая ваниль, сложные мобы, ИИ, приваты. ru.minecraft.mairwunnx.com](https://raw.githubusercontent.com/MairwunNx/mojoins/master/media/og-adv-image.ru.png)](https://minecraft.mairwunnx.com)

## Что вы получаете

- 🌍 **Каждый читает на своём языке.** Mo’Joins выбирает его по IP: английский, русский, испанский, португальский и немецкий идут в комплекте, а свои можно добавить.
- 📨 **Строка на каждый случай:** вход, личное приветствие при первом входе, строка «поздоровайтесь» для остальных и выход — всё в MiniMessage.
- ✨ **Звуки, кольцо частиц и фейерверк** по желанию — его взрыв никого не ранит.
- 🗣️ **Игрок выбирает сам:** `/mojoins language` показывает языки, каждый — в один клик.
- 🤫 **Тихий вход для админов,** а игроки в ванише заходят и выходят без следа.
- 🧩 **Работает с PlaceholderAPI, MiniPlaceholders и Plan** — все необязательны.
- 🔒 **Без аккаунтов и без отправки данных:** база стран лежит на вашем сервере.
- ⚙️ **`/mojoins reload`** применяет изменения без перезапуска.

## Как начать

1. Скачайте jar с [Modrinth](https://modrinth.com/plugin/mojoins), [Hangar](https://hangar.papermc.io/MairwunNx/MoJoins) или [GitHub](https://github.com/MairwunNx/mojoins/releases/latest/download/mojoins.jar) либо добавьте `mojoins` в `MODRINTH_PROJECTS` в [itzg/minecraft-server](https://docker-minecraft-server.readthedocs.io).
2. Положите его в `plugins/` на Paper 26.3 с Java 25 и перезапустите сервер.
3. Зайдите — и строка поприветствует вас. Как её поменять, рассказывает страница [First steps](https://github.com/MairwunNx/mojoins/wiki/First-steps).

## Документация

Подробное руководство — в [вики](https://github.com/MairwunNx/mojoins/wiki), на английском.

**Для игроков и владельцев серверов:** [Installation](https://github.com/MairwunNx/mojoins/wiki/Installation) · [First steps](https://github.com/MairwunNx/mojoins/wiki/First-steps) · [How a player gets their language](https://github.com/MairwunNx/mojoins/wiki/How-a-player-gets-their-language) · [Adding a language](https://github.com/MairwunNx/mojoins/wiki/Adding-a-language) · [Commands and permissions](https://github.com/MairwunNx/mojoins/wiki/Commands-and-permissions) · [Messages](https://github.com/MairwunNx/mojoins/wiki/Messages) · [Configuration](https://github.com/MairwunNx/mojoins/wiki/Configuration) · [PlaceholderAPI](https://github.com/MairwunNx/mojoins/wiki/PlaceholderAPI) · [MiniPlaceholders](https://github.com/MairwunNx/mojoins/wiki/MiniPlaceholders) · [Plan](https://github.com/MairwunNx/mojoins/wiki/Plan) · [FAQ](https://github.com/MairwunNx/mojoins/wiki/FAQ)

**Для разработчиков:** [Building from source](https://github.com/MairwunNx/mojoins/wiki/Building-from-source) · [Stack and architecture](https://github.com/MairwunNx/mojoins/wiki/Stack-and-architecture) · [AI involvement](https://github.com/MairwunNx/mojoins/wiki/AI-involvement) · [Releasing](https://github.com/MairwunNx/mojoins/wiki/Releasing)

## Связанные проекты

- **[Moonlands](https://minecraft.mairwunnx.com)** — сервер со сложным выживанием, где всё это работает.
- **[WorldAPI](https://github.com/MairwunNx/minecraft-worldapi)** — REST и WebSocket API к миру, игрокам и статистике сервера на Paper, Fabric или NeoForge.
- **[Minecraft MCP](https://github.com/MairwunNx/minecraft-mcp)** — даёт ИИ-клиенту читать сервер и управлять им через WorldAPI.
- **[Allay](https://github.com/allay-ai/minecraft)** — ИИ-помощник в игре: спросите через `/ask` прямо в чате.
- **[Allay Server](https://github.com/allay-ai/server)** — бэкенд Allay.
- **[Mo’Afks](https://github.com/MairwunNx/moafks)** — безопасный режим AFK, который не мешает пропустить ночь.
- **[Mo’Bosses](https://github.com/MairwunNx/mobosses)** — монстры появляются боссами с классами, способностями и добычей.
- **[minecraft-exporter](https://github.com/MairwunNx/minecraft-exporter)** — метрики Prometheus из WorldAPI.
- **[minecraft-grafana](https://github.com/MairwunNx/minecraft-grafana)** — дашборд Grafana для WorldAPI.

---

[IP Geolocation by DB-IP](https://db-ip.com), лицензия CC BY 4.0.

> 🫡 Сделал Павел Ерохин (Pavel Erokhin), он же mairwunnx.
