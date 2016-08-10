package com.snowgears.arathibasin.command;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.structure.Base;
import com.snowgears.arathibasin.structure.Spawn;
import com.snowgears.arathibasin.structure.Structure;
import com.snowgears.arathibasin.structure.StructureModule;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * This is the command executor for the /structure command (for defining custom structure).
 */
public class StructureCommand implements CommandExecutor {

    private ArathiBasin plugin;

    public StructureCommand(ArathiBasin instance){
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)){
            sender.sendMessage("This command is only usable by players.");
            return true;
        }
        Player player = (Player)sender;

        if(!player.isOp() || (plugin.usePerms() && player.hasPermission("arathi.operator"))){
            player.sendMessage(ChatColor.RED+"You do not have access to that command.");
            return true;
        }

        if(args.length == 0){
            player.sendMessage(ChatColor.AQUA+"/structure list"+ChatColor.GRAY+" - lists all structure in current world");
            player.sendMessage(ChatColor.AQUA+"/structure define <base/spawn> <name>"+ChatColor.GRAY+" - creates a new structure");
            player.sendMessage(ChatColor.AQUA+"/structure select <name>"+ChatColor.GRAY+" - selects a structure for editing");
            player.sendMessage(ChatColor.AQUA+"/structure module <module>"+ChatColor.GRAY+" - switches to the module of the selected structure");
            player.sendMessage(ChatColor.AQUA+"/structure deselect"+ChatColor.GRAY+" - deselects current structure");
            player.sendMessage(ChatColor.AQUA+"/structure remove"+ChatColor.GRAY+" - removes selected structure entirely");
            player.sendMessage(ChatColor.AQUA+"/structure setcolor <color>"+ChatColor.GRAY+" - sets the color of the selected structure");
        }
        else if(args.length == 1){
            if(args[0].equalsIgnoreCase("list")){
                player.sendMessage(ChatColor.BOLD + "Structures "+ChatColor.GRAY+"(in this world):");
                boolean noStructures = true;
                Structure selected = plugin.getStructureManager().getSelectedStructure(player);
                for (Map.Entry<String, Structure> entry : plugin.getStructureManager().getStructures().entrySet()) {
                    //only list structure in current world
                    if(player.getWorld().getName().equals(entry.getValue().getWorld().getName())) {
                        //display selected structure in GREEN and others in AQUA
                        if(selected != null && selected.getName().equals(entry.getValue().getName()))
                            player.sendMessage(ChatColor.GREEN+"  - "+entry.getKey());
                        else
                            player.sendMessage(ChatColor.AQUA+"  - "+entry.getKey());
                        noStructures = false;
                    }
                }
                if(noStructures){
                    player.sendMessage(ChatColor.ITALIC+"none");
                }
            }
            else if(args[0].equalsIgnoreCase("deselect")) {
                Structure structure = plugin.getStructureManager().getSelectedStructure(player);
                if (structure == null)
                    player.sendMessage(ChatColor.GRAY + "You do not have a structure selected.");
                else {
                    player.sendMessage(ChatColor.GRAY + "You have deselected the structure. (" + structure.getName() + ")");
                    plugin.getStructureManager().deselectStructure(player);
                }
            }
            else if(args[0].equalsIgnoreCase("remove")) {
                Structure structure = plugin.getStructureManager().getSelectedStructure(player);
                if (structure == null)
                    player.sendMessage(ChatColor.RED + "You do not have a structure selected.");
                else {
                    player.sendMessage(ChatColor.GRAY + "You have removed the structure <"+structure.getName()+">");
                    plugin.getStructureManager().deselectStructure(player);
                    plugin.getStructureManager().removeStructure(structure);
                }
            }
        }
        else if(args.length == 2){
            if(args[0].equalsIgnoreCase("select")){
                String name = args[1];
                Structure structure = plugin.getStructureManager().getStructure(name);
                if(structure == null){
                    player.sendMessage(ChatColor.RED+"There is no structure by that name.");
                    player.sendMessage(ChatColor.GRAY+"To see available structure, type /structure list.");
                    return true;
                }
                plugin.getStructureManager().selectStructure(player, structure.getName());
                player.sendMessage(ChatColor.GREEN+"You have selected the structure <"+structure.getName()+">.");
                player.sendMessage(ChatColor.GRAY+"To begin defining locations, run /structure module <module>");
            }
            else if(args[0].equalsIgnoreCase("module")){
                Structure structure = plugin.getStructureManager().getSelectedStructure(player);
                if (structure == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a structure selected.");
                    return true;
                }

                StructureModule module = null;
                try{
                    module = StructureModule.valueOf(args[1].toUpperCase());
                } catch (Exception e){
                    player.sendMessage(ChatColor.RED+"<"+args[1]+"> is not a type of module.");
                    player.sendMessage(ChatColor.GRAY+"List of all module types:");
                    String message = ChatColor.GRAY+ "";
                    if(structure instanceof Spawn){
                        for(StructureModule mod : StructureModule.values()){
                            String modName = mod.toString().toUpperCase();
                            if(mod.toString().toUpperCase().contains("SPAWN"))
                                message += modName + ", ";
                        }
                    }
                    else if(structure instanceof Base){
                        for(StructureModule mod : StructureModule.values()){
                            String modName = mod.toString().toUpperCase();
                            if(mod.toString().toUpperCase().contains("BASE"))
                                message += modName + ", ";
                        }
                    }
                    player.sendMessage(message);
                    return true;
                }
                structure.setCurrentModule(module);
                player.sendMessage(ChatColor.AQUA + "You can now begin adding/removing blocks to the <"+module.toString()+"> in <"+structure.getName()+">.");
                player.sendMessage(ChatColor.GRAY + "To add blocks, right click them with a BLAZE_ROD.");
                player.sendMessage(ChatColor.GRAY + "To remove blocks, left click them with a BLAZE_ROD.");
            }
            else if(args[0].equalsIgnoreCase("setcolor")){

                Structure structure = plugin.getStructureManager().getSelectedStructure(player);
                if (structure == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a structure selected.");
                    return true;
                }

                DyeColor color;
                try{
                    color = DyeColor.valueOf(args[1].toUpperCase());
                } catch(Exception e){
                    player.sendMessage(ChatColor.RED + "Unable to resolve color: "+args[1]);
                    return true;
                }
                structure.setColor(color);
                plugin.getStructureManager().addStructure(structure);
                player.sendMessage(ChatColor.GRAY+"The color of the selected structure has been set to "+color.toString());
            }
        }
        else if(args.length == 3){
            if(args[0].equalsIgnoreCase("define")){
                String name = args[2];
                Structure structure = null;
                if(args[1].equalsIgnoreCase("base")){
                    structure = new Base(name, player.getWorld().getName());
                }
                else if(args[1].equalsIgnoreCase("spawn")){
                    structure = new Spawn(name, player.getWorld().getName());
                }
                else{
                    player.sendMessage(ChatColor.RED+"Incorrect arguments.");
                    player.sendMessage(ChatColor.RED+"/structure define <base/spawn> <name>");
                    return true;
                }
                if(plugin.getStructureManager().getStructure(structure.getName()) != null){
                    player.sendMessage(ChatColor.RED + "There is already a structure with that name.");
                    return true;
                }

                plugin.getStructureManager().addStructure(structure);
                System.out.println("[ArathiBasin] Name of created structure: "+structure.getName());
                plugin.getStructureManager().selectStructure(player, structure.getName());
                player.sendMessage(ChatColor.GREEN+"You have created a new "+args[1]+" named "+args[2]+".");
                player.sendMessage(ChatColor.GRAY+"It has automatically been selected.");
                player.sendMessage(ChatColor.GRAY+"To begin defining locations, run /structure module <module>");
            }
        }

        return true;
    }
}
