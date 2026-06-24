# Domination
Minecraft plugin based on the Arathi Basin battleground from World of Warcraft

https://www.spigotmc.org/resources/arathibasin-a-domination-mini-game.27977/

[CENTER][IMG]http://i.imgur.com/IAshDsJ.png[/IMG]
[B][B][SIZE=7]Capture the point mini-game that comes fully preconfigured with a default world, advanced player queue system, with massive flexibility to use your own worlds![/SIZE][/B][/B]
[I][SIZE=6]Control resources for your team to win!

[URL='https://youtu.be/8UsLoQXirGg?list=PLQizl_mKEXqRvQh5gBGEu_pFZ3ltzdnDe&t=30']This plugin was featured in a Twitch Rivals tournament with 64 popular streamers! Check out the gameplay here.[/URL][/SIZE]

[SIZE=5]This plugin comes [B]preconfigured[/B] with the world shown below
It can just as easily be integrated into [B]any world you want to use[/B]! [URL='https://www.spigotmc.org/wiki/arathi-basin-world-tutorial/?noRedirect=1'](tutorial)[/URL][/SIZE]
[SIZE=6][IMG]http://i.imgur.com/bRWI9Fa.jpg[/IMG]  
[/SIZE][/I]
[U][B][SIZE=7]How to play:[/SIZE][/B][/U]
[SIZE=6][B]The goal is to control bases on the map [/B][I](default map is provided)[/I][B]. [/B][/SIZE]
[B][SIZE=6]While a base is controlled, it generates resources for your team.[/SIZE][/B]
[SIZE=6][B]The first team to gain 1,600 resources [/B][I](configurable)[/I][B] wins the game.[/B][/SIZE][/CENTER]

[CENTER][SIZE=5][B]To capture a base [/B]simply stand near it's flag and your team will begin to assault it.
[B]If it remains uncontested for 1 minute, [/B]your team will capture it. Once a base is captured, it will be fully colored and will begin to generate resources for your team! Your team will then also have access to teleport to it from their spawn via a map [I](shown in the gifs below).[/I]

[I][I][B]Once you capture the base, you will need to defend it because the other team can still assault it and try to take it for themselves![/B][/I][/I][/SIZE][/CENTER]

[CENTER][B][SIZE=6][IMG]http://i.imgur.com/fKbNtv2.jpg[/IMG]   [/SIZE][/B]

[/CENTER]
[RIGHT][B][SIZE=6][IMG]https://media.giphy.com/media/l2SpLBEPqowSbpjY4/giphy.gif[/IMG]  [/SIZE][/B][/RIGHT]
[LEFT][B][SIZE=6][IMG]https://media.giphy.com/media/9qW86tVJ7vb9e/giphy.gif[/IMG] [/SIZE][/B][/LEFT]
[RIGHT][B][SIZE=6][IMG]https://media.giphy.com/media/l3vRciF72H26tXJS0/giphy.gif[/IMG] [/SIZE][/B][/RIGHT]
[B][SIZE=6][IMG]https://media.giphy.com/media/l0MYzQcoPnMIBZAoU/giphy.gif[/IMG] [/SIZE][/B]
[RIGHT][B][SIZE=6][IMG]https://media.giphy.com/media/3o6ZtbXc16agMqVgwE/giphy.gif[/IMG] [/SIZE][/B][/RIGHT]

[B][SIZE=6]Features:[/SIZE][/B]
[LIST]
[*][SIZE=4]Domination world is included in the plugin and will be auto-generated with default spawns and bases already set up![/SIZE]
[*][SIZE=4]Advanced queueing system allows for players to join the queue for the battleground, starting and ending games [/SIZE]automatically so you don't have to manage the games!
[*]Set up your own custom world to work with the game! [I][URL='https://www.spigotmc.org/wiki/arathi-basin-world-tutorial/'](Tutorial)[/URL][/I]
[*]Configurable options for teams, including names and maximum and minimum sizes
[*]Configurable options for game, including score to win, lobby time, etc...
[*]Reward system for wins and losses (optional)
[*]Vault integration to charge players entry fees to play (optional)
[*]Permissions are optional!
[*]Plug and Play! [I](No need to setup anything if you don't want to)[/I]
[/LIST]
[CENTER][I][B][SIZE=5]It is highly recommended that you use a kits plugin with sign support (such as [URL='https://www.spigotmc.org/resources/playerkits-fully-configurable-kits-1-8-1-17.75185/']PlayerKits[/URL]) so that players are able to choose classes of weapons and armor in their respective spawns.

It could also be useful to have a plugin such as [URL='https://www.spigotmc.org/resources/serversigns.10693/']ServerSigns[/URL] so that users are able to hit a sign to join the game. (by executing the command /domination join)[/SIZE][/B][/I][/CENTER]

[SPOILER="Permissions"]
[I][B]domination.play[/B] -  allows player to play the domination game
[B]domination.operator [/B]-  allows operator access to the domination plugin[/I]
[/SPOILER]
[SPOILER="Commands"]
[I][B]/domination[/B] -  displays all available commands for the plugin
[B]/domination join [/B]-  join the queue to play the domination game
[B]/domination join <team>[/B] -  join the queue to play the domination game on a specific team
[B]/domination leave [/B]-  leave the queue (or game if in progress)
[B]/domination tp[/B]  -   [B][I](OP)[/I][/B] teleport to the domination world
[B]/domination start[/B]  -  [B][I](OP)[/I][/B] force start the domination game
[B]/domination end[/B] -  [B][I](OP)[/I][/B] force end the domination game

[B]The structure command is used to setup a new world to work with the mini-game [/B][I][URL='https://www.spigotmc.org/wiki/arathi-basin-world-tutorial/'](tutorial for this can be found here)[/URL][/I]

[B]/structure list  - [/B] lists all structure in current world
[B]/structure define <base/spawn> <name> [/B]-  creates a new structure
[B]/structure select <name>  [/B]-  selects a structure for editing
[B]/structure module <module>  [/B]-  switches to the module of the selected structure
[B]/structure deselect  [/B]-  deselects current structure
[B]/structure remove  [/B]-  removes selected structure entirely
[B]/structure color <color>"  [/B]-  sets the color of the selected structure
[B]/structure direction  [/B]- sets the direction of the selected structure to your position[/I]
[/SPOILER]
