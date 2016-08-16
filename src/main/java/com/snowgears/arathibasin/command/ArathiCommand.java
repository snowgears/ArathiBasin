package com.snowgears.arathibasin.command;

import com.snowgears.arathibasin.ArathiBasin;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This is the command executor for the /arathi command.
 */

public class ArathiCommand implements CommandExecutor {

    private ArathiBasin plugin;

    public ArathiCommand(ArathiBasin instance){
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only usable by players.");
            return true;
        }
        Player player = (Player) sender;

        if (plugin.usePerms() && !player.hasPermission("arathi.play")) {
            player.sendMessage(ChatColor.RED + "You do not have access to that command.");
            return true;
        }

        boolean isOp = true;
        if (!player.isOp() || (plugin.usePerms() && !player.hasPermission("arathi.operator"))) {
            isOp = false;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.AQUA + "/arathi join" + ChatColor.GRAY + " - join the queue to play the Arathi Basin game");
            player.sendMessage(ChatColor.AQUA + "/arathi join <team>" + ChatColor.GRAY + " - join the queue to play the Arathi Basin game (on a specific team)");
            player.sendMessage(ChatColor.AQUA + "/arathi stats" + ChatColor.GRAY + " - show your stats from past games");
            player.sendMessage(ChatColor.AQUA + "/arathi leave" + ChatColor.GRAY + " - leave the queue (or game if in progress)");
            if (isOp) {
                player.sendMessage(ChatColor.RED + "/arathi tp" + ChatColor.GRAY + " - (OP) teleport to the Arathi Basin world");
                player.sendMessage(ChatColor.RED+"/arathi start"+ChatColor.GRAY+" - (OP) force start the Arathi Basin game");
                player.sendMessage(ChatColor.RED+"/arathi end"+ChatColor.GRAY+" - (OP) force end the Arathi Basin game");
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("join")) {
                boolean added = plugin.getArathiGame().addPlayer(player, null);
                if (added)
                    player.sendMessage(ChatColor.GREEN + "You have been added to the queue for the Arathi Basin game.");
                else
                    player.sendMessage(ChatColor.RED + "You are already in the queue for the Arathi Basin game.");
            } else if (args[0].equalsIgnoreCase("leave")) {
                boolean removed = plugin.getArathiGame().removePlayer(player);
                if (removed) {
                    player.sendMessage(ChatColor.GRAY + "You have been removed from the Arathi Basin game.");
                } else {
                    player.sendMessage(ChatColor.RED + "You are not currently queued for the Arathi Basin game.");
                }
            } else if (args[0].equalsIgnoreCase("stats")) {
                //TODO display stats to player
                player.sendMessage(ChatColor.GRAY + "Player stats will be displayed here.");
            } else if (args[0].equalsIgnoreCase("tp")) {
                if (!isOp)
                    player.sendMessage(ChatColor.RED + "You do not have access to that command.");
                else {
                    player.teleport(plugin.getServer().getWorld("world_arathi").getSpawnLocation());
                    player.sendMessage(ChatColor.GRAY + "You have been teleported to the Arathi Basin world.");
                }
            } else if (args[0].equalsIgnoreCase("end")) {
                if (!isOp)
                    player.sendMessage(ChatColor.RED + "You do not have access to that command.");
                else {
                    boolean ended = plugin.getArathiGame().endGame();
                    if(ended)
                        player.sendMessage(ChatColor.GRAY + "You have force ended the Arathi Basin game.");
                    else
                        player.sendMessage(ChatColor.RED + "There is currently no Arathi Basin game in progress.");
                }
            } else if (args[0].equalsIgnoreCase("start")) {
                if (!isOp)
                    player.sendMessage(ChatColor.RED + "You do not have access to that command.");
                else {
                    boolean started = plugin.getArathiGame().startGame();
                    if(started)
                        player.sendMessage(ChatColor.GRAY + "You have force started the Arathi Basin game.");
                    else
                        player.sendMessage(ChatColor.RED + "There is already an Arathi Basin game in progress.");
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("join")) {
                DyeColor color;
                if (args[1].equalsIgnoreCase(plugin.getRedTeamName()) || args[1].equalsIgnoreCase("red")) {
                    color = DyeColor.RED;
                } else if (args[1].equalsIgnoreCase(plugin.getBlueTeamName()) || args[1].equalsIgnoreCase("blue")) {
                    color = DyeColor.BLUE;
                } else {
                    player.sendMessage(ChatColor.RED + "Could not resolve <" + args[1] + "> as a team.");
                    return true;
                }
                boolean added = plugin.getArathiGame().addPlayer(player, color);
                if (added)
                    player.sendMessage(ChatColor.GREEN + "You have been added to the " + color.toString() + " team queue for the Arathi Basin game.");
                else
                    player.sendMessage(ChatColor.RED + "You are already in the queue for the Arathi Basin game.");
            }
        }
        return true;
    }
}
