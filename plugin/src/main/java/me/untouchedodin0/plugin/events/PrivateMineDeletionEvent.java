package me.untouchedodin0.plugin.events;

import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.MineType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PrivateMineDeletionEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Mine mine;
    private boolean isCancelled;

    public PrivateMineDeletionEvent(Mine mine) {
        this.mine = mine;
        this.isCancelled = false;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Mine getMine() {
        return this.mine;
    }

    public UUID getOwner() {
        return mine.getMineOwner();
    }

    public MineType getMineType() {
        return mine.getMineType();
    }
}
