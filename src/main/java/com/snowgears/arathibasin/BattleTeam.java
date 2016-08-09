package com.snowgears.arathibasin;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BattleTeam {

    private String name;
    private DyeColor color;
    private int score;
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
        return true;
    }

    public boolean remove(Player player){
        if(players.containsKey(player.getUniqueId())) {
            players.remove(player.getUniqueId());
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
        return score;
    }

    public void setScore(int score){
        this.score = score;
    }

    public int maxSize(){
        return ArathiBasin.getPlugin().getMaxTeamSize();
    }

    public int size(){
        return players.size();
    }
}
