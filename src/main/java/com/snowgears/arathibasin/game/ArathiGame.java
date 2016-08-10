package com.snowgears.arathibasin.game;

import com.snowgears.arathibasin.TeamManager;
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
        //TODO
        return true;
    }

    public boolean endGame(){
        //TODO
        return true;
    }
}
