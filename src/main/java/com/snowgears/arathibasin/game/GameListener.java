package com.snowgears.arathibasin.game;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.structure.Structure;
import com.snowgears.arathibasin.structure.StructureModule;
import com.snowgears.arathibasin.util.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Listener class for all custom events used in the Arathi Basin game.
 *
 * <P>All actions are provided to ScoreManager for handling.
 */

public class GameListener implements Listener{

    private ArathiBasin plugin;
    private int timeTaskID;

    public GameListener(ArathiBasin instance){
        plugin = instance;
        startDaytimeTask();
    }

    @EventHandler
    public void freezePlayersOnEnd(PlayerMoveEvent event){
        if(event.getPlayer().getWorld().getName().equals("world_arathi")){
            if(plugin.getArathiGame().isEnding()) {
                Location from=event.getFrom();
                Location to=event.getTo();
                double x=Math.floor(from.getX());
                double z=Math.floor(from.getZ());
                if(Math.floor(to.getX())!=x||Math.floor(to.getZ())!=z)
                {
                    x+=.5;
                    z+=.5;
                    event.getPlayer().teleport(new Location(from.getWorld(),x,from.getY(),z,from.getYaw(),from.getPitch()));
                }
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event){
        plugin.getArathiGame().removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();

        if(plugin.getArathiGame().isInProgress()) {
            BattleTeam senderTeam = plugin.getArathiGame().getTeamManager().getCurrentTeam(player);
            //if the chat sender is not on a battle team
            if(senderTeam == null) {
                Iterator<Player> iterator = event.getRecipients().iterator();
                while(iterator.hasNext()){
                    BattleTeam team = plugin.getArathiGame().getTeamManager().getCurrentTeam(iterator.next());
                    //do not send their message to anyone on a battle team
                    if (team != null) {
                        try{
                            iterator.remove();
                        } catch (Exception e) {}
                    }
                }
            }
            //sender of the chat is on a battle team
            else{
                Iterator<Player> iterator = event.getRecipients().iterator();
                event.setMessage(ChatColor.RESET + event.getMessage());
                //TODO mess around with set format of chat to get rid of colored '>' bracket
                while(iterator.hasNext()){
                    BattleTeam team = plugin.getArathiGame().getTeamManager().getCurrentTeam(iterator.next());
                    if(plugin.getArathiGame().isEnding()){
                        //send chats to both teams
                        if (team == null) {
                            try{
                                iterator.remove();
                            } catch (Exception e) {}
                        }
                    }
                    else {
                        //only send chat to own battle team
                        if (team == null || (senderTeam.getColor() != team.getColor())) {
                            try {
                                iterator.remove();
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onWeatherChange(WeatherChangeEvent event){
        if(event.getWorld().getName().equals("world_arathi")){
            if(event.toWeatherState())
                event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onHungerChange(FoodLevelChangeEvent event){
        if(event.getEntity().getWorld().getName().equals("world_arathi")){
            event.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onGamemodeChange(PlayerGameModeChangeEvent event){
        Player player = event.getPlayer();
        if(plugin.getArathiGame().isInProgress()) {
            BattleTeam team = plugin.getArathiGame().getTeamManager().getCurrentTeam(player);
            if (team != null) {
                event.setCancelled(true);
            }
        }
    }

    private void startDaytimeTask(){
        timeTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(ArathiBasin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getWorld("world_arathi").setTime(6000); //set time to noon
            }
        }, 100L, 2000L); //every 1-2 minutes
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
                            tpLoc.setYaw(s.getDirectionYaw());
                            tpLoc.add(0.5,0,0.5);
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

    //make sure player respawns in own spawn
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        BattleTeam team = plugin.getArathiGame().getTeamManager().getCurrentTeam(player);
        if(team != null){
            Location respawn = team.getSpawnLocation();
            if(respawn != null)
                event.setRespawnLocation(respawn);
        }
    }

    //make sure that if player somehow quit without getting their old data back, return it to them when they login next
    @EventHandler
    public void onLogin(PlayerLoginEvent event){
        final Player player = event.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(ArathiBasin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                PlayerData data = PlayerData.loadFromFile(player);
                if(data != null){
                    data.apply();
                }
            }
        }, 10);
    }
}
