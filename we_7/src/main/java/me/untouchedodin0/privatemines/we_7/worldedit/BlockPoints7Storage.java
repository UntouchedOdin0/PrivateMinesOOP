package me.untouchedodin0.privatemines.we_7.worldedit;

import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public class BlockPoints7Storage {

    public Map<File, BlockPoints7> blockPoints7Map = new HashMap<>();

    public void addBlockPoints(File file, BlockPoints7 blockPoints7) {
        blockPoints7Map.putIfAbsent(file, blockPoints7);
    }
}
