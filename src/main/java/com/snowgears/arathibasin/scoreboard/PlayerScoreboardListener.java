package com.snowgears.arathibasin.scoreboard;

import com.snowgears.arathibasin.ArathiBasin;
import org.bukkit.event.Listener;

/**
 * Listener class for default actions that will be used on their respective scoreboards.
 *
 * <P>All actions are provided to {@link ScoreManager} for handling.
 */

public class PlayerScoreboardListener implements Listener {

    public ArathiBasin plugin = ArathiBasin.getPlugin();

    public PlayerScoreboardListener(ArathiBasin instance) {
        plugin = instance;
    }


}
