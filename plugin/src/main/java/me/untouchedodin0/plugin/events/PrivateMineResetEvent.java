package me.untouchedodin0.plugin.events;

import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.mines.data.MineData;
import me.untouchedodin0.plugin.world.MineWorldManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import redempt.redlib.region.CuboidRegion;

import java.util.Map;
import java.util.UUID;

public class PrivateMineResetEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final PrivateMines privateMines;
    private final Mine mine;
    private boolean isCancelled;

    public PrivateMineResetEvent(Mine mine, PrivateMines privateMines) {
        this.mine = mine;
        this.privateMines = privateMines;
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

    public double getResetPercentage() {
        return mine.getPercentage();
    }

    public MineData getMineData() {
        return mine.getMineData();
    }

    public MineType getMineType() {
        return mine.getMineType();
    }

    public Map<Material, Double> getMaterials() {
        return mine.getMaterials();
    }

    public CuboidRegion getCuboidRegion() {
        return mine.getRegion();
    }

    public IWrappedRegion getIWrappedFullRegion() {
        return mine.getIWrappedFullRegion();
    }

    public IWrappedRegion getIWrappedMiningRegion() {
        return mine.getIWrappedMiningRegion();
    }

    public Location getSpawnLocation() {
        MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
        World world = mineWorldManager.getMinesWorld();

        int spawnX = getMineData().getSpawnX();
        int spawnY = getMineData().getSpawnY();
        int spawnZ = getMineData().getSpawnZ();
        return new Location(world, spawnX, spawnY, spawnZ);
    }
}
