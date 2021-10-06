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
import org.bukkit.entity.Player;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.blockdata.DataBlock;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.misc.Task;
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

    private final PrivateMines privateMines;
    private final Utils utils;
    private final Material airMaterial = XMaterial.AIR.parseMaterial();
    private MineData mineData;
    private Location mineLocation;
    private Location spawnLocation;
    private Location npcLocation;
    private Location corner1;
    private Location corner2;
    private CuboidRegion cuboidRegion;
    private UUID mineOwner;
    private Structure structure;
    private Task task;
    private WeightedRandom<Material> weightedRandom;
    private boolean debugMode;
    private boolean isAutoResetting;

    public Mine(PrivateMines privateMines, Utils utils) {
        this.privateMines = privateMines;
        this.utils = utils;
    }

    /**
     * @return MineData - The mine data for the players mine
     */

    public MineData getMineData() {
        return mineData;
    }

    /**
     * @param mineData - The mine data to be set for the Mine
     */

    public void setMineData(MineData mineData) {
        this.mineData = mineData;
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
        this.spawnLocation = mineLocation;
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

    /**
     * @return Location - The corner 2 location
     */

    public Location getCorner2() {
        return corner2;
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

    public WeightedRandom<Material> getWeightedRandom() {
        return weightedRandom;
    }

    public void setWeightedRandom(WeightedRandom<Material> weightedRandom) {
        this.weightedRandom = weightedRandom;
    }

    public boolean isAutoResetting() {
        return isAutoResetting;
    }

    public void setAutoResetting(boolean isAutoResetting) {
        this.isAutoResetting = isAutoResetting;
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

        this.corner1 = utils.getRelative(structure, mineData.getCorner1());
        this.corner2 = utils.getRelative(structure, mineData.getCorner2());

        BlockDataManager blockDataManager = privateMines.getBlockDataManager();
        DataBlock dataBlock = blockDataManager.getDataBlock(mineLocation.getBlock());
        CuboidRegion cuboidRegion = new CuboidRegion(corner1, corner2);
        cuboidRegion.expand(1, 0, 1, 0, 1, 0);
        setCuboidRegion(cuboidRegion);
        if (airMaterial != null) {
            spawnLocation.getBlock().setType(airMaterial, false);
            npcLocation.getBlock().setType(airMaterial, false);
            dataBlock.set("location", LocationUtils.toString(mineLocation));
            dataBlock.set("spawnLocation", LocationUtils.toString(spawnLocation));
            dataBlock.set("npcLocation", LocationUtils.toString(npcLocation));
            dataBlock.set("corner1", LocationUtils.toString(corner1));
            dataBlock.set("corner2", LocationUtils.toString(corner2));
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
            if (airMaterial != null) {
                structure.getRegion().forEachBlock(b -> b.setType(airMaterial, false));
            }
        }
    }

    // Nice l
    public void reset() {
        cuboidRegion.forEachBlock(block -> {
            Material material = XMaterial.matchXMaterial(mineData.getWeightedRandom().roll()).parseMaterial();
            if (material != null) {
                block.setType(material);
            }
        });
    }

    public void autoReset() {
        this.task = Task.syncRepeating(privateMines, () -> {
            double blocksPercentageLeft = utils.getPercentageLeft(this);
            if (blocksPercentageLeft <= blocksPercentageLeft) {
                reset();
            }
        }, 0L, 20L);

        this.task = Task.syncRepeating(privateMines,
                this::reset, 0L, 20L);
    }

    public void cancelAutoReset() {
        if (task.isCurrentlyRunning()) {
            task.cancel();
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
        MineData upgradeData = utils.getNextMineData(this);
        Player player = Bukkit.getPlayer(mineOwner);

        if (debugMode) {
            Bukkit.getLogger().info("upgradeData: " + upgradeData);
            Bukkit.getLogger().info("upgradeData Name: " + upgradeData.getName());
        }

        if (privateMines.isAtLastMineData(mineData)) {
            Bukkit.getLogger().info("Can't upgrade anymore, at highest!");
            return;
        }

        setMineData(upgradeData);
        if (player != null) {
            Structure structure = getStructure();
            if (airMaterial != null) {
                structure.getRegion().forEachBlock(block -> block.setType(airMaterial, false));
            }
            Mine mine = mineFactory.createMine(player, getMineLocation(), upgradeData);
            mine.teleportPlayer(player);
            mineStorage.replaceMine(player.getUniqueId(), mine);
        }
    }
}
