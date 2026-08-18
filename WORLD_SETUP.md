# Setting Up a Custom World for Domination

This guide walks through configuring your own world to work with Domination (previously known as Arathi Basin). It preserves the original tutorial from the Spigot wiki. The original screenshots have unfortunately been lost, but every step is fully described in text.

## Overview

To use a custom world:

1. Replace the existing `world_domination` world folder with the custom world you wish to use.
2. Set the `worldName` variable in `config.yml` to the name of your custom world folder.

The Domination game consists of 7 structures that you will need to define:

- 1 Red Spawn
- 1 Blue Spawn
- 5 Bases (neutral)

First, teleport into the domination world:

```
/domination tp
```

You will need the appropriate permissions (`domination.operator`) to do this.

This tutorial walks through setting up one Spawn and one Base. Creating the remaining Spawn and Bases follows the exact same process.

## Structures and Modules

Every structure you create in Domination consists of modules, which are named lists of block locations. You select a module, then add blocks to it by right-clicking them while holding a blaze rod. Right-clicking a block that is already in the module removes it again.

## Creating a Spawn

Each Spawn structure consists of 2 modules:

| Module | Purpose |
|---|---|
| `SPAWN` | The list of blocks that players are teleported to when joining the game and when respawning. |
| `SPAWN_GATE` | The list of blocks that are broken down when the game starts and rebuilt when the game ends (the gate holding players in until the match begins). |

### Step by step

1. Define the spawn structure (in this example we name it `RedSpawn`):

   ```
   /structure define spawn RedSpawn
   ```

2. Select the `SPAWN_GATE` module and populate it:

   ```
   /structure module SPAWN_GATE
   ```

   Equip a blaze rod in your hand and right-click each block that you want to add to the gate.

3. Select the `SPAWN` module and populate it the same way:

   ```
   /structure module SPAWN
   ```

   Tip: set the `SPAWN` locations one block above the floor so that players do not spawn inside the floor blocks.

4. Set the color of the spawn structure:

   ```
   /structure color red
   ```

5. Set the facing direction, so that when players spawn in they are not facing a random wall. Face the direction you want players to spawn facing, then run:

   ```
   /structure direction
   ```

6. Save it to file:

   ```
   /structure save
   ```

That's the red team spawn done! Deselect the structure so you don't accidentally add or remove locations from it:

```
/structure deselect
```

You can view your created structures at any time with `/structure list`.

Repeat the same steps for the blue spawn (e.g. `/structure define spawn BlueSpawn`, then `/structure color blue`).

## Creating a Base

Each Base structure consists of 5 modules:

| Module | Purpose |
|---|---|
| `BASE_FLAG` | The single location of the flag that sits on top of the glass floor. This is where the base scans for nearby players from. |
| `BASE_GLASS_FLOOR` | The list of blocks underneath the flag. A 5x5 block area works best. |
| `BASE_SKY` | The decorative set of blocks above the beacon. This is just for looks. |
| `BASE_GLASS_BEACON` | The location of the glass just above the beacon. This changes the color of the beacon beam when the base is taken over. |
| `BASE_MAP` | The two locations (one in each team's spawn) of this base on the wall map. Optional, used to let players teleport to owned bases from their spawn. |

### Step by step

1. Define the base structure (in this example we name it `Farm`):

   ```
   /structure define base Farm
   ```

2. Select and populate each module in turn, exactly as with the spawn. Run `/structure module <MODULE>` and then right-click blocks with the blaze rod:

   ```
   /structure module BASE_FLAG
   /structure module BASE_GLASS_FLOOR
   /structure module BASE_SKY
   /structure module BASE_GLASS_BEACON
   /structure module BASE_MAP
   ```

   For `BASE_MAP`, don't forget to add the base to both maps (one in each spawn)! Also remember that the left side of the map in one spawn could be the right side of the map in the other spawn.

3. Set the direction of the base. This is the direction players will face when they warp to the base by clicking its `BASE_MAP` location. Face that direction and run:

   ```
   /structure direction
   ```

4. Save it to file:

   ```
   /structure save
   ```

Repeat for the remaining four bases. The classic Arathi Basin names are Farm, Stables, Mine, Lumber Mill, and Blacksmith, but you can name them whatever you like.

### Verifying a base

To check that a base is set up correctly, run:

```
/structure color pink
```

The base should change color! Just make sure to change the color back to white before saving, since bases are meant to start as neutral:

```
/structure color white
/structure save
```

## Quick Reference

| Command | Description |
|---|---|
| `/domination tp` | Teleport to the domination world (OP) |
| `/structure define <base\|spawn> <name>` | Create a new structure |
| `/structure select <name>` | Select an existing structure for editing |
| `/structure module <module>` | Switch to a module of the selected structure |
| `/structure color <color>` | Set the color of the selected structure |
| `/structure direction` | Set facing direction to your current facing |
| `/structure save` | Save the selected structure to file |
| `/structure deselect` | Deselect the current structure |
| `/structure remove` | Remove the selected structure entirely |
| `/structure list` | List all structures in the current world |

Setup tool: blaze rod (right-click blocks to add or remove them from the selected module).

Questions? Come check out the [Discord server](https://discord.gg/GpSwEWS)!
