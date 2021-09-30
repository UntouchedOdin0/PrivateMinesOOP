package me.untouchedodin0.privatemines.util;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mines.Mine;
import me.untouchedodin0.privatemines.mines.MineData;
import org.bukkit.Location;
import org.bukkit.block.Block;
import redempt.redlib.multiblock.Structure;
import redempt.redlib.region.CuboidRegion;

public class Utils {

    private final PrivateMines privateMines;
    private final boolean debugMode;

    public Utils(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.debugMode = privateMines.isDebugMode();
    }

    public Location getRelative(Structure structure, int[] relative) {
        return structure
                .getRelative(relative[0], relative[1], relative[2])
                .getBlock()
                .getLocation();
    }


    public MineData getNextMineData(Mine mine) {
        MineData mineData = mine.getMineData();
        MineData upgradeMineData;
        boolean isAtLastMineData = privateMines.isAtLastMineData(mineData);
        if (isAtLastMineData) {
            privateMines.getLogger().info("Mine is already maxed out!");
            return mineData;
        }
        if (debugMode) {
            privateMines.getLogger().info("Current mine data Name: " + mineData.getName());
            upgradeMineData = privateMines.getNextMineData(mineData);
            privateMines.getLogger().info("Next mine data name: " + upgradeMineData.getName());
        }
        upgradeMineData = privateMines.getNextMineData(mineData);
        return upgradeMineData;
    }

    public double getPercentageLeft(Mine mine) {
        CuboidRegion cuboidRegion = mine.getCuboidRegion();
        int totalBlocks = cuboidRegion.getBlockVolume();
        long airBlocks = cuboidRegion.stream().filter(Block::isEmpty).count();
        return (double) airBlocks * 100 / totalBlocks;
    }
}
