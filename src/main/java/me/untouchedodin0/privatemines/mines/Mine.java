/*
MIT License

Copyright (c) 2021 Kyle Hicks

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package me.untouchedodin0.privatemines.mines;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.storage.MineStorage;
import me.untouchedodin0.privatemines.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.blockdata.DataBlock;
import redempt.redlib.misc.WeightedRandom;
import redempt.redlib.multiblock.Structure;
import redempt.redlib.region.CuboidRegion;

import java.util.UUID;

@SuppressWarnings("unused")
public class Mine {

    /*
        mineType: The type of mine (of which where the blocks come from etc..)
        mineLocation: Where the blocks are actual mine is within the world
        cuboidRegion: The mining area of the actual mine, (could this go to MineType?)
     */

    private MineData mineData;
    private Location mineLocation;
    private Location spawnLocation;
    private Location npcLocation;

    private CuboidRegion cuboidRegion;
    private UUID mineOwner;
    private Structure structure;

    private WeightedRandom<Material> weightedRandom;

    private boolean debugMode;


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
     * @return Location - The location of where the spawn location is in the world
     */

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    /**
     *
     * @return Location - The location of where the npc location is in the world
     */

    public Location getNpcLocation() {
        return npcLocation;
    }

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

    public void setWeightedRandom(WeightedRandom<Material> weightedRandom) {
        this.weightedRandom = weightedRandom;
    }

    public WeightedRandom<Material> getWeightedRandom() {
        return weightedRandom;
    }

    public void build() {
        if (mineData == null) {
            Bukkit.getLogger().info("Failed to build structure due to the mine data being null!");
        }
        PrivateMines privateMines = PrivateMines.getPlugin(PrivateMines.class);
        this.debugMode = privateMines.isDebugMode();

        Utils utils = new Utils(privateMines);

        if (debugMode) {
            privateMines.getLogger().info("MultiBlockStructure: " + mineData.getMultiBlockStructure());
            privateMines.getLogger().info("Location " + mineLocation);
        }

        mineData.getMultiBlockStructure().build(mineLocation);
        this.structure = mineData.getMultiBlockStructure().assumeAt(mineLocation);
        this.spawnLocation = utils.getRelative(structure, mineData.getSpawnLocation());
        this.npcLocation = utils.getRelative(structure, mineData.getNpcLocation());

        Location corner1 = utils.getRelative(structure, mineData.getCorner1());
        Location corner2 = utils.getRelative(structure, mineData.getCorner2());

        CuboidRegion cuboidRegion = new CuboidRegion(corner1, corner2);
        cuboidRegion.expand(1, 0, 1, 0, 1, 0);
        setCuboidRegion(cuboidRegion);
        spawnLocation.getBlock().setType(Material.AIR, false);
        npcLocation.getBlock().setType(Material.AIR, false);
    }

    public void teleportPlayer(Player player) {
        player.teleport(spawnLocation);
    }

    public void delete() {
        PrivateMines privateMines = PrivateMines.getPlugin(PrivateMines.class);
        if (mineData == null) {
            privateMines.getLogger().info("Failed to delete the mine due to mine data being null!");
        }
        MineStorage mineStorage = privateMines.getMineStorage();

        if (mineOwner != null) {
            mineStorage.removeMine(mineOwner);

            BlockDataManager blockDataManager = privateMines.getBlockDataManager();
            DataBlock dataBlock = blockDataManager.getDataBlock(mineLocation.getBlock());
            Location location = dataBlock.getBlock().getLocation();
            this.structure = mineData.getMultiBlockStructure().assumeAt(location);
            if (debugMode) {
                privateMines.getLogger().info("delete Structure: " + structure);
            }
            structure.getRegion().forEachBlock(b -> b.setType(Material.AIR, false));
        }
    }

    public void reset() {
        cuboidRegion.forEachBlock(block ->
                block.setType(mineData.getWeightedRandom().roll(), false));
    }

    public void upgrade() {
        PrivateMines privateMines = PrivateMines.getPlugin(PrivateMines.class);
        Utils utils = new Utils(privateMines);
        MineFactory mineFactory = privateMines.getMineFactory();
        MineData upgradeData = utils.getNextMineData(this);
        Player player = Bukkit.getPlayer(mineOwner);

        if (debugMode) {
            Bukkit.getLogger().info("upgradeData: " + upgradeData);
            Bukkit.getLogger().info("upgradeData Name: " + upgradeData.getName());
        }
        setMineData(upgradeData);
        if (player != null) {
            Structure structure = getStructure();
            structure.getRegion().forEachBlock(block -> block.setType(Material.AIR, false));
            mineFactory.createMine(player, getMineLocation(), upgradeData);
        }
    }
}
