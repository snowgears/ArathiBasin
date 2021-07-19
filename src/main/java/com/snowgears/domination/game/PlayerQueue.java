package com.snowgears.domination.game;

import com.snowgears.domination.Domination;
import com.snowgears.domination.score.PlayerScore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * This class handles all player activity that happens while waiting for the DominationGame to start.
 */
public class PlayerQueue {

    private Domination plugin;
    private LinkedHashMap<UUID, DyeColor> queue; //player UUID, color of Team they will be

    public PlayerQueue(Domination instance){
        plugin = instance;
        queue = new LinkedHashMap<>();
    }

    public boolean addPlayer(Player player, DyeColor teamPreference){

        if(plugin.isDebug()) {
            String pref;
            if(teamPreference == null)
                pref = "none";
            else
                pref = teamPreference.name();
            System.out.println("[Domination] Adding " + player.getName() + " to queue with team preference " + pref);
        }

        if(queue.containsKey(player.getUniqueId())) {
            return false;
        }
        queue.put(player.getUniqueId(), teamPreference);

        tryToStartGame();

        //TODO may need to check if try to start game returned false (game is already in progress) as to not spam players (TEST THIS)
        if(plugin.getDominationGame().isInProgress())
            movePlayersToTeams();

        for(Player worldPlayer : Bukkit.getWorld(plugin.getWorldName()).getPlayers()){
            PlayerScore score = plugin.getDominationGame().getScoreManager().getPlayerScore(worldPlayer);
            if(score != null){
                score.update();
            }
        }

        return true;
    }

    public boolean removePlayer(Player player){
        if(queue.containsKey(player.getUniqueId())){
            queue.remove(player.getUniqueId());
            return true;
        }
        return false;
    }

    public boolean contains(Player player){
        return queue.containsKey(player.getUniqueId());
    }

    private boolean tryToStartGame(){
        if(!plugin.getDominationGame().isInProgress()) {
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
                plugin.getDominationGame().startGame();
                return true;
            }
            else{
                messagePlayers(redSize, blueSize);
            }
        }
        return false;
    }

    public void movePlayersToTeams(){
//        System.out.println("Moving players from queue to game.");
        BattleTeam redTeam = plugin.getDominationGame().getTeamManager().getTeam(DyeColor.RED);
        BattleTeam blueTeam = plugin.getDominationGame().getTeamManager().getTeam(DyeColor.BLUE);

        Iterator<UUID> iterator = queue.keySet().iterator();
        while(iterator.hasNext()){
            UUID playerUUID = iterator.next();
            DyeColor color = queue.get(playerUUID);

            Player player = Bukkit.getPlayer(playerUUID);
            if(player == null)
                iterator.remove();

            if(color == null){
                if(blueTeam.size() > redTeam.size()) {
                    if(plugin.isDebug()) {
                        System.out.println("[Domination] Current team sizes - BLUE: "+blueTeam.size()+", RED: "+redTeam.size()+".Adding player to team RED.");
                    }
                    redTeam.add(player);
                    iterator.remove();
                    movePlayersToTeams();
                    return;
                }
                else{
                    if(plugin.isDebug()) {
                        System.out.println("[Domination] Current team sizes - BLUE: "+blueTeam.size()+", RED: "+redTeam.size()+".Adding player to team BLUE.");
                    }
                    blueTeam.add(player);
                    iterator.remove();
                    movePlayersToTeams();
                    return;
                }
            }
            else if(color == DyeColor.RED){
                if(redTeam.size() <= blueTeam.size()) {
                    redTeam.add(player);
                    iterator.remove();
                    movePlayersToTeams();
                    return;
                }
            }
            else{
//                System.out.println("Wanted blue team.");
//                System.out.println("Blue Size: "+blueTeam.size());
//                System.out.println("Red Size: "+redTeam.size());
                if(blueTeam.size() <= redTeam.size()) {
//                    System.out.println("Got onto blue team.");
                    blueTeam.add(player);
                    iterator.remove();
                    movePlayersToTeams();
                    return;
                }
 //               System.out.println("Done with adding players.");
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
        String message = ChatColor.AQUA + "" +amountNeeded + ChatColor.GRAY + " more players needed to start the game.";

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
