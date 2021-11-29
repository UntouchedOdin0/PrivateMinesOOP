package me.untouchedodin0.plugin.util.addons;

import me.untouchedodin0.plugin.PrivateMines;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;

public class AddonLoader {

    PrivateMines privateMines;
    PluginManager pluginManager;

    public AddonLoader(PrivateMines privateMines,
                       PluginManager pluginManager) {
        this.privateMines = privateMines;
        this.pluginManager = pluginManager;
    }

    public void load(File file) {
        if (pluginManager != null) {
            try {
                Plugin plugin = pluginManager.loadPlugin(file);
                if (plugin != null) {
                    privateMines.getLogger().info("Loading plugin... " + plugin.getName());
                } else {
                    privateMines.getLogger().warning("Failed to load file: " + file);
                }
            } catch (InvalidPluginException | InvalidDescriptionException e) {
                e.printStackTrace();
            }
        }
    }
}
