package com.snowgears.domination.game;

import com.snowgears.domination.Domination;
import com.snowgears.domination.score.PlayerScore;
import com.snowgears.domination.structure.Spawn;
import com.snowgears.domination.structure.StructureModule;
import com.snowgears.domination.util.PlayerData;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class BattleTeam {

    private String name;
    private DyeColor color;
    private HashMap<UUID, Boolean> players;

    public BattleTeam(DyeColor color){
        this.color = color;
        if(color == DyeColor.BLUE)
            this.name = Domination.getPlugin().getBlueTeamName();
        else
            this.name = Domination.getPlugin().getRedTeamName();
        this.players = new HashMap<>();
    }

    public boolean add(Player player){
        if(size() >= maxSize() || players.containsKey(player.getUniqueId()))
            return false;

        //TODO REMOVE THIS when porting new changes back over to non Twitch event specific plugin
        //Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user "+player.getName()+" parent set "+color.name().toLowerCase());

        Bukkit.getScheduler().scheduleSyncDelayedTask(Domination.getPlugin(), new Runnable() {
            @Override
            public void run() {
                //save player data to file
                new PlayerData(player);
                player.setMaxHealth(20);
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setDisplayName(ChatColor.valueOf(color.toString()) + player.getName() + ChatColor.RESET);
                //player.setPlayerListName(player.getDisplayName()); //might not need this once scoreboards work correctly
                player.getInventory().clear();
                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(getSpawnLocation());

                //players.put(player.getUniqueId(), true);

                for(Player p : Domination.getPlugin().getDominationGame().getTeamManager().getAllPlayers()){
                    if(p != null)
                        p.sendMessage(player.getDisplayName()+ChatColor.YELLOW+" has joined the battle!");
                }

                //create a new player score (and display scoreboard)
                PlayerScore s = Domination.getPlugin().getDominationGame().getScoreManager().addPlayerScore(player);

            }
        }, 5);

        players.put(player.getUniqueId(), true);

        return true;
    }

    public boolean remove(Player player){
        if(players.containsKey(player.getUniqueId())) {

            //remove scoreboard from player
            Domination.getPlugin().getDominationGame().getScoreManager().removePlayerScore(player);

            //TODO REMOVE THIS when porting new changes back over to non Twitch event specific plugin
            //Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user "+player.getName()+" parent set spectator");

            for(Player p : Domination.getPlugin().getDominationGame().getTeamManager().getAllPlayers()){
                if(p != null)
                    p.sendMessage(player.getDisplayName()+ChatColor.YELLOW+" has left the battle.");
            }

            PlayerData data = PlayerData.loadFromFile(player);
            if(data != null) {
                //return all player data to player
                data.apply();
            }
            players.remove(player.getUniqueId());

            Domination.getPlugin().getDominationGame().getTeamManager().moveQueue();
            return true;
        }
        return false;
    }

    public boolean contains(Player player){
        return players.containsKey(player.getUniqueId());
    }

    public DyeColor getColor(){
        return color;
    }

    public ChatColor getChatColor(){
        switch (color){
            case RED:
                return ChatColor.RED;
            case BLUE:
                return ChatColor.BLUE;
            default:
                return ChatColor.WHITE;
        }
    }

    public String getName(){
        return name;
    }

    public int getScore(){
        if(color == DyeColor.RED)
            return Domination.getPlugin().getDominationGame().getScoreManager().getRedScore();
        return Domination.getPlugin().getDominationGame().getScoreManager().getBlueScore();
    }

    public int maxSize(){
        return Domination.getPlugin().getMaxTeamSize();
    }

    public int size(){
        return players.size();
    }

    public ArrayList<Player> getPlayers(){
        ArrayList<Player> allPlayers = new ArrayList<>(players.size());
        for(UUID uuid : players.keySet()){
            Player player = Bukkit.getPlayer(uuid);
            if(player != null)
                allPlayers.add(player);
        }
        return allPlayers;
    }

    public Location getSpawnLocation(){
        Spawn spawn = Domination.getPlugin().getStructureManager().getSpawn(color);
        ArrayList<Location> locations = spawn.getLocations(StructureModule.SPAWN);
        if(locations == null && locations.isEmpty())
            return null;
        int random = ThreadLocalRandom.current().nextInt(0, locations.size());
        Location loc = locations.get(random).clone();
        loc.setYaw(spawn.getDirectionYaw());
        loc.add(0.5,0,0.5);
        return loc;
    }

    public void clear(){
        for(UUID uuid : players.keySet()){
            Player player = Bukkit.getPlayer(uuid);
            if(player != null){

                //TODO REMOVE THIS when porting new changes back over to non Twitch event specific plugin
                //Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user "+player.getName()+" parent set spectator");

                PlayerData data = PlayerData.loadFromFile(player);
                if(data != null)
                    data.apply();
            }
        }
        players.clear();
    }
}
