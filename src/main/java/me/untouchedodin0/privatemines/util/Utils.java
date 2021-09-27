package me.untouchedodin0.privatemines.util;

import org.bukkit.Location;
import redempt.redlib.multiblock.Structure;

public class Utils {

    public Location getRelative(Structure structure, int[] relative) {
        return structure
                .getRelative(relative[0], relative[1], relative[2])
                .getBlock()
                .getLocation();
    }
}
