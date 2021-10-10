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

package me.untouchedodin0.privatemines.factory;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mines.Mine;
import me.untouchedodin0.privatemines.mines.MineType;
import me.untouchedodin0.privatemines.storage.MineStorage;
import me.untouchedodin0.privatemines.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.blockdata.DataBlock;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.misc.LocationUtils;

public class MineFactory {

    private final boolean debugMode;
    PrivateMines privateMines;
    Utils utils;
    MineStorage mineStorage;
    MineFactory mineFactory;
    MineType defaultMineType;
    BlockDataManager blockDataManager;
    ConfigManager minesConfig;

    public MineFactory(PrivateMines privateMines, BlockDataManager blockDataManager) {
        this.privateMines = privateMines;
        this.utils = privateMines.getUtils();
        this.mineStorage = privateMines.getMineStorage();
        this.mineFactory = privateMines.getMineFactory();
        this.defaultMineType = privateMines.getDefaultMineData();
        this.blockDataManager = blockDataManager;
        this.debugMode = privateMines.isDebugMode();
        this.minesConfig = privateMines.getMinesConfig();
    }

    public Mine createMine(Player player, Location location) {
        if (defaultMineType == null) {
            privateMines.getLogger().warning("Failed to create mine due to defaultMineData being null");
        }

        Utils utils = new Utils(privateMines);
        Block block = location.getBlock();
        String userUUID = player.getUniqueId().toString();
        Mine mine = new Mine(privateMines, utils);
        mine.setMineOwner(player.getUniqueId());
        mine.setMineLocation(location);
        mine.setMineData(defaultMineType);
        mine.setWeightedRandom(defaultMineType.getWeightedRandom());
        mine.build();

        Location corner1 = utils.getRelative(mine.getStructure(), defaultMineType.getCorner1());
        Location corner2 = utils.getRelative(mine.getStructure(), defaultMineType.getCorner2());

        Location spawnLocation = utils.getRelative(mine.getStructure(), defaultMineType.getSpawnLocation());
        Location npcLocation = utils.getRelative(mine.getStructure(), defaultMineType.getNpcLocation());

        mine.setCorner1(corner1);
        mine.setCorner2(corner2);
        mine.setSpawnLocation(spawnLocation);
        mine.setNpcLocation(npcLocation);

        mineStorage.addMine(player.getUniqueId(), mine);

//        structure.getRelative(defaultMineData.getCorner1()).getBlock().getLocation();

        DataBlock dataBlock = blockDataManager.getDataBlock(block);
        dataBlock.set("owner", String.valueOf(userUUID));
        dataBlock.set("type", defaultMineType.getName());
        dataBlock.set("location", LocationUtils.toString(location));
        dataBlock.set("spawnLocation", LocationUtils.toString(mine.getSpawnLocation()));
        dataBlock.set("npcLocation", LocationUtils.toString(mine.getNpcLocation()));
        dataBlock.set("corner1", LocationUtils.toString(mine.getCorner1()));
        dataBlock.set("corner2", LocationUtils.toString(mine.getCorner2()));
        dataBlock.set("structure", mine.getStructure());

//        dataBlock.set("name", defaultMineData.getName());
//        dataBlock.set("corner1", mine.getCorner1());
//        dataBlock.set("corner2", mine.getCorner2());

        blockDataManager.save();
        DataBlock mineDataBlock = blockDataManager.getExisting(block);
        if (mineDataBlock != null) {
            String owner = mineDataBlock.getString("owner");
            String type = mineDataBlock.getString("type");
            String locationString = mineDataBlock.getString("location");
            String spawnLocationString = mineDataBlock.getString("spawnLocation");
            String npcLocationString = mineDataBlock.getString("npcLocation");
            String corner1String = mineDataBlock.getString("corner1");
            String corner2String = mineDataBlock.getString("corner2");
//            String structureString = mineDataBlock.getString("structure");

            Bukkit.getLogger().info("owner: " + owner);
            Bukkit.getLogger().info("type: " + type);
            Bukkit.getLogger().info("location: " + locationString);
            Bukkit.getLogger().info("spawnLocation: " + spawnLocationString);
            Bukkit.getLogger().info("npcLocation: " + npcLocationString);

            Bukkit.getLogger().info("corner1: " + corner1String);
            Bukkit.getLogger().info("corner2: " + corner2String);
        }

//        String mines = "mines.";
//        String mineType = mine.getMineData().getName();
//        minesConfig.getConfig().set(mines + userUUID + ".type", mineType);
//        minesConfig.getConfig().set(mines + userUUID + ".location", location);
//        minesConfig.getConfig().set(mines + userUUID + ".spawnLocation", mine.getSpawnLocation());
//        minesConfig.getConfig().set(mines + userUUID + ".npcLocation", mine.getNpcLocation());
//        minesConfig.getConfig().set(mines + userUUID + ".corner1", mine.getCuboidRegion().getStart());
//        minesConfig.getConfig().set(mines + userUUID + ".corner2", mine.getCuboidRegion().getEnd());
//        minesConfig.getConfig().set(mines + userUUID + ".mineOwner", mine.getMineOwner().toString());
//        minesConfig.getConfig().set(mines + userUUID + ".weightedRandom", mine.getWeightedRandom().toString());
//        minesConfig.getConfig().set(mines + userUUID + ".isAutoResetting", mine.isAutoResetting());
//        minesConfig.save();

        mine.reset();
//        if (debugMode) {
//            privateMines.getLogger().info("createMine block: " + block);
//            privateMines.getLogger().info("createMine dataBlock: " + dataBlock);
//            privateMines.getLogger().info("createMine dataBlock getData: " + dataBlock.getData());
//        }
        return mine;
    }

    /**
     * @param player   - The target player to be given a mine
     * @param location - The spigot world location where to create the mine
     * @param mineType - The mine data such as the MultiBlockStructure and the Materials
     */

    public Mine createMine(Player player, Location location, MineType mineType) {
        if (mineType == null) {
            privateMines.getLogger().warning("Failed to create mine due to defaultMineData being null");
        } else {
            String userUUID = player.getUniqueId().toString();

            Mine mine = new Mine(privateMines, utils);
            mine.setMineOwner(player.getUniqueId());
            mine.setMineLocation(location);
            mine.setMineData(mineType);
            mine.setWeightedRandom(mineType.getWeightedRandom());
            mine.build();

            Location corner1 = utils.getRelative(mine.getStructure(), mineType.getCorner1());
            Location corner2 = utils.getRelative(mine.getStructure(), mineType.getCorner2());

            Location spawnLocation = utils.getRelative(mine.getStructure(), mineType.getSpawnLocation());
            Location npcLocation = utils.getRelative(mine.getStructure(), mineType.getNpcLocation());

            mine.setCorner1(corner1);
            mine.setCorner2(corner2);
            mine.setSpawnLocation(spawnLocation);
            mine.setNpcLocation(npcLocation);

            mineStorage.addMine(player.getUniqueId(), mine);
            Block block = location.getBlock();
            DataBlock dataBlock = blockDataManager.getDataBlock(block);
            dataBlock.set("owner", String.valueOf(userUUID));
            dataBlock.set("type", defaultMineType.getName());
            dataBlock.set("location", LocationUtils.toString(location));
            dataBlock.set("spawnLocation", LocationUtils.toString(mine.getSpawnLocation()));
            dataBlock.set("npcLocation", LocationUtils.toString(mine.getNpcLocation()));
            dataBlock.set("corner1", LocationUtils.toString(mine.getCorner1()));
            dataBlock.set("corner2", LocationUtils.toString(mine.getCorner2()));
            dataBlock.set("structure", mine.getStructure());
            blockDataManager.save();
            mine.reset();
            if (debugMode) {
                privateMines.getLogger().info("createMine block: " + block);
                privateMines.getLogger().info("createMine dataBlock: " + dataBlock);
                privateMines.getLogger().info("createMine dataBlock getData: " + dataBlock.getData());
            }
            return mine;
        }
        return null;
    }
}


