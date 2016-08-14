package com.snowgears.arathibasin.game;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.score.PlayerScore;
import com.snowgears.arathibasin.score.ScoreManager;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

/**
 * This class stores information about the current game.
 */
public class ArathiGame {

    private boolean inProgress;
    private TeamManager teamManager;
    private ScoreManager scoreManager;

    public ArathiGame(){
        teamManager = new TeamManager();
        scoreManager = new ScoreManager(ArathiBasin.getPlugin());
    }

    //TODO add some sort of PlayerHandler Queue class that disperses players to this class (ArathiGame)
    //That class can save player data, implement timers + countdown Titles from 10,9....1, <Arathi Basin><Control bases to gather resources>

    //TODO addPlayer method and in method crete new PlayerScore with it
    //TODO also addPlayer method with 'DyeColor team'
    public void addPlayer(Player player){
        teamManager.addPlayer(player);
        PlayerScore score = new PlayerScore(player);
        scoreManager.savePlayerScore(score);
    }

    public boolean startGame(){
        ArathiBasin.getPlugin().getStructureManager().startStructureTasks();
        scoreManager.startScoreTask();
        inProgress = true;
        return true;
    }

    public boolean endGame(){
        ArathiBasin.getPlugin().getStructureManager().stopStructureTasks();
        ArathiBasin.getPlugin().getStructureManager().resetStructures("world_arathi");
        scoreManager.stopScoreTask();
        inProgress = false;

        //TODO send title with final score to all players and prompt them to leave
        return true;
    }

    public TeamManager getTeamManager(){
        return teamManager;
    }

    public ScoreManager getScoreManager(){
        return scoreManager;
    }

    public boolean isInProgress(){
        return inProgress;
    }
}
