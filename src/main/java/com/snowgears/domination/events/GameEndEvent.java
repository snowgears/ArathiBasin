package com.snowgears.domination.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This is a custom event that is called whenever an domination game ends.
 */

public class GameEndEvent extends Event{

    boolean wasForced;

    private static final HandlerList handlers = new HandlerList();

    public GameEndEvent(boolean wasForced) {
        this.wasForced = wasForced;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean wasForceEnded(){
        return wasForced;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
