package me.untouchedodin0.plugin.storage.points;

import me.untouchedodin0.privatemines.we_6.worldedit.BlockPoints6;
import me.untouchedodin0.privatemines.we_7.worldedit.BlockPoints7;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BlockPointsStorage {

    private final Map<File, BlockPoints6> blockPoints6 = new HashMap<>();
    private final Map<File, BlockPoints7> blockPoints7 = new HashMap<>();

    public Map<File, BlockPoints6> getBlockPoints6() {
        return blockPoints6;
    }

    public Map<File, BlockPoints7> getBlockPoints7() {
        return blockPoints7;
    }
}
