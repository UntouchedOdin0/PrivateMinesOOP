package me.untouchedodin0.privatemines.config;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mines.MineData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigPath;
import redempt.redlib.configmanager.annotations.ConfigPostInit;
import redempt.redlib.configmanager.annotations.ConfigValue;
import redempt.redlib.multiblock.MultiBlockStructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("FieldMayBeFinal")
@ConfigMappable
public class MineConfig {

    private PrivateMines privateMines;
    private Path path;
    private String contents;

    @SuppressWarnings("unused")
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

    /*
        These tasks are fired after all the objects in the config file have been mapped.
        Path: The path of the specified file to be processed
        Contents: The contents of the the file e.g. 14x24x27;bedrock*9072;
     */

    @ConfigPostInit
    private void postInit() throws IOException {
        if (privateMines == null) {
            Bukkit.getLogger().info("private mines was null.");
        }

        this.path = privateMines.getDataFolder().toPath().resolve(file);
        this.contents = Files.lines(path).collect(Collectors.joining());

        Bukkit.getLogger().info("Test postInit!");
        Bukkit.getLogger().info("Name: " + getName());
        Bukkit.getLogger().info("Path: " + getPath());
        Bukkit.getLogger().info("File: " + getFile());
        Bukkit.getLogger().info("Priority: " + getPriority());
        Bukkit.getLogger().info("Reset Time: " + getResetTime());
        Bukkit.getLogger().info("Materials: " + getMaterials());
        Bukkit.getLogger().info("MultiBlockStructure: " + getMultiBlockStructure());
        Bukkit.getLogger().info("Contents: " + getContents());

        this.multiBlockStructure = MultiBlockStructure.create(contents, name, false, true);

        Bukkit.getLogger().info("mbs getName: " + multiBlockStructure.getName());
        MineData mineData = new MineData();
        mineData.setName(getName());
        mineData.setMultiBlockStructure(multiBlockStructure);
        mineData.setMineTier(getPriority());
        mineData.setResetTime(getResetTime());
        mineData.setMaterials(getMaterials());

        Bukkit.getLogger().info("MineConfig MineData " + mineData);
        Bukkit.getLogger().info("MineConfig MineData Name: " + mineData.getName());
        Bukkit.getLogger().info("MineConfig MineData MBS: " + mineData.getMultiBlockStructure());
        Bukkit.getLogger().info("MineConfig MineData Tier: " + mineData.getMineTier());
        Bukkit.getLogger().info("MineConfig MineData Reset time: " + mineData.getResetTime());
        Bukkit.getLogger().info("MineConfig MineData Materials: " + mineData.getMaterials());
    }

    // a getter for the private mines instance
    public PrivateMines getPrivateMines() {
        return privateMines;
    }

    // A getter for the path
    public Path getPath() { return path; }

    // A getter for the contents

    public String getContents() { return contents; }

    // A getter for getting the section name

    public String getName() { return name; }

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
