package com.snowgears.domination.command;

import com.snowgears.domination.Domination;
import com.snowgears.domination.structure.Base;
import com.snowgears.domination.structure.Spawn;
import com.snowgears.domination.structure.Structure;
import com.snowgears.domination.structure.StructureModule;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * This is the command executor for the /structure command (for defining custom structure).
 */
public class StructureCommand implements CommandExecutor {

    private Domination plugin;

    public StructureCommand(Domination instance){
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)){
            sender.sendMessage("This command is only usable by players.");
            return true;
        }
        Player player = (Player)sender;

        if(!player.isOp() || (plugin.usePerms() && !player.hasPermission("domination.operator"))){
            player.sendMessage(ChatColor.RED+"You do not have access to that command.");
            return true;
        }
        String baseCommand = plugin.getStructureCommand();

        if(args.length == 0){
            player.sendMessage(ChatColor.AQUA+"/"+baseCommand+" list"+ChatColor.GRAY+" - lists all structure in current world");
            player.sendMessage(ChatColor.AQUA+"/"+baseCommand+" define <base/spawn> <name>"+ChatColor.GRAY+" - creates a new structure");
            player.sendMessage(ChatColor.AQUA+"/"+baseCommand+" select <name>"+ChatColor.GRAY+" - selects a structure for editing");
            player.sendMessage(ChatColor.AQUA+"/"+baseCommand+" module <module>"+ChatColor.GRAY+" - switches to the module of the selected structure");
            player.sendMessage(ChatColor.AQUA+"/"+baseCommand+" deselect"+ChatColor.GRAY+" - deselects current structure");
            player.sendMessage(ChatColor.AQUA+"/"+baseCommand+" remove"+ChatColor.GRAY+" - removes selected structure entirely");
            player.sendMessage(ChatColor.AQUA+"/"+baseCommand+" color <color>"+ChatColor.GRAY+" - sets the color of the selected structure");
            player.sendMessage(ChatColor.AQUA+"/"+baseCommand+" direction"+ChatColor.GRAY+" - sets the direction of the selected structure to your position");
            player.sendMessage(ChatColor.AQUA+"/"+baseCommand+" save"+ChatColor.GRAY+" - saves all defined structure to file(s)");
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
            else if(args[0].equalsIgnoreCase("direction")) {
                Structure structure = plugin.getStructureManager().getSelectedStructure(player);
                if (structure == null)
                    player.sendMessage(ChatColor.RED + "You do not have a structure selected.");
                else {
                    BlockFace direction = yawToFace(player.getLocation().getYaw());
                    structure.setDirection(direction);
                    player.sendMessage(ChatColor.GRAY + "The direction of <"+structure.getName()+"> has been set to "+direction.toString()+".");
                }
            }
            else if(args[0].equalsIgnoreCase("save")) {
                plugin.getStructureManager().saveStructures();
                player.sendMessage(ChatColor.GREEN + "Structures saved to file(s).");
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
            else if(args[0].equalsIgnoreCase("color")){

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
                structure.setColor(color, null);
                //plugin.getStructureManager().addStructure(structure); TODO
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
                System.out.println("[Domination] Name of created structure: "+structure.getName());
                plugin.getStructureManager().selectStructure(player, structure.getName());
                player.sendMessage(ChatColor.GREEN+"You have created a new "+args[1]+" named "+args[2]+".");
                player.sendMessage(ChatColor.GRAY+"It has automatically been selected.");
                player.sendMessage(ChatColor.GRAY+"To begin defining locations, run /structure module <module>");
            }
        }

        return true;
    }

    protected BlockFace yawToFace(float yaw) {
        double rotation = (yaw - 90) % 360;
        if(rotation < 0)
            rotation += 360;

        if(0 <= rotation && rotation < 22.5)
            return BlockFace.WEST;
        else if(22.5 <= rotation && rotation < 67.5)
            return BlockFace.NORTH_WEST;
        else if(67.5 <= rotation && rotation < 112.5)
            return BlockFace.NORTH;
        else if(112.5 <= rotation && rotation < 157.5)
            return BlockFace.NORTH_EAST;
        else if(157.5 <= rotation && rotation < 202.5)
            return BlockFace.EAST;
        else if(202.5 <= rotation && rotation < 247.5)
            return BlockFace.SOUTH_EAST;
        else if(247.5 <= rotation && rotation < 292.5)
            return BlockFace.SOUTH;
        else if(292.5 <= rotation && rotation < 337.5)
            return BlockFace.SOUTH_WEST;
        return BlockFace.WEST;
    }
}
