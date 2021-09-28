package me.untouchedodin0.privatemines.util;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mines.Mine;
import me.untouchedodin0.privatemines.mines.MineData;
import org.bukkit.Location;
import redempt.redlib.multiblock.Structure;

public class Utils {

    private final PrivateMines privateMines;

    public Utils(PrivateMines privateMines) {
        this.privateMines = privateMines;
    }

    public Location getRelative(Structure structure, int[] relative) {
        return structure
                .getRelative(relative[0], relative[1], relative[2])
                .getBlock()
                .getLocation();
    }


    public MineData getNextMineData(Mine mine) {
        MineData mineData = mine.getMineData();
        boolean isAtLastMineData = privateMines.isAtLastMineData(mineData);
        if (isAtLastMineData) {
            privateMines.getLogger().info("Mine is already maxed out!");
            return mineData;
        }
        privateMines.getLogger().info("Current mine data Name: " + mineData.getName());
        MineData upgradeMineData = privateMines.getNextMineData(mineData);
        privateMines.getLogger().info("Next mine data name: " + upgradeMineData.getName());
        return upgradeMineData;
    }
}
