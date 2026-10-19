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

🎉 **Mo’Joins** приветствует игроков на серверах Paper на их родном языке. Плагин заменяет ванильные строки о входе и выходе вашим текстом в MiniMessage, встречает новичков личным приветствием, а при входе играет звук и рисует кольцо частиц. Фейерверк и эффекты зелий — по желанию.

[English version](https://github.com/MairwunNx/mojoins/blob/master/README.md)

## Возможности

- 🌍 **Каждый читает на своём языке.** Плагин определяет страну игрока по IP и выбирает язык: английский, русский, испанский, португальский и немецкий идут в комплекте. Строку о входе видят все, но каждый — на своём языке.
- 📨 **Сообщения на каждый случай:** вход, личное приветствие при первом входе, строка «поздоровайтесь» для остальных и выход. Подстановки: `<player>`, `<display_name>`, `<online>` и всё из PlaceholderAPI через `<papi:…>`.
- 🔊 **Звуки** на обычный и на первый вход.
- ✨ **Кольцо частиц** вокруг вошедшего и **фейерверк** по желанию — его взрыв никого не ранит.
- 🛡️ **Эффекты зелий при входе**, по умолчанию выключены: например, несколько секунд сопротивления.
- 📜 **В логе сервера остаются ванильные строки «joined the game»**, так что программы, которые читают лог, продолжают работать.
- 🗣️ **Игрок сам выбирает язык:** `/mojoins language` показывает языки, `/mojoins language ru` выбирает один, `/mojoins language auto` возвращает выбор плагину.
- 🤫 **Тихий вход для админов:** `/mojoins silent` прячет ваши входы и выходы от остальных.
- 👻 **Понимает ванишь:** игрок, которого спрятал SuperVanish, PremiumVanish, Essentials или любой плагин с меткой `vanished`, заходит и выходит без строк, звуков и частиц, а `<online>` его не считает.
- 🧩 **PlaceholderAPI**, если он стоит: `<papi:player_health>` в сообщении показывает `%player_health%`.
- 🧩 **MiniPlaceholders**, если он стоит: его теги работают в сообщениях, а `<mojoins_language>`, `<mojoins_country>` и `<mojoins_silent>` — в TAB и других плагинах.
- 📊 **Plan**, если он стоит: язык и тихий режим каждого игрока на его странице, а на странице сервера — диаграмма языков.
- ⚙️ **`/mojoins reload`** применяет изменения без перезапуска.

## Попробовать вживую

Mo’Joins работает на Moonlands — моём сервере со сложным выживанием для друзей. Заходите, плагин вас поприветствует.

[![Играйте на Moonlands: чистая ваниль, сложные мобы, ИИ, приваты. ru.minecraft.mairwunnx.com](https://raw.githubusercontent.com/MairwunNx/mojoins/master/media/og-adv-image.ru.png)](https://minecraft.mairwunnx.com)

Адрес для игроков из России — `ru.minecraft.mairwunnx.com`, для остальных — `minecraft.mairwunnx.com`. На сайте — живая карта, статистика игроков и зал славы.

## Установка

Mo’Joins нужен Paper 26.3 или его форк, например Purpur, на Java 25. PlaceholderAPI, MiniPlaceholders и Plan — по желанию.

### Через Docker (рекомендуем)

Если сервер работает в [itzg/minecraft-server](https://docker-minecraft-server.readthedocs.io), он сам скачает Mo’Joins. Достаточно назвать его в `MODRINTH_PROJECTS`:

```yaml
services:
  minecraft:
    image: itzg/minecraft-server:java25
    environment:
      EULA: "TRUE"
      TYPE: PAPER
      VERSION: "26.3"
      MODRINTH_PROJECTS: |
        mojoins
    ports: ["25565:25565"]
    volumes: ["./data:/data"]
```

`mojoins:2.3.1` закрепляет версию. Необязательные плагины добавляются в тот же список, по одному на строку: `placeholderapi`, `plan`. Чтобы брать Mo’Joins с GitHub, укажите `PLUGINS: https://github.com/MairwunNx/mojoins/releases/latest/download/mojoins.jar`.

### Вручную

1. Скачайте jar с [Modrinth](https://modrinth.com/plugin/mojoins), [Hangar](https://hangar.papermc.io/MairwunNx/MoJoins) или [GitHub](https://github.com/MairwunNx/mojoins/releases/latest/download/mojoins.jar).
2. Положите его в `plugins/` и перезапустите сервер.

### Первые шаги

При первом запуске Mo’Joins создаёт `plugins/MoJoins/config.yml` и по файлу на каждый язык в `plugins/MoJoins/lang/`, а ещё скачивает базу стран. Зайдите на сервер — строка о входе встретит вас на вашем языке.

Чтобы поменять то, что читают все, поправьте строку в `lang/ru.yml`:

```yaml
join: "<green>+</green> <white><player></white> <gray>зашёл, в сети <online></gray>"
```

Выполните `/mojoins reload`, и следующий вход покажет «+ Steve зашёл, в сети 4». В остальных файлах `lang/` та же строка на других языках.

## Как выбирается язык

Плагин перебирает источники из `locales.sources` по порядку:

1. **`ip`** — страна по IP-адресу игрока. `locales.countries` сопоставляет языкам коды стран: Россия, Беларусь и Казахстан получают русский, Испания и Латинская Америка — испанский, Бразилия и Португалия — португальский, Германия, Австрия и Швейцария — немецкий.
2. **`client`** — язык, выбранный в игре, если для него есть файл в `lang/`.

Если ничего не подошло, игрок получает `locales.default`. Язык, выбранный через `/mojoins language`, важнее всех этих правил, пока игрок не выполнит `/mojoins language auto`. Проверить, какой язык достался игроку: `/mojoins locale <игрок>`.

Поиск страны не требует аккаунта и никуда не отправляет адреса. При первом запуске плагин скачивает бесплатную базу стран DB-IP (4 МБ, 8 МБ в распакованном виде) в `plugins/MoJoins/geoip/` и обновляет её раз в 30 дней. Игроки, которые заходят через прокси или ретранслятор, видны с его адресом и получают язык его страны.

### Как добавить язык

1. Скопируйте `lang/en.yml`, например, в `lang/it.yml` и переведите.
2. Добавьте строку в `locales.countries`: `it: [ IT, SM, VA ]`.
3. Выполните `/mojoins reload`.

Ключ, которого нет в файле языка, берётся из языка по умолчанию. Пустая строка отключает сообщение для этого языка.

## Команды и права

| Команда | Что делает |
|---|---|
| `/mojoins language [код\|auto]` | Показывает языки, выбирает ваш или с `auto` возвращает выбор плагину |
| `/mojoins reload` | Перечитывает `config.yml` и файлы языков |
| `/mojoins locale [игрок]` | Показывает язык игрока, его страну и язык игры |
| `/mojoins silent` | Включает или выключает для вас тихий режим |

Допустим, друг из Москвы играет с английским языком игры. `/mojoins locale Steve` ответит:

```
Steve: язык ru, страна RU, язык игры en_US
```

IP оказался важнее языка игры. Если Steve удобнее читать по-английски, он выполнит `/mojoins language en`.

`/mojoins language` доступна каждому игроку, выбор сохраняется между перезапусками. `reload` и `locale` требуют `mojoins.admin`, `silent` — `mojoins.silent`; у операторов оба права есть по умолчанию. Тихий режим сохраняется между перезапусками и действует, пока у вас есть `mojoins.silent`. Он прячет строки в чате, звуки и частицы, но в списке игроков вас видно.

## PlaceholderAPI

Если стоит [PlaceholderAPI](https://modrinth.com/plugin/placeholderapi), пишите в любом сообщении `<papi:name>`, и там появится `%name%` для игрока, который зашёл или вышел: `<papi:luckperms_prefix><player>`. Mo’Joins не ищет `%…%` в ваших текстах, так что знак процента остаётся знаком процента. Без PlaceholderAPI такие теги ничего не выводят.

## MiniPlaceholders

Если стоит [MiniPlaceholders](https://modrinth.com/plugin/miniplaceholders), строки входа и выхода понимают его теги как есть, для игрока, который зашёл или вышел: `<luckperms_prefix><player>`. А Mo’Joins добавляет три тега для других плагинов, каждый про игрока, которому его показывают:

| Тег | Что показывает |
|---|---|
| `<mojoins_language>` | На каком языке он читает сообщения, например `ru` |
| `<mojoins_country>` | Страну его IP-адреса или ничего |
| `<mojoins_silent>` | `true`, пока его входы не объявляются |

## Plan

Если стоит [Plan](https://modrinth.com/plugin/plan), Mo’Joins добавляет свой раздел на его страницы. Во вкладке «Plugins» у игрока видно, на каком языке он читает сообщения, какая страна выбрала этот язык и входит ли он тихо; язык попадает и в колонку таблицы игроков. В «Plugins Overview» сервера — диаграмма языков и сколько тихих игроков сейчас в сети. Plan сохраняет значения, когда игрок заходит, и держит последние, пока его нет. IP-адрес в Plan не попадает.

## Сборка

Нужен JDK 25; если его нет, Gradle скачает сам.

```shell
git clone https://github.com/MairwunNx/mojoins
cd mojoins
./gradlew build
```

Готовый jar — в `build/libs/`. Релизы собирает workflow «Release» на GitHub, версия передаётся ему на входе.

## Стек

- Kotlin и Gradle Kotlin DSL
- Paper API 26.3 и Adventure MiniMessage
- kotlinx-serialization с kaml для конфига
- MaxMind DB reader для базы стран

## Участие ИИ

ИИ написал часть документации и часть кода: сначала сериализаторы и эффекты, потом переписанную версию 2.0 с языками по IP. Сообщения коммитов тоже пишет ИИ по изменениям.

## Связанные проекты

[Allay Server](https://github.com/allay-ai/server) — 🛠️⚡ бэкенд Allay, ИИ-помощника для Minecraft, на OpenRouter.

[Allay](https://github.com/allay-ai/minecraft) — 🪽 приводит Allay в игру: спрашивайте ИИ о чём угодно командой `/ask` прямо в чате. 🎮

## От того же автора

[Mo’Bosses](https://github.com/mairwunnx/mobosses) — 🏆 RPG-плагин, который превращает обычных мобов в эпических боссов, с продуманной системой прогрессии игрока. Каждый бой имеет значение, а каждый уровень открывает новые возможности. ⚔

## Лицензия

MIT, смотрите [LICENSE](https://github.com/MairwunNx/mojoins/blob/master/LICENSE).

## Благодарности

[IP Geolocation by DB-IP](https://db-ip.com), лицензия CC BY 4.0.

> 🫡 Сделал Павел Ерохин (Pavel Erokhin), он же mairwunnx.
