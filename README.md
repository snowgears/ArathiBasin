# Domination

**A capture-the-point mini-game plugin for Spigot/Paper Minecraft servers, based on the Arathi Basin battleground from World of Warcraft.**

[![Spigot](https://img.shields.io/badge/Spigot-27977-orange)](https://www.spigotmc.org/resources/arathibasin-a-domination-mini-game.27977/)
[![Discord](https://img.shields.io/badge/Discord-join-5865F2)](https://discord.gg/GpSwEWS)

Two teams battle for control of five bases on a map. While your team controls a base, it generates resources — the first team to reach **1,600 resources** (configurable) wins.

> 🎥 This plugin was featured in a **Twitch Rivals tournament with 64 popular streamers!** [Check out the gameplay here.](https://youtu.be/8UsLoQXirGg?list=PLQizl_mKEXqRvQh5gBGEu_pFZ3ltzdnDe&t=30)

---

## How to Play

- **Control bases on the map.** Each base your team controls generates resources over time.
- **To capture a base**, stand near its flag — your team will begin to *assault* it. If it remains uncontested for 1 minute (configurable), your team captures it.
- Once captured, the base is colored for your team, generates resources, and becomes a **teleport destination** from your spawn via the wall map.
- **Defend what you take** — the other team can assault your bases and flip them back at any time.
- First team to the winning score takes the game.

## Features

- 🌍 **Plug and play** — a fully built Domination world ships inside the plugin and is auto-generated on first run, with spawns and bases already configured. No setup required.
- 🔁 **Advanced queue system** — players queue up, and games start and end automatically. No manual game management needed.
- 🗺️ **Bring your own world** — set up any custom world to work with the game (see [Setting Up a Custom World](WORLD_SETUP.md)).
- ⚙️ **Configurable teams** — team names, minimum and maximum sizes.
- 🎮 **Configurable gameplay** — score to win, lobby/prep time, capture times, game time limits, world rollback, and more.
- 🏆 **Reward system** — run any console commands for winners and losers (optional).
- 💰 **Vault integration** — charge players an entry fee to play (optional).
- 📊 **PlaceholderAPI support** — expose live scores and player stats to other plugins (see [Placeholders](#placeholderapi-placeholders)).
- 🔒 **Permissions are optional** — enable them only if you want them.

### Recommended companion plugins

- A kits plugin with sign support (such as [PlayerKits](https://www.spigotmc.org/resources/playerkits-fully-configurable-kits-1-8-1-17.75185/)) so players can choose weapon/armor classes in their spawns.
- A command-sign plugin (such as [ServerSigns](https://www.spigotmc.org/resources/serversigns.10693/)) so players can join the game by hitting a sign that runs `/domination join`.

---

## Installation

1. Download the plugin jar (or [build it yourself](#building-from-source)).
2. Drop `Domination.jar` into your server's `plugins/` folder.
3. Restart the server. The default `world_domination` world is extracted and loaded automatically.
4. That's it — players can `/domination join` immediately. Tweak `plugins/Domination/config.yml` as desired.

**Optional dependencies** (soft-depends — the plugin works without them):

| Plugin | Enables |
|---|---|
| [Vault](https://www.spigotmc.org/resources/vault.34315/) | Entry fees (`vaultEntryCost`) |
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) | `%domination_*%` placeholders |
| [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) | Custom in-game tab list |

## Commands

| Command | Description | |
|---|---|---|
| `/domination` | Display all available commands | |
| `/domination join` | Join the queue to play | |
| `/domination join <team>` | Join the queue on a specific team | |
| `/domination leave` | Leave the queue (or the game if in progress) | |
| `/domination tp` | Teleport to the domination world | OP |
| `/domination start` | Force start the game | OP |
| `/domination end` | Force end the game | OP |

The `/structure` command is used to set up a custom world to work with the mini-game — the full walkthrough lives in **[WORLD_SETUP.md](WORLD_SETUP.md)**.

| Command | Description |
|---|---|
| `/structure list` | List all structures in the current world |
| `/structure define <base\|spawn> <name>` | Create a new structure |
| `/structure select <name>` | Select a structure for editing |
| `/structure module <module>` | Switch to a module of the selected structure |
| `/structure color <color>` | Set the color of the selected structure |
| `/structure direction` | Set the facing direction of the selected structure to your position |
| `/structure save` | Save the selected structure to file |
| `/structure deselect` | Deselect the current structure |
| `/structure remove` | Remove the selected structure entirely |

> The base command names (`domination`, `structure`) are configurable in `config.yml`.

## Permissions

Permissions are **off by default** (`usePermissions: false` in `config.yml`). When enabled:

| Permission | Description | Default |
|---|---|---|
| `domination.play` | Allows a player to play the domination game | OP |
| `domination.operator` | Grants operator access to the plugin (tp/start/end, structure setup) | OP |

## Configuration

`plugins/Domination/config.yml` — the key options:

| Option | Default | Description |
|---|---|---|
| `usePermissions` | `false` | Require the permissions above |
| `worldName` | `world_domination` | World folder used for the game |
| `gamemode` | `ADVENTURE` | Gamemode players are placed in |
| `rollbackWorldAfterGame` | `false` | Copy the world at game start and restore it after (useful if block placement is allowed) |
| `clearInventoryOnStart` | `true` | Stash player inventories during the game, restore after |
| `blueTeamName` / `redTeamName` | `Alliance` / `Horde` | Display names for the two teams |
| `minTeamSize` / `maxTeamSize` | `5` / `15` | Team size limits (min triggers auto-start) |
| `startWait` / `endWait` | `120` / `120` | Seconds of prep time before start / time to leave after end |
| `respawnWait` | `0` | Seconds after respawn before the wall map can be used |
| `scoreWin` / `scoreWarning` | `1600` / `1400` | Winning score and "near victory" alert threshold |
| `gameMaxTime` / `gameTimeWarning` | `0` / `0` | Optional time limit in minutes (0 = none) |
| `baseAssaultInterval` | `40` | Ticks between base scans for nearby players |
| `baseCaptureInterval` | `60` | Seconds a base must stay controlled before capture |
| `vaultEntryCost` | `0` | Entry fee (requires Vault) |
| `winnerCommands` / `loserCommands` | — | Console commands run per player at game end (use `[player]` as a placeholder) |

Messages are customizable in `config_messages.yml`.

## PlaceholderAPI Placeholders

With PlaceholderAPI installed, the `domination` expansion registers automatically:

| Placeholder | Value |
|---|---|
| `%domination_red_name%` / `%domination_blue_name%` | Team display names |
| `%domination_red_score%` / `%domination_blue_score%` | Live team scores |
| `%domination_stat_self_<stat>%` | The requesting player's stat |
| `%domination_stat_<rank>_<stat>%` | Stat of the player at scoreboard rank `<rank>` (1-based) |

Available `<stat>` values: `name`, `points`, `kills`, `deaths`, `captures`, `assaults`, `defends`. Rank-based values are prefixed with the player's team color.

---

## Building from Source

The project is a standard Maven build targeting **Java 8** and the **Spigot 1.17 API** (`api-version: 1.13`, so it runs on 1.13+ servers).

```bash
git clone https://github.com/snowgears/ArathiBasin.git
cd ArathiBasin
mvn package
```

The plugin jar is produced in `target/`. Dependencies (Spigot API, VaultAPI, PlaceholderAPI, ProtocolLib, json-simple) are pulled from the repositories declared in `pom.xml` — no manual jar installs needed.

## Project Structure (for developers)

```
src/main/java/com/snowgears/domination/
├── Domination.java          # Plugin main class — config loading, world setup, wiring
├── command/                 # /domination and /structure command handlers
├── game/                    # Core game loop: DominationGame, BattleTeam, TeamManager,
│                            #   PlayerQueue (auto start/stop), GameListener, start timer
├── score/                   # ScoreManager, per-player scores, scoreboard rendering,
│                            #   resource ticking (ScoreTick)
├── structure/               # World structures: Base, Spawn, StructureManager,
│                            #   StructureModule, SetupStructureListener (blaze-rod setup)
├── events/                  # Custom Bukkit events (see below)
└── util/                    # Config updater, messages, placeholders, player data,
                             #   update checker, and a vendored "tabbed" tab-list API
                             #   (requires ProtocolLib)
src/main/resources/
├── plugin.yml               # Plugin descriptor
├── config.yml               # Default configuration
├── config_messages.yml      # Customizable messages
└── world_domination.zip     # The bundled default world, extracted on first run
```

### Custom events

Other plugins can listen to these Bukkit events fired by Domination:

- `GameStartEvent` / `GameEndEvent`
- `SpawnGatesOpenEvent`
- `BaseAssaultEvent` / `BaseCaptureEvent` / `BaseDefendEvent`

### Structures and modules

Every physical game element in the world is a **structure** made of **modules** (named lists of block locations), saved to file per world:

- **Spawn** (one red, one blue): `SPAWN` (respawn locations), `SPAWN_GATE` (blocks removed at game start, rebuilt at game end)
- **Base** (five, neutral): `BASE_FLAG`, `BASE_GLASS_FLOOR`, `BASE_SKY`, `BASE_GLASS_BEACON`, `BASE_MAP`

See **[WORLD_SETUP.md](WORLD_SETUP.md)** for the complete world-building walkthrough.

---

## Support

Questions or issues? Come hang out in the [Discord server](https://discord.gg/GpSwEWS).
