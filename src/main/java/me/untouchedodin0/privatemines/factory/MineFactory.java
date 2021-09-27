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

import java.util.UUID;

public class MineFactory {

    PrivateMines privateMines;
    MineStorage mineStorage;
    MineData defaultMineData;
    BlockDataManager blockDataManager;

    public MineFactory(PrivateMines privateMines, BlockDataManager blockDataManager) {
        this.privateMines = privateMines;
        this.mineStorage = privateMines.getMineStorage();
        this.defaultMineData = privateMines.getDefaultMineData();
        this.blockDataManager = blockDataManager;
    }

    public Mine createMine(Player player, Location location) {
        Mine mine = new Mine();
        mine.setMineOwner(player.getUniqueId());
        mine.setMineLocation(location);
        mine.setMineData(defaultMineData);
        mine.build();
        mineStorage.addMine(player.getUniqueId(), mine);
        Block block = location.getBlock();
        DataBlock dataBlock = blockDataManager.getDataBlock(block);
        dataBlock.set("mine", mine);

        Bukkit.getLogger().info("createMine block: " + block);
        Bukkit.getLogger().info("createMine dataBlock: " + dataBlock);
        Bukkit.getLogger().info("createMine dataBlock getData: " + dataBlock.getData());
        return mine;
    }

    /**
     * @param player   - The target player to be given a mine
     * @param location - The spigot world location where to create the mine
     * @param mineData - The mine data such as the MultiBlockStructure and the Materials
     */

    public Mine createMine(Player player, Location location, MineData mineData) {
        Mine mine = new Mine();
        mine.setMineOwner(player.getUniqueId());
        mine.setMineLocation(location);
        mine.setMineData(mineData);
        mine.build();
        mineStorage.addMine(player.getUniqueId(), mine);
        return mine;
    }

    public void deleteMine(Player player) {
        UUID uuid = player.getUniqueId();
        Mine mine = mineStorage.getMine(uuid);
        mineStorage.removeMine(uuid);
        mine.delete();
    }

    public void upgradeMine(Player player, MineData mineData) {
        MineData nextMineData = privateMines.getNextMineData(mineData.getName());
        Mine mine = mineStorage.getMine(player.getUniqueId());
        mine.setMineData(nextMineData);
    }
}
