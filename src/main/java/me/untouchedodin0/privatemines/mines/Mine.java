package me.untouchedodin0.privatemines.mines;

import org.bukkit.Location;
import org.bukkit.Material;
import redempt.redlib.region.CuboidRegion;

import java.util.EnumMap;

public class Mine {

    private String name;

    private EnumMap<Material, Double> mineBlocks = new EnumMap<>(Material.class);

    private int mineTier = 1;
    private int resetTime = 1;

    private Location mineLocation;

    private CuboidRegion cuboidRegion;

    public void setName(String mineName) {
        this.name = mineName;
    }

    public void setMineBlocks(EnumMap<Material, Double> mineBlocks) {
        this.mineBlocks = mineBlocks;
    }

    public void setTier(int tier) { this.mineTier = tier; }

    public void setResetTime(int time) {
        this.resetTime = time;
    }

    public void setCuboidRegion(CuboidRegion cuboidRegion) {
        this.cuboidRegion = cuboidRegion;
    }

    public void setMineLocation(Location mineLocation) {
        this.mineLocation = mineLocation;
    }


    public String getName() {
        return name;
    }

    public EnumMap<Material, Double> getMineBlocks() { return mineBlocks; }

    public int getMineTier() { return mineTier; }

    public int getResetTime() { return resetTime; }

    public Location getMineLocation() { return mineLocation; }

    public CuboidRegion getCuboidRegion() { return cuboidRegion; }
}
