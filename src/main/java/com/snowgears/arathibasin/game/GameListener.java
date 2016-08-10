package com.snowgears.arathibasin.game;

import com.snowgears.arathibasin.ArathiBasin;
import org.bukkit.event.Listener;

/**
 * Listener class for all custom events used in the Arathi Basin game.
 *
 * <P>All actions are provided to {@link ScoreManager} for handling.
 */

public class GameListener implements Listener{

    private ArathiBasin plugin;

    public GameListener(ArathiBasin instance){
        plugin = instance;
    }
}
