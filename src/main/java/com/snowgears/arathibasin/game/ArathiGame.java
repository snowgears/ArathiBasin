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

    public boolean startGame(){
        ArathiBasin.getPlugin().getStructureManager().startStructureTasks();
        return true;
    }

    public boolean endGame(){
        ArathiBasin.getPlugin().getStructureManager().stopStructureTasks();
        //TODO
        return true;
    }

    public TeamManager getTeamManager(){
        return teamManager;
    }
}
