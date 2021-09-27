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
import me.untouchedodin0.privatemines.world.utils.MineLoopUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import redempt.redlib.multiblock.MultiBlockStructure;

import java.util.HashMap;
import java.util.Map;

public class MineData {

    PrivateMines privateMines;

    public MineData(PrivateMines privateMines) {
        this.privateMines = privateMines;
    }

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
    private int[] spawnLocation;
    private int[] npcLocation;
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

    public void setupRelativeLocations() {
        MineLoopUtil mineLoopUtil = new MineLoopUtil();
        Material spawnMaterial = Material.valueOf(privateMines.getSpawnMaterial());
        Material cornerMaterial = Material.valueOf(privateMines.getCornerMaterial());
        Material sellNpcMaterial = Material.valueOf(privateMines.getSellNpcMaterial());

        privateMines.getLogger().info("mine data setupRelativeLocations: spawnMaterial: " +
                spawnMaterial);

        privateMines.getLogger().info("mine data setupRelativeLocations: cornerMaterial: " +
                cornerMaterial);

        privateMines.getLogger().info("mine data setupRelativeLocations: sellNpcMaterial: " +
                sellNpcMaterial);

        this.spawnLocation = mineLoopUtil.findSpawnLocation(multiBlockStructure, spawnMaterial);
        this.npcLocation = mineLoopUtil.findNpcLocation(multiBlockStructure, sellNpcMaterial);
        this.cornerLocations = mineLoopUtil.findCornerLocations(multiBlockStructure, cornerMaterial);
    }

    public int[] getSpawnLocation() {
        return spawnLocation;
    }

    public int[] getNpcLocation() { return npcLocation; }

    public int[][] getCornerLocations() {
        return cornerLocations;
    }
}
