package me.untouchedodin0.plugin.config;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import redempt.redlib.config.annotations.Comment;
import redempt.redlib.config.annotations.ConfigMappable;
import redempt.redlib.config.annotations.ConfigName;
import redempt.redlib.config.annotations.ConfigPostInit;

@ConfigMappable
public class Config {

    @Comment("The template material for the spawn point")
    public static Material spawnPoint = Material.SPONGE;
    @Comment("The template material for the corners (Needs 2 in a mine to create the cuboid)")
    public static Material mineCorner = Material.POWERED_RAIL;
//    @Comment("The template material for the sell npc")
//    public static Material sellNpc = Material.WOOL;
    @Comment("During the /pmine expand process this block will")
    @Comment("looked for to tell the plugin to upgrade to")
    @Comment("the next mine type")
    public static Material upgradeMaterial = Material.OBSIDIAN;
    @Comment("The title name for the /pmine menu")
    public static String mainMenuTitle = "Private Mines";
    @Comment("The title for the menu when you click Your Mine in /pmine")
    public static String yourMine = "Your Mine";
    @Comment("Toggle for debug mode. (Causes more spam).")
    public static boolean debugMode = false;
    @Comment("Check for any spigot updates")
    public static boolean notifyForUpdates = true;
    @Comment("The distance between the private mines")
    public static int mineDistance = 150;
    @Comment("The mine percentage for when it should reset")
    public static double resetPercentage = 50;
    @Comment("Should we use FastAsyncWorldEdit to reset the mines? (Should make it faster)")
    public static boolean useFAWE = true;

    @ConfigName("autoUpgrade.enabled")
    public static boolean autoUpgradeEnabled;
    @ConfigName("autoUpgrade.everyXthExpansion")
    public static int everyXthExpansion;
    @ConfigName("autoUpgrade.startingSize")
    public static int startingSize;

    public static Material getSpawnPoint() {
        return spawnPoint;
    }

    public static Material getMineCorner() {
        return mineCorner;
    }

    public static Material getUpgradeMaterial() {
        return upgradeMaterial;
    }

    public static String getMainMenuTitle() {
        return mainMenuTitle;
    }

    public static String getYourMineTitle() {
        return yourMine;
    }

    @ConfigPostInit
    private void postInit() {
        Bukkit.getLogger().info("config post init working");
    }
}
