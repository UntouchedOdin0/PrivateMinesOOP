package me.untouchedodin0.privatemines.mines;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import redempt.redlib.multiblock.Structure;
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
    private Structure structure;

    /**
     * @param mineData - The mine data to be set for the Mine
     */

    public void setMineData(MineData mineData) { this.mineData = mineData; }

    /**
     *
     * @return MineData - The mine data for the players mine
     */

    public MineData getMineData() { return mineData; }

    /**
     *
     * @param mineLocation - The location of where the mine should go
     */

    public void setMineLocation(Location mineLocation) {
        this.mineLocation = mineLocation;
    }

    /**
     *
     * @return Location - The location of where the mine is in the world
     */
    public Location getMineLocation() { return mineLocation; }

    /**
     *
     * @param cuboidRegion - The cuboid region for the mining region
     */

    public void setCuboidRegion(CuboidRegion cuboidRegion) {
        this.cuboidRegion = cuboidRegion;
    }

    /**
     *
     * @return CuboidRegion - The cuboid region of the mining area
     */

    public CuboidRegion getCuboidRegion() { return cuboidRegion; }

    /**
     *
     * @param mineOwner - The UUID of the new mine owner
     */

    public void setMineOwner(UUID mineOwner) { this.mineOwner = mineOwner; }

    /**
     * @return UUID - The UUID of the mine owner.
     */

    public UUID getMineOwner() { return mineOwner; }

    /**
     *
     * @return Structure - Gets the mine's structure.
     */

    public Structure getStructure() {
        return structure;
    }

    public void build() {
        if (mineData == null) {
            Bukkit.getLogger().info("Failed to build structure due to the mine data being null!");
        }

        Bukkit.getLogger().info("build method called...");
        Bukkit.getLogger().info("MultiBlockStructure: " + mineData.getMultiBlockStructure());
        Bukkit.getLogger().info("Location " + mineLocation);
        mineData.getMultiBlockStructure().build(mineLocation);
    }

    public void delete() {
        if (mineData == null) {
            Bukkit.getLogger().info("Failed to delete the mine due to mine data being null!");
        }
        this.structure = mineData.getMultiBlockStructure().assumeAt(getMineLocation());
        structure.getRegion().forEachBlock(block -> {
            block.setType(Material.AIR, false);
        });
    }
}
