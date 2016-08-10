package com.snowgears.arathibasin.command;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.structure.Structure;
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

        if(args.length == 0){
            sender.sendMessage("/arathi join");
            sender.sendMessage("/arathi join <team>");
            sender.sendMessage("/arathi quit");
            sender.sendMessage("/arathi fstart");

            //TODO delete this
            if(sender instanceof Player){
                Player player = (Player)sender;
                Structure structure = plugin.getStructureManager().getSelectedStructure(player);
                if(structure != null)
                    structure.setColor(DyeColor.WHITE);
            }
        }
        return true;
    }
}
