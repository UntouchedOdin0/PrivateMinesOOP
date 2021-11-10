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

package me.untouchedodin0.plugin.mines;

import com.cryptomorin.xseries.XMaterial;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.world.utils.MineLoopUtil;
import org.bukkit.Material;
import redempt.redlib.misc.WeightedRandom;
import redempt.redlib.multiblock.MultiBlockStructure;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MineType {

    private final int[] spawnLocation;
    private final int[] npcLocation;

    /*
        name: Name of the mine type
        mineTier: The tier of the mine (where it goes in the upgrade process)
        resetTime: How often the mine should reset in minutes.
        materials: A list of materials and their percentages of which goes in the mine
     */

    private final int[][] cornerLocations;
    PrivateMines privateMines;
    MineLoopUtil mineLoopUtil;
    private String name;
    private int mineTier = 1;
    private int resetTime = 1;
    private double resetPercentage;
    private Map<Material, Double> materials = new HashMap<>();
    private WeightedRandom<Material> weightedRandom = new WeightedRandom<>();
    private MultiBlockStructure multiBlockStructure;
    private File file;
    private List<String> allowFlags;
    private List<String> denyFlags;

    private Material spawnMaterial;
    private Material sellNpcMaterial;
    private Material cornerMaterial;


    public MineType(PrivateMines privateMines,
                    MultiBlockStructure multiBlockStructure) {
        this.privateMines = privateMines;
        this.mineLoopUtil = new MineLoopUtil();
        this.multiBlockStructure = multiBlockStructure;

        if (XMaterial.matchXMaterial(privateMines.getSpawnMaterial()).isPresent()) {
            spawnMaterial = XMaterial.matchXMaterial(privateMines.getSpawnMaterial()).get().parseMaterial();
        }
        if (XMaterial.matchXMaterial(privateMines.getSellNpcMaterial()).isPresent()) {
            sellNpcMaterial = XMaterial.matchXMaterial(privateMines.getSellNpcMaterial()).get().parseMaterial();
        }
        if (XMaterial.matchXMaterial(privateMines.getCornerMaterial()).isPresent()) {
            cornerMaterial = XMaterial.matchXMaterial(privateMines.getCornerMaterial()).get().parseMaterial();
        }

        this.spawnLocation = mineLoopUtil.findSpawnLocation(multiBlockStructure, spawnMaterial);
        this.npcLocation = mineLoopUtil.findNpcLocation(multiBlockStructure, sellNpcMaterial);
        this.cornerLocations = mineLoopUtil.findCornerLocations(multiBlockStructure, cornerMaterial);
    }


    /**
     * @return A string value of the name of the mine
     */

    public String getName() {
        return name;
    }

    /**
     * @param name - The name to set of the mine type
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The tier of the mine
     */


    public int getMineTier() {
        return mineTier;
    }

    /**
     * @param mineTier - The new mine tier to be set
     */

    public void setMineTier(int mineTier) {
        this.mineTier = mineTier;
    }

    /**
     * @return The reset time of the mine
     */

    public int getResetTime() {
        return resetTime;
    }

    /**
     * @param resetTime - The new reset delay for the mine
     */

    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }

    /**
     * @return The reset percentage for the mine
     */

    public double getResetPercentage() {
        return resetPercentage;
    }

    /**
     * @param resetPercentage - The new reset percentage for the mine
     */

    public void setResetPercentage(double resetPercentage) {
        this.resetPercentage = resetPercentage;
    }

    /**
     * @return A map of Materials and their percentages
     */

    public Map<Material, Double> getMaterials() {
        return materials;
    }

    /**
     * @param mineBlocks - The map of Materials and their percentages to be set in the private mine
     */

    public void setMaterials(Map<Material, Double> mineBlocks) {
        this.materials = mineBlocks;
    }

    public WeightedRandom<Material> getWeightedRandom() {
        return weightedRandom;
    }

    public void setWeightedRandom(WeightedRandom<Material> weightedRandom) {
        this.weightedRandom = weightedRandom;
    }

    /**
     * @return The MultiBlockStructure of the MineData
     */

    public MultiBlockStructure getMultiBlockStructure() {
        return multiBlockStructure;
    }

    /**
     * @param multiBlockStructure - The new MultiBlockStructure to be set for the MineData
     *                            This method will likely go idk yet
     */

    @Deprecated
    public void setMultiBlockStructure(MultiBlockStructure multiBlockStructure) {
        this.multiBlockStructure = multiBlockStructure;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int[] getSpawnLocation() {
        return spawnLocation;
    }

    public int[] getNpcLocation() {
        return npcLocation;
    }

    public int[][] getCornerLocations() {
        return cornerLocations;
    }

    public int[] getCorner1() {
        return getCornerLocations()[0];
    }

    public int[] getCorner2() {
        return getCornerLocations()[1];
    }

    public List<String> getAllowFlags() {
        return allowFlags;
    }

    public void setAllowFlags(List<String> allowFlags) {
        this.allowFlags = allowFlags;
    }

    public List<String> getDenyFlags() {
        return denyFlags;
    }

    public void setDenyFlags(List<String> denyFlags) {
        this.denyFlags = denyFlags;
    }
}
