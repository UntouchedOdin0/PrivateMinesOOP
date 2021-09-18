package me.untouchedodin0.privatemines.config;

import org.bukkit.Material;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.io.File;
import java.util.Map;

@ConfigMappable
public class MineConfig {

    @ConfigValue
    private File file; // maybe needs to be String

    @ConfigValue
    private int priority; // reads the priority from the section

    @ConfigValue
    private Map<Material, Double> materials = ConfigManager.map(Material.class, Double.class); // reads the materials from the section

    // A getter for getting the file name from the secion

    public File getFile() {
        return file;
    }

    // A getter for getting the priority from the secion

    public int getPriority() {
        return priority;
    }

    // A getter for getting the materials from the secion

    public Map<Material, Double> getMaterials() {
        return materials;
    }
}
