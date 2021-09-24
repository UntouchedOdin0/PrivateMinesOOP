package me.untouchedodin0.privatemines.world.utils;

import org.bukkit.Material;
import redempt.redlib.multiblock.MultiBlockStructure;

public class MineLoopUtil {

    // Find the corner locations of a mine in a MultiBlockStructure

    /**
     *
     * @param multiBlockStructure   The MultiBlockStructure to search through
     * @param cornerMaterial        The material to look for in the MultiBlockStructure
     * @return                      The relative corner locations of a structure
     */

    public int[][] findCornerLocations(MultiBlockStructure multiBlockStructure, Material cornerMaterial) {

        int[] structureDimensions = multiBlockStructure.getDimensions();
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
                    if (multiBlockStructure.getType(x, y, z) != cornerMaterial) {
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
}
