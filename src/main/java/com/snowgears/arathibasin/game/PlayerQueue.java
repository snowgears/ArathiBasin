package com.snowgears.arathibasin.game;

import com.snowgears.arathibasin.ArathiBasin;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * This class handles all player activity that happens while waiting for the ArathiGame to start.
 */
public class PlayerQueue {

    private ArathiBasin plugin;
    private HashMap<UUID, DyeColor> queue; //player UUID, color of Team they will be
    private HashMap<UUID, DyeColor> playersWaiting; //player UUID, color of Team they have a preference for

    public PlayerQueue(ArathiBasin instance){
        plugin = instance;
        queue = new HashMap<>();
        playersWaiting = new HashMap<>();
    }

    public boolean addPlayer(Player player, DyeColor teamPreference){

        boolean added = tryAddPlayerToTeam(player, teamPreference);
        if(!added){
            player.sendMessage("You will be added to the queue when a space is available.");
            return false;
        }
        else{
            //take care of any waiting players
            Iterator<Map.Entry<UUID, DyeColor>> iterator = playersWaiting.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<UUID, DyeColor> entry = iterator.next();
                Player toAdd = Bukkit.getPlayer(entry.getKey());
                if(toAdd != null) {
                    boolean wasAdded = tryAddPlayerToTeam(toAdd, entry.getValue());
                    if (wasAdded)
                        iterator.remove();
                }
            }
        }
        return true;
    }

    private boolean tryAddPlayerToTeam(Player player, DyeColor team){

        if(getTeamSize(DyeColor.RED) == plugin.getMaxTeamSize() && getTeamSize(DyeColor.BLUE) == plugin.getMaxTeamSize()){
            player.sendMessage("All spaces are currently full to join the Arathi Basin game.");
            return false;
        }

        //if they have a preference, try to put them on that team
        if(team != null) {
            DyeColor opposite = getOppositeTeam(team);
            if (getTeamSize(team) > getTeamSize(opposite)) {
                playersWaiting.put(player.getUniqueId(), team);
                return false;
            } else {
                queue.put(player.getUniqueId(), team);
                player.sendMessage("You have been added to the Arathi Basin queue on the " + team.toString() + " team.");
                //try to put them in the game if it is progress
                putPlayerInGame(player);
                return true;
            }
        }
        else{
            if(getTeamSize(DyeColor.RED) > getTeamSize(DyeColor.BLUE)){
                tryAddPlayerToTeam(player, DyeColor.BLUE);
            }
            else{
                tryAddPlayerToTeam(player, DyeColor.RED);
            }
        }
        return false;
    }

    private boolean putPlayerInGame(Player player){
        if(plugin.getArathiGame().isInProgress()){
            if(queue.containsKey(player.getUniqueId())) {
                DyeColor team = queue.get(player.getUniqueId());
                BattleTeam battleTeam = plugin.getArathiGame().getTeamManager().getTeam(team);
                if(battleTeam.size() < plugin.getMaxTeamSize()) {
                    battleTeam.add(player);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private int getTeamSize(DyeColor color){
        int size = 0;
        for(DyeColor c : queue.values()){
            if(c == color){
                size++;
            }
        }
        return size;
    }

    private DyeColor getOppositeTeam(DyeColor team){
        if(team == DyeColor.RED)
            return DyeColor.BLUE;
        return DyeColor.RED;
    }
}
