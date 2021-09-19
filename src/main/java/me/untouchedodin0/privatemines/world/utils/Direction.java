package me.untouchedodin0.privatemines.world.utils;

import org.bukkit.Location;

public enum Direction {

    NORTH(0, -1), NORTH_EAST(1, -1),
    EAST(1, 0), SOUTH_EAST(1, 1),
    SOUTH(0, 1), SOUTH_WEST(-1, 1),
    WEST(-1, 0), NORTH_WEST(-1, -1);

    private final int xMulti;
    private final int zMulti;

    Direction(int xMulti, int zMulti) {
        this.xMulti = xMulti;
        this.zMulti = zMulti;
    }

    public Direction next() {
        return values()[(ordinal() + 1) % (values().length)];
    }

    public Location addTo(Location location, int value) {
        return location.clone().add(value * (double) xMulti, 0, value * (double) zMulti);
    }
}