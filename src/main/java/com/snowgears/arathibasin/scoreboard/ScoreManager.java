package com.snowgears.arathibasin.scoreboard;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ScoreManager {

    private int redScore;
    private int blueScore;
    private HashMap<UUID, PlayerScore> playerScores;

    public ScoreManager(){
        playerScores = new HashMap<>();
    }

    public PlayerScore getPlayerScore(Player player){
        if(playerScores.containsKey(player.getUniqueId()))
            return playerScores.get(player.getUniqueId());
        return null;
    }

    public void savePlayerScore(PlayerScore score){
        playerScores.put(score.getPlayerUUID(), score);
        score.update();
    }

    public int getRedScore(){
        return redScore;
    }

    public void setRedScore(int redScore){
        this.redScore = redScore;
    }

    public int getBlueScore(){
        return blueScore;
    }

    public void setBlueScore(int blueScore){
        this.blueScore = blueScore;
    }
}
