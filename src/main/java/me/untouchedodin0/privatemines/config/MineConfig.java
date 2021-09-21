package me.untouchedodin0.privatemines.config;

import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigPath;
import redempt.redlib.configmanager.annotations.ConfigPostInit;
import redempt.redlib.configmanager.annotations.ConfigValue;
import redempt.redlib.multiblock.MultiBlockStructure;

import java.nio.file.Path;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
@ConfigMappable
public class MineConfig {

    private PrivateMines privateMines;

    @ConfigPath
    private String name;

    @ConfigValue
    private String file; // reads the file

    @ConfigValue
    private int priority; // reads the priority from the section

    @ConfigValue
    private int resetTime; // reads the reset time from the section

    @ConfigValue
    private Map<Material, Double> materials =
            ConfigManager.map(Material.class, Double.class); // reads the materials from the section

    private MultiBlockStructure multiBlockStructure;

    public MineConfig() {
        this.privateMines = PrivateMines.getPlugin(PrivateMines.class);
    }

    @ConfigPostInit
    private void postInit() {
        if (privateMines == null) {
            Bukkit.getLogger().info("private mines was null.");
        }
        Path path = privateMines.getDataFolder().toPath().resolve(file);

        Bukkit.getLogger().info("Test postInit!");
        Bukkit.getLogger().info("Path: " + path);
        Bukkit.getLogger().info("File: " + file);

        this.multiBlockStructure = MultiBlockStructure.create(name, file, false, true);

        Bukkit.getLogger().info("mbs getName: " + multiBlockStructure.getName());
    }

//    @ConfigPostInit
//    public void postInit() throws IOException {
//        this.privateMines = PrivateMines.getPlugin(PrivateMines.class);
//        Path path = privateMines.getDataFolder().toPath().resolve(file);
//        String contents = Files.lines(path).collect(Collectors.joining());
//        this.multiBlockStructure = MultiBlockStructure
//                .create(contents, file, false, true);
//    }

    // a getter for the private mines instance
    public PrivateMines getPrivateMines() {
        return privateMines;
    }

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

    // A getter for getting the multi block structure

    public MultiBlockStructure getMultiBlockStructure() {
        return multiBlockStructure;
    }
}
