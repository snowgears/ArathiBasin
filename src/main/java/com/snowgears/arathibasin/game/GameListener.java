package com.snowgears.arathibasin.game;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.events.BaseAssaultEvent;
import com.snowgears.arathibasin.events.BaseCaptureEvent;
import com.snowgears.arathibasin.events.BaseDefendEvent;
import com.snowgears.arathibasin.structure.Structure;
import com.snowgears.arathibasin.structure.StructureModule;
import com.snowgears.arathibasin.util.TitleMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;

/**
 * Listener class for all custom events used in the Arathi Basin game.
 *
 * <P>All actions are provided to ScoreManager for handling.
 */

public class GameListener implements Listener{

    private ArathiBasin plugin;

    public GameListener(ArathiBasin instance){
        plugin = instance;
    }

    @EventHandler
    public void onBaseAssault(BaseAssaultEvent event){
        BattleTeam team = plugin.getArathiGame().getTeamManager().getTeam(event.getTeamColor());
        String message = event.getNotificationColor() + event.getBase().getName() + " assaulted.";
        String subtitle = ChatColor.GRAY+"+1 Assault Point(s)";
        for(Player player : event.getBase().getWorld().getPlayers()) {
            if(containsPlayer(player, event.getPlayers())) {
                TitleMessage.sendTitle(player, 20, 40, 20, message, subtitle);
                //TODO increment assult score for player
            }
            else{
                TitleMessage.sendTitle(player, 20, 40, 20, message, null);
            }
        }
    }

    @EventHandler
    public void onBaseCapture(BaseCaptureEvent event){
        BattleTeam team = plugin.getArathiGame().getTeamManager().getTeam(event.getTeamColor());
        String message = event.getNotificationColor() + event.getBase().getName() + " captured.";
        String subtitle = ChatColor.GRAY+"+1 Capture Point(s)";
        for(Player player : event.getBase().getWorld().getPlayers()) {
            if(containsPlayer(player, event.getPlayers())) {
                TitleMessage.sendTitle(player, 20, 40, 20, message, subtitle);
                //TODO increment capture score for player
            }
            else{
                TitleMessage.sendTitle(player, 20, 40, 20, message, null);
            }
        }

    }

    @EventHandler
    public void onBaseDefend(BaseDefendEvent event){
        BattleTeam team = plugin.getArathiGame().getTeamManager().getTeam(event.getTeamColor());
        String message = event.getNotificationColor() + event.getBase().getName() + " defended.";
        String subtitle = ChatColor.GRAY+"+1 Defend Point(s)";
        for(Player player : event.getBase().getWorld().getPlayers()) {
            if(containsPlayer(player, event.getPlayers())) {
                TitleMessage.sendTitle(player, 20, 40, 20, message, subtitle);
                //TODO increment defend score for player
            }
            else{
                TitleMessage.sendTitle(player, 20, 40, 20, message, null);
            }
        }
    }

    private boolean containsPlayer(Player player, List<Player> players){
        for(Player p : players){
            if(p.getUniqueId().equals(player.getUniqueId()))
                return true;
        }
        return false;
    }

    //allow players to teleport to their own colored bases by clicking the wall map
    @EventHandler
    public void onMapClick(PlayerInteractEvent event){
        if (event.isCancelled()) {
            return;
        }
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return; // off hand packet, ignore.
        }
        Player player = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.WOOL){
            Structure s = plugin.getStructureManager().getStructure(StructureModule.BASE_MAP, event.getClickedBlock().getLocation());
            if(s != null){
                BattleTeam team = plugin.getArathiGame().getTeamManager().getCurrentTeam(player);
                if(team != null) {
                    //only allow player to teleport to own color structure
                    if (s.getColor() == team.getColor()) {
                        ArrayList<Location> beaconGlassLocations = s.getLocations(StructureModule.BASE_GLASS_BEACON);
                        if (beaconGlassLocations != null) {
                            Location tpLoc = beaconGlassLocations.get(0).clone().add(0, 1, 0);
                            player.teleport(tpLoc);
                        }
                    }
                    else{
                        player.sendMessage(ChatColor.RED + "Your team does not have control of this base.");
                    }
                }
                else
                    player.sendMessage(ChatColor.RED + "You are not currently on a team.");
            }
        }
    }
}
