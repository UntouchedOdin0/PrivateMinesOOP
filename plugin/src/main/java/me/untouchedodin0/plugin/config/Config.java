package me.untouchedodin0.plugin.config;

import org.bukkit.Material;
import redempt.redlib.config.annotations.Comment;
import redempt.redlib.config.annotations.ConfigMappable;
import redempt.redlib.config.annotations.ConfigName;

@ConfigMappable
public class Config {

    public static String stuff = "abc.def";

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

    @Comment("Toggle for debug mode. (Causes more spam).")
    public static boolean debugMode = false;
    @Comment("Check for any spigot updates")
    public static boolean notifyForUpdates = true;
    @Comment("The distance between the private mines")
    public static int mineDistance = 150;
    @Comment("The mine percentage for when it should reset")
    public static double resetPercentage = 50;

    @ConfigName("autoUpgrade.enabled")
    public static boolean autoUpgradeEnabled;
    @ConfigName("autoUpgrade.everyXthExpansion")
    public static int everyXthExpansion;
    @ConfigName("autoUpgrade.startingSize")
    public static int startingSize;
}
