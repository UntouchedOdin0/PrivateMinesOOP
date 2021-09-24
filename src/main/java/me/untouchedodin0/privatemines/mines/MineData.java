package me.untouchedodin0.privatemines.mines;

import me.untouchedodin0.privatemines.world.utils.MineLoopUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import redempt.redlib.multiblock.MultiBlockStructure;

import java.util.HashMap;
import java.util.Map;

public class MineData {

    /*
        name: Name of the mine type
        mineTier: The tier of the mine (where it goes in the upgrade process)
        resetTime: How often the mine should reset in minutes.
        materials: A list of materials and their percentages of which goes in the mine
     */


    private String name;
    private int mineTier = 1;
    private int resetTime = 1;
    private Map<Material, Double> materials = new HashMap<>();
    private MultiBlockStructure multiBlockStructure;
    private int[][] cornerLocations;

    /**
     *
     * @param name - The name to set of the mine type
     */

    public void setName(String name) { this.name = name; }

    /**
     *
     * @return A string value of the name of the mine
     */

    public String getName() { return name; }

    /**
     *
     * @param mineTier - The new mine tier to be set
     */

    public void setMineTier(int mineTier) { this.mineTier = mineTier; }

    /**
     *
     * @return The tier of the mine
     */

    public int getMineTier() { return mineTier; }

    /**
     *
     * @param resetTime - The new reset delay for the mine
     */

    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }

    /**
     *
     * @return The reset time of the mine
     */

    public int getResetTime() {
        return resetTime;
    }

    /**
     *
     * @param mineBlocks - The map of Materials and their percentages to be set in the private mine
     */

    public void setMaterials(Map<Material, Double> mineBlocks) { this.materials = mineBlocks; }

    /**
     *
     * @return A map of Materials and their percentages
     */

    public Map<Material, Double> getMaterials() { return materials; }

    /**
     *
     * @param multiBlockStructure - The new MultiBlockStructure to be set for the MineData
     */

    public void setMultiBlockStructure(MultiBlockStructure multiBlockStructure) {
        this.multiBlockStructure = multiBlockStructure;
    }

    /**
     *
     * @return The MultiBlockStructure of the MineData
     */

    public MultiBlockStructure getMultiBlockStructure() {
        return multiBlockStructure;
    }

    public void setupRelativeLocations(Material cornerMaterial) {
        MineLoopUtil mineLoopUtil = new MineLoopUtil();
        Bukkit.getLogger().info("setupRelativeLocations mineLoopUtil: " + mineLoopUtil);
        this.cornerLocations = mineLoopUtil.findCornerLocations(multiBlockStructure, cornerMaterial);
    }

    public int[][] getCornerLocations() {
        return cornerLocations;
    }
}
