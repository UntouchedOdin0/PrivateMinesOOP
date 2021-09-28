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
import me.untouchedodin0.privatemines.mines.MineData;
import me.untouchedodin0.privatemines.storage.MineStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.blockdata.DataBlock;

public class MineFactory {

    private final boolean debugMode;
    PrivateMines privateMines;
    MineStorage mineStorage;
    MineData defaultMineData;
    BlockDataManager blockDataManager;

    public MineFactory(PrivateMines privateMines, BlockDataManager blockDataManager) {
        this.privateMines = privateMines;
        this.mineStorage = privateMines.getMineStorage();
        this.defaultMineData = privateMines.getDefaultMineData();
        this.blockDataManager = blockDataManager;
        this.debugMode = privateMines.isDebugMode();
    }

    public Mine createMine(Player player, Location location) {
        Mine mine = new Mine();
        mine.setMineOwner(player.getUniqueId());
        mine.setMineLocation(location);
        mine.setMineData(defaultMineData);
        mine.setWeightedRandom(defaultMineData.getWeightedRandom());
        mine.build();
        mineStorage.addMine(player.getUniqueId(), mine);
        Block block = location.getBlock();
        DataBlock dataBlock = blockDataManager.getDataBlock(block);
        dataBlock.set("mine", mine);
        mine.reset();
        if (debugMode) {
            Bukkit.getLogger().info("createMine block: " + block);
            Bukkit.getLogger().info("createMine dataBlock: " + dataBlock);
            Bukkit.getLogger().info("createMine dataBlock getData: " + dataBlock.getData());
        }
        return mine;
    }

    /**
     * @param player   - The target player to be given a mine
     * @param location - The spigot world location where to create the mine
     * @param mineData - The mine data such as the MultiBlockStructure and the Materials
     */

    public void createMine(Player player, Location location, MineData mineData) {
        Mine mine = new Mine();
        mine.setMineOwner(player.getUniqueId());
        mine.setMineLocation(location);
        mine.setMineData(mineData);
        mine.setWeightedRandom(defaultMineData.getWeightedRandom());
        mine.build();
        mineStorage.addMine(player.getUniqueId(), mine);
        Block block = location.getBlock();
        DataBlock dataBlock = blockDataManager.getDataBlock(block);
        dataBlock.set("mine", mine);
        mine.reset();
        if (debugMode) {
            Bukkit.getLogger().info("createMine block: " + block);
            Bukkit.getLogger().info("createMine dataBlock: " + dataBlock);
            Bukkit.getLogger().info("createMine dataBlock getData: " + dataBlock.getData());
        }
    }

    public void upgradeMine(Player player, MineData mineData) {
        if (privateMines.isAtLastMineData(mineData)) {
            Bukkit.getLogger().info("Can't upgrade anymore, at highest!");
            return;
        } else {
            MineData nextMineData =
                    privateMines.getNextMineData(mineData.getName());
            if (mineStorage.hasMine(player.getUniqueId())) {
                Mine mine = mineStorage.getMine(player.getUniqueId());
                mine.setMineData(nextMineData);
                createMine(player, mine.getMineLocation(), nextMineData);
            }
        }
    }
}

//    public void upgradeMine(Player player, MineData mineData) {
//        if (privateMines.isAtLastMineData(mineData)) return;
//            MineData nextMineData = privateMines.getNextMineData(mineData.getName());
//            if (mineStorage.hasMine(player.getUniqueId())) {
//                Mine mine = mineStorage.getMine(player.getUniqueId());
//                mine.setMineData(nextMineData);
//                createMine(player, mine.getMineLocation(), nextMineData);
//                mine.teleportPlayer(player);
//            }
//        }
//    }

