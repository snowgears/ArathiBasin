package com.snowgears.domination.util;

import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by Tanner on 8/13/16.
 */
public abstract class DominationObjective implements Objective {

    @Override
    public String getName() throws IllegalStateException {
        return null;
    }

    @Override
    public String getDisplayName() throws IllegalStateException {
        return null;
    }

    @Override
    public void setDisplayName(String s) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public String getCriteria() throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isModifiable() throws IllegalStateException {
        return false;
    }

    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public void unregister() throws IllegalStateException {

    }

    @Override
    public void setDisplaySlot(DisplaySlot displaySlot) throws IllegalStateException {

    }

    @Override
    public DisplaySlot getDisplaySlot() throws IllegalStateException {
        return null;
    }

    @Override
    public Score getScore(OfflinePlayer offlinePlayer) throws IllegalArgumentException, IllegalStateException {
        return null;
    }

    @Override
    public Score getScore(String s) throws IllegalArgumentException, IllegalStateException {
        return null;
    }
}
