package com.snowgears.arathibasin;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

public class TeamManager {

    private BattleTeam redTeam;
    private BattleTeam blueTeam;

    public TeamManager(){
        this.redTeam = new BattleTeam(DyeColor.RED);

        this.blueTeam = new BattleTeam(DyeColor.BLUE);
    }

    public void addPlayer(Player player){

        removePlayer(player);

        if(redTeam.size() >= blueTeam.size())
            blueTeam.add(player);
        else
            redTeam.add(player);
    }

    public void removePlayer(Player player){
        redTeam.remove(player);
        blueTeam.remove(player);
    }

    public BattleTeam getCurrentTeam(Player player){
        if(redTeam.contains(player))
            return redTeam;
        if(blueTeam.contains(player))
            return blueTeam;
        return null;
    }

    public BattleTeam getRedTeam(){
        return redTeam;
    }

    public BattleTeam getBlueTeam(){
        return blueTeam;
    }
}
