package me.untouchedodin0.plugin.config;

import org.bukkit.Material;
import redempt.redlib.config.annotations.ConfigMappable;
import redempt.redlib.config.annotations.ConfigName;

@ConfigMappable
public class Config {

    public static Material spawnPoint = Material.SPONGE;
    public static Material mineCorner = Material.POWERED_RAIL;
    public static Material sellNpc = Material.WOOL;
    public static Material upgradeMaterial = Material.OBSIDIAN;
    public static String mainMenuTitle = "Private Mines";

    public static boolean debugMode = false;
    public static boolean notifyForUpdates = true;
    public static int mineDistance = 150;
    public static double resetPercentage = 50;

    @ConfigName("autoUpgrade.enabled")
    public static boolean autoUpgradeEnabled;
    @ConfigName("autoUpgrade.everyXthExpansion")
    public static int everyXthExpansion;
    @ConfigName("autoUpgrade.startingSize")
    public static int startingSize;
}
