package com.snowgears.arathibasin.events;

import com.snowgears.arathibasin.structure.Base;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a custom event that is called whenever a base is captured by Players.
 */

public class BaseCaptureEvent extends Event{

    private static final HandlerList handlers = new HandlerList();
    private Base base;
    private DyeColor teamColor;
    private List<Player> players;

    public BaseCaptureEvent(Base base, DyeColor teamColor, List<Player> players) {
        this.base = base;
        this.players = players;
        this.teamColor = teamColor;
        if(players == null)
            this.players = new ArrayList<>();
    }

    public Base getBase() {
        return base;
    }

    public DyeColor getTeamColor(){
        return teamColor;
    }

    public ChatColor getNotificationColor(){
        if(teamColor == DyeColor.BLUE)
            return ChatColor.BLUE;
        else if(teamColor == DyeColor.RED)
            return ChatColor.RED;
        return ChatColor.WHITE;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
