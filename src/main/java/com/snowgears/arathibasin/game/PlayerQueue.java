package com.snowgears.arathibasin.game;

import com.snowgears.arathibasin.ArathiBasin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * This class handles all player activity that happens while waiting for the ArathiGame to start.
 */
public class PlayerQueue {

    private ArathiBasin plugin;
    private LinkedHashMap<UUID, DyeColor> queue; //player UUID, color of Team they will be

    public PlayerQueue(ArathiBasin instance){
        plugin = instance;
        queue = new LinkedHashMap<>();
    }

    public boolean addPlayer(Player player, DyeColor teamPreference){

        if(queue.containsKey(player.getUniqueId())) {
            return false;
        }
        queue.put(player.getUniqueId(), teamPreference);

        tryToStartGame();

        if(plugin.getArathiGame().isInProgress())
            movePlayersToTeams();

        return true;
    }

    public boolean removePlayer(Player player){
        if(queue.containsKey(player.getUniqueId())){
            queue.remove(player.getUniqueId());
            return true;
        }
        return false;
    }

    private boolean tryToStartGame(){
        if(!plugin.getArathiGame().isInProgress()) {
            int redSize = 0, blueSize = 0;
            for(DyeColor color : queue.values()) {
                if(color == DyeColor.RED)
                    redSize++;
                else if(color == DyeColor.BLUE)
                    blueSize++;
                //has no preference
                else{
                    if(redSize < blueSize)
                        redSize++;
                    else
                        blueSize++;
                }
            }
            if (redSize >= plugin.getMinTeamSize() && blueSize >= plugin.getMinTeamSize()) {
                plugin.getArathiGame().startGame();
                return true;
            }
            else{
                messagePlayers(redSize, blueSize);
            }
        }
        return false;
    }

    private void movePlayersToTeams(){
        BattleTeam redTeam = plugin.getArathiGame().getTeamManager().getTeam(DyeColor.RED);
        BattleTeam blueTeam = plugin.getArathiGame().getTeamManager().getTeam(DyeColor.BLUE);

        Iterator<UUID> iterator = queue.keySet().iterator();
        while(iterator.hasNext()){
            UUID playerUUID = iterator.next();
            DyeColor color = queue.get(playerUUID);

            Player player = Bukkit.getPlayer(playerUUID);
            if(player == null)
                iterator.remove();

            if(color == null){
                if(blueTeam.size() > redTeam.size()) {
                    redTeam.add(player);
                    iterator.remove();
                }
                else{
                    blueTeam.add(player);
                    iterator.remove();
                }
            }
            else if(color == DyeColor.RED){
                if(redTeam.size() <= blueTeam.size()) {
                    redTeam.add(player);
                    iterator.remove();
                }
            }
            else{
                if(blueTeam.size() <= redTeam.size()) {
                    blueTeam.add(player);
                    iterator.remove();
                }
            }
        }
    }

    private void messagePlayers(int redSize, int blueSize){
        int redNeeded = plugin.getMinTeamSize() - redSize;
        if(redNeeded < 0)
            redNeeded = 0;
        int blueNeeded = plugin.getMinTeamSize() - blueSize;
        if(blueNeeded < 0)
            blueNeeded = 0;
        int amountNeeded = redNeeded + blueNeeded;
        String message = ChatColor.AQUA + "" +amountNeeded + ChatColor.GRAY + " more players needed to start the Arathi Basin game.";

        for(UUID uuid : queue.keySet()){
            Player player = Bukkit.getPlayer(uuid);
            if(player != null)
                player.sendMessage(message);
        }
    }

    private DyeColor getOppositeTeam(DyeColor team){
        if(team == DyeColor.RED)
            return DyeColor.BLUE;
        return DyeColor.RED;
    }
}
