package com.snowgears.arathibasin.events;

import com.snowgears.arathibasin.structure.Base;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a custom event that is called whenever a base is captured by Players.
 */

public class BaseDefendEvent extends Event{

    private static final HandlerList handlers = new HandlerList();
    private Base base;
    private List<Player> players;

    public BaseDefendEvent(Base base, List<Player> players) {
        this.base = base;
        this.players = players;
        if(players == null)
            this.players = new ArrayList<>();
    }

    public Base getBase() {
        return base;
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
