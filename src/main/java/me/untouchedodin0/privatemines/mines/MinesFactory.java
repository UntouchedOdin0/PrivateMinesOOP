package me.untouchedodin0.privatemines.mines;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import redempt.redlib.multiblock.MultiBlockStructure;

import java.util.UUID;

public class MinesFactory {

    public void build(UUID owner, Location location, MineData mineData) {
        MultiBlockStructure multiBlockStructure = mineData.getMultiBlockStructure();

        if (owner == null) {
            Bukkit.getLogger().info("Failed to build mine due to the owner being null!");
            return;
        }
        if (location == null) {
            Bukkit.getLogger().info("Failed to build mine due to the location being null!");
            return;
        }
        if (multiBlockStructure == null) {
            Bukkit.getLogger().info("Failed to build mine due to the MultiBlockStructure being null!");
            return;
        }
        multiBlockStructure.build(location);
    }
}
