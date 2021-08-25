package com.snowgears.domination.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This is a custom event that is called when the DominationStartTimer reaches 0 and the gates in the world open.
 */

public class SpawnGatesOpenEvent extends Event{

    private static final HandlerList handlers = new HandlerList();

    public SpawnGatesOpenEvent() {
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
