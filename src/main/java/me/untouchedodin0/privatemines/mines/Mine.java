package me.untouchedodin0.privatemines.mines;

import redempt.redlib.region.CuboidRegion;

public class Mine {

    public String name = "";
    public String type = "";

    public String material = "";

    public int resetTime = 1;

    private CuboidRegion cuboidRegion;

    public String getName() {
        return name;
    }

    public String getMaterial() {
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
}
