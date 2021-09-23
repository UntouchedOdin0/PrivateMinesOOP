package me.untouchedodin0.privatemines.mines;

import org.bukkit.Location;
import redempt.redlib.region.CuboidRegion;

import java.util.UUID;

public class Mine {

    /*
        mineType: The type of mine (of which where the blocks come from etc..)
        mineLocation: Where the blocks are actual mine is within the world
        cuboidRegion: The mining area of the actual mine, (could this go to MineType?)
     */

    private MineData mineData;
    private Location mineLocation;
    private CuboidRegion cuboidRegion;
    private UUID mineOwner;

    public void setMineData(MineData mineData) { this.mineData = mineData; }

    public MineData getMineData() { return mineData; }

    public void setMineLocation(Location mineLocation) {
        this.mineLocation = mineLocation;
    }

    public Location getMineLocation() { return mineLocation; }

    public void setCuboidRegion(CuboidRegion cuboidRegion) {
        this.cuboidRegion = cuboidRegion;
    }

    public CuboidRegion getCuboidRegion() { return cuboidRegion; }

    public void setMineOwner(UUID mineOwner) { this.mineOwner = mineOwner; }

    public UUID getMineOwner() { return mineOwner; }

    public Mine build(Location location) {
        mineData.getMultiBlockStructure().build(location);
        return this;
    }
}
