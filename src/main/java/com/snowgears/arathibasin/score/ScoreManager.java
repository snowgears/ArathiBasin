package com.snowgears.arathibasin.score;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.structure.Base;
import com.snowgears.arathibasin.structure.Structure;
import com.snowgears.arathibasin.util.TitleMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ScoreManager {

    private int redScore;
    private int blueScore;

    private ScoreTick redTick;
    private ScoreTick blueTick;

    private HashMap<UUID, PlayerScore> playerScores;

    private ScoreTick scoreTick;
    private int scoreTaskID;

    public ScoreManager(){
        playerScores = new HashMap<>();
        redTick = new ScoreTick(DyeColor.RED);
        blueTick = new ScoreTick(DyeColor.BLUE);
    }

    public void startScoreTask(){
        scoreTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(ArathiBasin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                scoreTask();
            }
        }, 20L, 20L);
    }

    public void stopScoreTask(){
        Bukkit.getScheduler().cancelTask(scoreTaskID);
    }

    private void scoreTask(){
        int redBases = 0;
        int blueBases = 0;

        for(Structure s : ArathiBasin.getPlugin().getStructureManager().getStructures().values()){
            if(s instanceof Base){
                if(s.getColor() == DyeColor.RED){
                    redBases++;
                }
                else if(s.getColor() == DyeColor.BLUE){
                    blueBases++;
                }
            }
        }

        int redpoints = redTick.getPoints(redBases);
        this.redScore += redpoints;
        int bluepoints = blueTick.getPoints(blueBases);
        this.blueScore += bluepoints;

        //TODO delete this
        String scoreTitle = ChatColor.RED+""+redScore + "   "+ChatColor.BLUE+blueScore;
        if(redpoints > 0 || bluepoints > 0){
            TitleMessage.sendTitle(Bukkit.getPlayer("SnowGears"), 20, 40, 20, scoreTitle, null);
        }
    }

    //TODO addPlayerScore method

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
