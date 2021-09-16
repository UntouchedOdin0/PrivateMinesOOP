package me.untouchedodin0.privatemines.mines;

import org.bukkit.Location;
import org.bukkit.Material;
import redempt.redlib.region.CuboidRegion;

public class Mine {

    public String name = "";
    public String type = "";

    public Material material = Material.STONE;

    public int resetTime = 1;

    public Location mineLocation;

    private CuboidRegion cuboidRegion;

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
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
