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

import com.cryptomorin.xseries.XMaterial;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.storage.MineStorage;
import me.untouchedodin0.privatemines.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.blockdata.DataBlock;
import redempt.redlib.misc.Task;
import redempt.redlib.misc.WeightedRandom;
import redempt.redlib.multiblock.Structure;
import redempt.redlib.region.CuboidRegion;
import redempt.redlib.region.Region;

import java.util.UUID;

public class Mine {

    /*
        mineType: The type of mine (of which where the blocks come from etc..)
        mineLocation: Where the blocks are actual mine is within the world
        cuboidRegion: The mining area of the actual mine, (could this go to MineType?)
     */

    private final PrivateMines privateMines;
    private final Material airMaterial = XMaterial.AIR.parseMaterial();
    private MineType mineType;
    private Location mineLocation;
    private Location spawnLocation;
    private Location npcLocation;
    private Location corner1;
    private Location corner2;
    private CuboidRegion cuboidRegion;
    private UUID mineOwner;
    private Structure structure;
    private WeightedRandom<Material> weightedRandom;
    private boolean debugMode;
    private boolean isAutoResetting;
    private Task resetTask;
    private Utils utils;

    public Mine(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.utils = new Utils(privateMines);
    }

    /**
     * @return MineData - The mine data for the players mine
     */

    public MineType getMineType() {
        return mineType;
    }

    /**
     * @param mineType - The mine data to be set for the Mine
     */

    public void setMineType(MineType mineType) {
        this.mineType = mineType;
    }

    /**
     * @return Location - The location of where the mine is in the world
     */

    public Location getMineLocation() {
        return mineLocation;
    }

    /**
     * @param mineLocation - The location of where the mine should go
     */

    public void setMineLocation(Location mineLocation) {
        this.mineLocation = mineLocation;
    }

    /**
     * @return Location - The location of where the spawn location is in the world
     */

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    /**
     * @param spawnLocation - The location where the player should spawn
     */

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    /**
     * @return Location - The location of where the npc location is in the world
     */

    public Location getNpcLocation() {
        return npcLocation;
    }

    /**
     * @param npcLocation - The location of where the npc should go
     */

    public void setNpcLocation(Location npcLocation) {
        this.npcLocation = npcLocation;
    }

    /**
     * @return Location - The corner 1 location
     */

    public Location getCorner1() {
        return corner1;
    }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    /**
     * @return Location - The corner 2 location
     */

    public Location getCorner2() {
        return corner2;
    }

    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }

    /**
     * @return CuboidRegion - The cuboid region of the mining area
     */

    public CuboidRegion getCuboidRegion() {
        return cuboidRegion;
    }

    /**
     * @param cuboidRegion - The cuboid region for the mining region
     */

    public void setCuboidRegion(CuboidRegion cuboidRegion) {
        this.cuboidRegion = cuboidRegion;
    }

    /**
     * @return UUID - The UUID of the mine owner.
     */

    public UUID getMineOwner() {
        return mineOwner;
    }

    /**
     * @param mineOwner - The UUID of the new mine owner
     */

    public void setMineOwner(UUID mineOwner) {
        this.mineOwner = mineOwner;
    }

    /**
     * @return Structure - Gets the mine's structure.
     */

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public WeightedRandom<Material> getWeightedRandom() {
        return weightedRandom;
    }

    public void setWeightedRandom(WeightedRandom<Material> weightedRandom) {
        this.weightedRandom = weightedRandom;
    }

    /*
        The method for creating the structures into the world
     */

    public void build() {

        // Simple check to make sure the type isn't null

        if (mineType == null) {
            Bukkit.getLogger().info("Failed to build structure due to the mine type being null!");
        }

        // Get the main class
        PrivateMines privateMines = PrivateMines.getPlugin(PrivateMines.class);

        // Set the debugMode field to true or false
        this.debugMode = privateMines.isDebugMode();

        // Initialise the util class
        Utils utils = new Utils(privateMines);

        // Print out more debug stuff if the mode is enabled
        if (debugMode) {
            privateMines.getLogger().info("MultiBlockStructure: " + mineType.getMultiBlockStructure());
            privateMines.getLogger().info("Location " + mineLocation);
        }

        Region assumeRegion = mineType.getMultiBlockStructure().assumeAt(mineLocation).getRegion();

        World world = mineLocation.getWorld();
        int minHeight = 0;
        int maxHeight = 0;

//        if (world != null) {
//            minHeight = world.getMinHeight();
//            maxHeight = world.getMaxHeight();
//        }

        Location assumeStart = assumeRegion.getStart();
        Location assumeEnd = assumeRegion.getEnd();

        this.structure = mineType.getMultiBlockStructure().build(mineLocation);

//        if (assumeStart.getBlockY() > minHeight && assumeStart.getBlockY() < maxHeight) {
//            if (assumeEnd.getBlockY() > minHeight && assumeEnd.getBlockY() < maxHeight) {
//                // Build the multi block structure at the location and set the structure field
//
//                this.structure = mineType.getMultiBlockStructure().build(mineLocation);
//            } else {
//                privateMines.getLogger().warning(
//                        "Failed to create structure due to the end height" +
//                                "either being too high or too low!");
//                return;
//            }
//        } else {
//            privateMines.getLogger().warning(
//                    "Failed to create structure due to the start height" +
//                            "either being too high or too low!");
//            return;
//        }

        // Simple check to make sure the structure isn't null

        if (this.structure == null) {
            Bukkit.getLogger().info("Structure is null");
        }

        // Set the mines spawn and npc locations using the relative location from the mineType's

        this.spawnLocation = utils.getRelative(structure, mineType.getSpawnLocation());
        this.npcLocation = utils.getRelative(structure, mineType.getNpcLocation());
        this.corner1 = utils.getRelative(structure, mineType.getCorner1());
        this.corner2 = utils.getRelative(structure, mineType.getCorner2());

        // Initialize the block manager

        BlockDataManager blockDataManager = privateMines.getBlockDataManager();

        // Initialize the data block

        DataBlock dataBlock = blockDataManager.getDataBlock(mineLocation.getBlock());

        /*
            Create a cuboid region with the two corners
            Expand the region (small fix for the mines not fully filling)
            Set the mines cuboid region with the created region
            set the spawn and npc location blocks to air
            Finally set and save the values into the block data manager
         */

        CuboidRegion cuboidRegion = new CuboidRegion(corner1, corner2);
        cuboidRegion.expand(1, 0, 1, 0, 1, 0);
        setCuboidRegion(cuboidRegion);

        if (airMaterial != null) {
            spawnLocation.getBlock().setType(airMaterial, false);
            npcLocation.getBlock().setType(airMaterial, false);

            dataBlock.set("owner", getMineOwner());
            dataBlock.set("type", getMineType().getName());
            dataBlock.set("weightedRandom", getMineType().getMaterials());
            blockDataManager.save();
        }
    }

    public void teleportPlayer(Player player) {
        if (!isAutoResetting) {
            this.isAutoResetting = true;
        }
        player.teleport(spawnLocation);
    }

    public void delete() {
        PrivateMines privateMines = PrivateMines.getPlugin(PrivateMines.class);
        if (mineType == null) {
            privateMines.getLogger().info("Failed to delete the mine due to mine data being null!");
        }
        MineStorage mineStorage = privateMines.getMineStorage();

        this.structure = getStructure();

        if (mineOwner != null) {
            mineStorage.removeMine(mineOwner);
            if (airMaterial != null) {
                structure.getRegion().forEachBlock(block -> block.setType(airMaterial, false));
            }
        }
        cancelResetTask();
    }

    // Nice little reset system for filling in the cuboid region using the mine type's weighted random.
    public void reset() {
        if (mineLocation == null) return;
        if (mineType == null) {
            privateMines.getLogger().warning("Failed to reset mine due to the type being null!");
            return;
        }

        if (mineType.getWeightedRandom().getWeights().isEmpty()) {
            privateMines.getLogger().warning("There were no materials in the weighted random!");
            return;
        }

        CuboidRegion cuboidRegion = getCuboidRegion();
        cuboidRegion.forEachBlock(block -> block.setType(mineType.getWeightedRandom().roll(), false));
        teleportPlayer(Bukkit.getPlayer(getMineOwner()));
    }

    public void startAutoResetTask() {
        int time;
        long intervalTime;

        if (mineType != null) {
            time = mineType.getResetTime();
            intervalTime = utils.minutesToBukkit(time);

            this.resetTask = Task.syncRepeating(privateMines, () -> {
                CuboidRegion cuboidRegion = getCuboidRegion();
                cuboidRegion.forEachBlock(block -> block.setType(mineType.getWeightedRandom().roll(), false));
            }, 0L, intervalTime);
        }
    }

    public void cancelResetTask() {
        if (this.resetTask.isCurrentlyRunning() && this.resetTask != null) {
            resetTask.cancel();
        }
    }

    /*
        This system upgrades the private mines onto the next MineData and
        replaces the structure at the location

        PLEASE DO NOT TOUCH, IT WORKS!
     */

    public void upgrade() {
        PrivateMines privateMines = PrivateMines.getPlugin(PrivateMines.class);
        Utils utils = new Utils(privateMines);
        MineFactory mineFactory = privateMines.getMineFactory();
        MineStorage mineStorage = privateMines.getMineStorage();
        MineType upgradeType = utils.getNextMineType(this);
        Player player = Bukkit.getPlayer(mineOwner);

        if (debugMode) {
            Bukkit.getLogger().info("upgradeType: " + upgradeType);
            Bukkit.getLogger().info("upgradeType Name: " + upgradeType.getName());
        }

        if (privateMines.isAtLastMineType(mineType)) {
            Bukkit.getLogger().info("Can't upgrade anymore, at highest!");
            return;
        }

        if (player != null) {
            Structure structure = getStructure();
            if (airMaterial != null) {
                Mine mine = mineStorage.getMine(mineOwner);
                mine.cancelResetTask();
                structure.getRegion().forEachBlock(block -> block.setType(airMaterial, false));
            }

            setMineType(upgradeType);
            Mine mine = mineFactory.createMine(player, mineLocation, upgradeType);
            mine.teleportPlayer(player);
            mineStorage.replaceMine(player.getUniqueId(), mine);
        }
    }
}
