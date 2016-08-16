package com.snowgears.arathibasin.game;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.structure.Spawn;
import com.snowgears.arathibasin.structure.StructureModule;
import com.snowgears.arathibasin.util.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
            this.name = ArathiBasin.getPlugin().getBlueTeamName();
        else
            this.name = ArathiBasin.getPlugin().getRedTeamName();
        this.players = new HashMap<>();
    }

    public boolean add(Player player){
        if(size() >= maxSize() || players.containsKey(player.getUniqueId()))
            return false;
        players.put(player.getUniqueId(), true);
        ArathiBasin.getPlugin().getArathiGame().getTeamManager().getScoreboard().getTeam(color.toString()).addEntry(player.getName());

        //save player data to file
        new PlayerData(player);
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(this.getSpawnLocation());

        //create a new player score (and display scoreboard)
        ArathiBasin.getPlugin().getArathiGame().getScoreManager().addPlayerScore(player);

        return true;
    }

    public boolean remove(Player player){
        if(players.containsKey(player.getUniqueId())) {
            players.remove(player.getUniqueId());
            ArathiBasin.getPlugin().getArathiGame().getTeamManager().getScoreboard().getTeam(color.toString()).removeEntry(player.getName());

            //remove scoreboard from player
            ArathiBasin.getPlugin().getArathiGame().getScoreManager().removePlayerScore(player);

            PlayerData data = PlayerData.loadFromFile(player);
            if(data != null) {
                //return all player data to player
                data.apply();
            }
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

    public String getName(){
        return name;
    }

    public int getScore(){
        if(color == DyeColor.RED)
            return ArathiBasin.getPlugin().getArathiGame().getScoreManager().getRedScore();
        return ArathiBasin.getPlugin().getArathiGame().getScoreManager().getBlueScore();
    }

    public int maxSize(){
        return ArathiBasin.getPlugin().getMaxTeamSize();
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
        Spawn spawn = ArathiBasin.getPlugin().getStructureManager().getSpawn(color);
        ArrayList<Location> locations = spawn.getLocations(StructureModule.SPAWN);
        if(locations == null && locations.isEmpty())
            return null;
        int random = ThreadLocalRandom.current().nextInt(0, locations.size());
        Location loc = locations.get(random);
        loc.setYaw(spawn.getDirectionYaw());
        loc.add(0.5,0,0.5);
        return loc;
    }

    public void clear(){
        for(UUID uuid : players.keySet()){
            Player player = Bukkit.getPlayer(uuid);
            if(player != null){
                PlayerData data = PlayerData.loadFromFile(player);
                if(data != null)
                    data.apply();
            }
        }
        players.clear();
    }
}
