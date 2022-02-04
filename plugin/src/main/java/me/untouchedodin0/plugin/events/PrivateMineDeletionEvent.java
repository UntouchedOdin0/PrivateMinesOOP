package me.untouchedodin0.plugin.events;

import me.untouchedodin0.plugin.mines.Mine;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PrivateMineDeletionEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Mine mine;

    public PrivateMineDeletionEvent(Mine mine) {
        this.mine = mine;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Mine getMine() {
        return this.mine;
    }
}
