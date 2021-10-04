package me.untouchedodin0.privatemines.config;

import org.bukkit.Location;
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
    private Location location;

    @ConfigValue
    private Location spawnLocation;

    @ConfigValue
    private Location npcLocation;

    @ConfigValue
    private Location corner1;

    @ConfigValue
    private Location corner2;

    @ConfigValue
    private UUID mineOwner;

    @ConfigValue
    private WeightedRandom<Material> weightedRandom;

    @ConfigValue
    private boolean isAutoResetting;

    public String getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public Location getNpcLocation() {
        return npcLocation;
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
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
