package me.untouchedodin0.plugin.config;

import org.bukkit.Material;
import redempt.redlib.config.annotations.ConfigName;

public class Config {

    public Material spawnPoint = Material.SPONGE;
    public Material mineCorner = Material.POWERED_RAIL;
    public Material sellNpc = Material.WOOL;
    public Material upgradeMaterial = Material.OBSIDIAN;
    public String mainMenuTitle = "Private Mines";

    public boolean debugMode = false;
    public boolean notifyForUpdates = true;
    public int mineDistance = 150;
    public double resetPercentage = 50;

    @ConfigName("autoUpgrade.enabled")
    public boolean autoUpgradeEnabled;
    @ConfigName("autoUpgrade.everyXthExpansion")
    public int everyXthExpansion;
    @ConfigName("autoUpgrade.startingSize")
    public int startingSize;
}
