package me.untouchedodin0.plugin.storage;

import me.untouchedodin0.privatemines.we_7.worldedit.RelativePointsWE7;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TransformationStorageWE7 {

    public Map<File, RelativePointsWE7> relativePointsMap = new HashMap<>();

    public void addSchematic(File file, RelativePointsWE7 relativePoints) {
        if (file != null && relativePoints != null) {
            relativePointsMap.put(file, relativePoints);
        }
    }
}
