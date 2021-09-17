package me.untouchedodin0.privatemines.mines;

import org.bukkit.Location;
import redempt.redlib.region.CuboidRegion;

public class Mine {

    /*
        mineType: The type of mine (of which where the blocks come from etc..)
        mineLocation: Where the blocks are actual mine is within the world
        cuboidRegion: The mining area of the actual mine, (could this go to MineType?)
     */

    private MineData mineData;
    private Location mineLocation;
    private CuboidRegion cuboidRegion;

    public void setMineData(MineData mineData) { this.mineData = mineData; }

    public MineData getMineType() { return mineData; }

    public void setMineLocation(Location mineLocation) {
        this.mineLocation = mineLocation;
    }

    public void setCuboidRegion(CuboidRegion cuboidRegion) {
        this.cuboidRegion = cuboidRegion;
    }

    public Location getMineLocation() { return mineLocation; }

    public CuboidRegion getCuboidRegion() { return cuboidRegion; }
}
