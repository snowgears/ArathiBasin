package com.snowgears.arathibasin.command;

import com.snowgears.arathibasin.ArathiBasin;
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

        if(args.length == 0){
            sender.sendMessage("/arathi join");
            sender.sendMessage("/arathi join <team>");
            sender.sendMessage("/arathi quit");
            sender.sendMessage("/arathi fstart");
        }
        else if(args.length == 1){
            //TODO delete this
            if(sender instanceof Player){
                Player player = (Player)sender;
                if(args[0].equalsIgnoreCase("red")) {
                    plugin.getArathiGame().getTeamManager().removePlayer(player);
                    plugin.getArathiGame().getTeamManager().getRedTeam().add(player);
                    player.sendMessage("You are now on the red team.");
                }
                else if(args[0].equalsIgnoreCase("blue")){
                    plugin.getArathiGame().getTeamManager().removePlayer(player);
                    plugin.getArathiGame().getTeamManager().getBlueTeam().add(player);
                    player.sendMessage("You are now on the blue team.");
                }
                else if(args[0].equalsIgnoreCase("start")){
                    plugin.getArathiGame().startGame();
                    player.sendMessage("The arathi game has been started.");
                }
                else if(args[0].equalsIgnoreCase("end")){
                    plugin.getArathiGame().endGame();
                    player.sendMessage("The arathi game has been ended..");
                }
                else if(args[0].equalsIgnoreCase("join")){
                    plugin.getArathiGame().addPlayer(player);
                    player.sendMessage("You have been added to the game on the "+plugin.getArathiGame().getTeamManager().getCurrentTeam(player).toString()+" team.");
                }
            }
        }
        return true;
    }
}
