package me.untouchedodin0.plugin.mines;

import me.untouchedodin0.plugin.PrivateMines;

import java.io.File;

public class WorldEditMineType {

    PrivateMines privateMines;
    private String name;
    private final File schematicFile;
    private int mineTier = 1;
    private int resetTime = 1;

    public WorldEditMineType(PrivateMines privateMines, File schematic) {
        this.privateMines = privateMines;
        this.schematicFile = schematic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public File getSchematicFile() {
        return schematicFile;
    }

    public void setMineTier(int tier) {
        this.mineTier = tier;
    }

    public int getMineTier() {
        return mineTier;
    }

    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }

    public int getResetTime() {
        return resetTime;
    }
}
