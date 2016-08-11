package com.snowgears.arathibasin.game;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.score.ScoreManager;

/**
 * This class stores information about the current game.
 */
public class ArathiGame {

    private TeamManager teamManager;
    private ScoreManager scoreManager;

    public ArathiGame(){
        teamManager = new TeamManager();
        scoreManager = new ScoreManager();
    }

    //TODO addPlayer method and in method crete new PlayerScore with it

    public boolean startGame(){
        //TODO implement some sort of countdown with titles
        ArathiBasin.getPlugin().getStructureManager().startStructureTasks();
        scoreManager.startScoreTask();
        return true;
    }

    public boolean endGame(){
        ArathiBasin.getPlugin().getStructureManager().stopStructureTasks();
        ArathiBasin.getPlugin().getStructureManager().resetStructures("world_arathi");
        scoreManager.stopScoreTask();

        //TODO send title with final score to all players and prompt them to leave
        return true;
    }

    public TeamManager getTeamManager(){
        return teamManager;
    }
}
