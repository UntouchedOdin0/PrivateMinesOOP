/*
MIT License

Copyright (c) 2021 - 2022 Kyle Hicks

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

package me.untouchedodin0.plugin.world.utils;

import org.bukkit.Material;
import redempt.redlib.multiblock.MultiBlockStructure;
import redempt.redlib.multiblock.Structure;

public class MineLoopUtil {

    /**
     * Find the corner locations of a mine in a MultiBlockStructure
     * All credits go to Redempt for helping fix this up!
     *
     * @param multiBlockStructure The MultiBlockStructure to search through
     * @param cornerMaterial      The material to look for in the MultiBlockStructure
     * @return The relative corner locations of a structure
     */
    private int[][] findCornerLocations(MultiBlockStructure multiBlockStructure, Structure structure, Material cornerMaterial) {
        int[] structureDimensions = multiBlockStructure == null
                ? structure.getType().getDimensions()
                : multiBlockStructure.getDimensions();
        int dimensionsX = structureDimensions[0];
        int dimensionsY = structureDimensions[1];
        int dimensionsZ = structureDimensions[2];

        /*
            Creating an array of 2 arrays to hold locations
            Thanks to Redempt for the following example.

            Example: [[1, 2, 3], [4, 5, 6]]
         */

        int[][] locations = new int[2][];
        int corners = 0;

        for (int x = 0; x < dimensionsX; x++) {
            for (int y = 0; y < dimensionsY; y++) {
                for (int z = 0; z < dimensionsZ; z++) {
                    if ((multiBlockStructure == null
                            ? structure.getRelative(x, y, z).getBlock().getType()
                            : multiBlockStructure.getType(x, y, z)) != cornerMaterial) {
                        continue;
                    }
                    locations[corners] = new int[]{x, y, z};
                    corners++;
                    if (corners >= 2) break;
                }
            }
        }
        return locations;
    }

    /**
     * Find the corner locations of a mine in a MultiBlockStructure
     *
     * @param multiBlockStructure The MultiBlockStructure to search through
     * @param cornerMaterial      The material to look for in the MultiBlockStructure
     * @return The relative corner locations of a structure
     */
    public int[][] findCornerLocations(MultiBlockStructure multiBlockStructure, Material cornerMaterial) {
        return findCornerLocations(multiBlockStructure, null, cornerMaterial);
    }

    public int[][] findCornerLocations(Structure structure, Material cornerMaterial) {
        return findCornerLocations(null, structure, cornerMaterial);
    }

    private int[] findLocation(MultiBlockStructure multiBlockStructure, Structure structure, Material material) {

        int[] structureDimensions = multiBlockStructure == null
                ? structure.getType().getDimensions()
                : multiBlockStructure.getDimensions();
        int dimensionsX = structureDimensions[0];
        int dimensionsY = structureDimensions[1];
        int dimensionsZ = structureDimensions[2];

         /*
            Creating an array of 1 values to hold the location
            Thanks to Redempt for the following example.

            Example: [1, 2, 3]
         */

        int[] location = new int[1];

        for (int x = 0; x < dimensionsX; x++) {
            for (int y = 0; y < dimensionsY; y++) {
                for (int z = 0; z < dimensionsZ; z++) {
                    if ((multiBlockStructure == null
                            ? structure.getRelative(x, y, z).getBlock().getType()
                            : multiBlockStructure.getType(x, y, z)) != material) {
                        continue;
                    }
                    location = new int[]{x, y, z};
                }
            }
        }
        return location;
    }

    /**
     * Find the Spawn Point location for the mine
     */
    public int[] findSpawnLocation(MultiBlockStructure multiBlockStructure, Material spawnMaterial) {
        return findLocation(multiBlockStructure, null, spawnMaterial);
    }

    public int[] findSpawnLocation(Structure structure, Material spawnMaterial) {
        return findLocation(null, structure, spawnMaterial);
    }

    /**
     * Find the Spawn Point location for the mine
     */
    public int[] findNpcLocation(MultiBlockStructure multiBlockStructure, Material npcMaterial) {
        return findLocation(multiBlockStructure, null, npcMaterial);
    }

    public int[] findNpcLocation(Structure structure, Material npcMaterial) {
        return findLocation(null, structure, npcMaterial);
    }
}
