package com.snowgears.arathibasin.structure;

import com.snowgears.arathibasin.ArathiBasin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * This is a listener class specifically for the setup of {@link Structure}s.
 */
public class SetupStructureListener implements Listener {

    private ArathiBasin plugin;

    public SetupStructureListener(ArathiBasin instance){
        plugin = instance;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onClick(PlayerInteractEvent event){
        if (event.isCancelled()) {
            return;
        }
        try {
            if (event.getHand() == EquipmentSlot.OFF_HAND) {
                return; // off hand packet, ignore.
            }
        } catch (NoSuchMethodError error) {}

        Player player = event.getPlayer();
        Structure structure = plugin.getStructureManager().getSelectedStructure(player);
        if(structure == null)
            return;
        if(structure.getCurrentModule() == null)
            return;

        if(player.getItemInHand().getType() == Material.BLAZE_ROD) {
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                boolean added = structure.addLocation(event.getClickedBlock().getLocation());
                if(added) {
                    plugin.getStructureManager().addStructure(structure);
                    player.sendMessage(ChatColor.GRAY + "Added block to <" + structure.getCurrentModule().toString().toUpperCase() + "> in <" + structure.getName() + ">");
                }
                else{
                    player.sendMessage(ChatColor.GRAY + structure.getName()+" already contains this block.");
                }
            }
            else if(event.getAction() == Action.LEFT_CLICK_BLOCK){
                StructureModule module = structure.removeLocation(event.getClickedBlock().getLocation());
                plugin.getStructureManager().addStructure(structure);
                if(module != null){
                    player.sendMessage(ChatColor.GRAY+"Removed block from <"+module.toString().toUpperCase()+"> in <"+structure.getName()+">");
                }
                else{
                    player.sendMessage(ChatColor.GRAY+"That block is not in a module in <"+structure.getName()+">");
                }
            }
            event.setCancelled(true);
        }
    }
}
