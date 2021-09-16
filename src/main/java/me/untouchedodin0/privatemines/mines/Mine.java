package me.untouchedodin0.privatemines.mines;

import org.bukkit.Location;
import org.bukkit.Material;
import redempt.redlib.region.CuboidRegion;

import java.util.EnumMap;

public class Mine {

    public String name = "";
    public String type = "";

    public EnumMap<Material, Double> mineBlocks = new EnumMap<>(Material.class);

    public int resetTime = 1;

    public Location mineLocation;

    private CuboidRegion cuboidRegion;

    public String getName() {
        return name;
    }

    public EnumMap<Material, Double> getMineBlocks() {
        return mineBlocks;
    }

    public String getType() {
        return type;
    }

    public int getResetTime() {
        return resetTime;
    }

    public CuboidRegion getCuboidRegion() {
        return cuboidRegion;
    }

    public Location getMineLocation() {
        return mineLocation;
    }
}
