package me.untouchedodin0.plugin.storage;


import me.untouchedodin0.privatemines.we_6.worldedit.RelativePointsWE6;
import me.untouchedodin0.privatemines.we_7.worldedit.BlockPoints;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TransformationStorageWE6 {

    public Map<File, RelativePointsWE6> relativePointsWE6 = new HashMap<>();
    public Map<File, BlockPoints> relativePointsWE7 = new HashMap<>();

    public void addSchematicWE6(File file, RelativePointsWE6 relativePoints) {
        if (file != null && relativePoints != null) {
            relativePointsWE6.put(file, relativePoints);
        }
    }

    public void addSchematicWE7(File file, BlockPoints relativePoints) {
        if (file != null && relativePoints != null) {
            relativePointsWE7.put(file, relativePoints);
        }
    }
}
