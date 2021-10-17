package me.untouchedodin0.privatemines.config;

import org.bukkit.Material;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigPath;
import redempt.redlib.configmanager.annotations.ConfigValue;
import redempt.redlib.misc.WeightedRandom;

import java.util.UUID;

@ConfigMappable
public class StorageConfig {

    @ConfigPath
    private final String owner = "owner";

    @ConfigValue
    private String type;

    @ConfigValue
    private String location;

    @ConfigValue
    private String spawnLocation;

    @ConfigValue
    private String npcLocation;

    @ConfigValue
    private String corner1;

    @ConfigValue
    private String corner2;

    @ConfigValue
    private UUID mineOwner;

    @ConfigValue
    private WeightedRandom<Material> weightedRandom;

    @ConfigValue
    private boolean isAutoResetting;

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public String getSpawnLocation() {
        return spawnLocation;
    }

    public String getNpcLocation() {
        return npcLocation;
    }

    public String getCorner1() {
        return corner1;
    }

    public String getCorner2() {
        return corner2;
    }

    public UUID getMineOwner() {
        return mineOwner;
    }

    public WeightedRandom<Material> getWeightedRandom() {
        return weightedRandom;
    }

    public boolean isAutoResetting() {
        return isAutoResetting;
    }
}
