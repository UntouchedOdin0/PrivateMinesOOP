package me.untouchedodin0.privatemines.world.utils;

import org.bukkit.Location;

public enum Direction {

    NORTH(0, -1), NORTH_EAST(1, -1),
    EAST(1, 0),   SOUTH_EAST(1, 1),
    SOUTH(0, 1),  SOUTH_WEST(-1, 1),
    WEST(-1, 0),  NORTH_WEST(-1, -1);

    private final int xMulti;
    private final int zMulti;

    Direction(int xMulti, int zMulti) {
        this.xMulti = xMulti;
        this.zMulti = zMulti;
    }

    /**
     * Gets the direction for the next Private Mine
     * @return Direction
     */

    public Direction next() {
        return values()[(ordinal() + 1) % (values().length)];
    }

    /**
     *
     * @param location - The start location to be added onto
     * @param value    - The distance in blocks to add to the location
     * @return Location
     */

    public Location addTo(Location location, int value) {
        return location.clone().add(value * (double) xMulti, 0, value * (double) zMulti);
    }
}