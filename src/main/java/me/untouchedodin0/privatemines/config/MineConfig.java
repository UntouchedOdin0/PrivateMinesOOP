package me.untouchedodin0.privatemines.config;

import org.bukkit.Material;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
@ConfigMappable
public class MineConfig {

    @ConfigValue
    private String file; // reads the file

    @ConfigValue
    private int priority; // reads the priority from the section

    @ConfigValue
    private int resetTime; // reads the reset time from the section

    @ConfigValue
    private Map<Material, Double> materials =
            ConfigManager.map(Material.class, Double.class); // reads the materials from the section

    // A getter for getting the file name from the section

    public String getFile() {
        return file;
    }

    // A getter for getting the priority from the section

    public int getPriority() {
        return priority;
    }

    // A getter for getting the reset time from the section

    public int getResetTime() { return resetTime; }

    // A getter for getting the materials from the secion

    public Map<Material, Double> getMaterials() {
        return materials;
    }
}
