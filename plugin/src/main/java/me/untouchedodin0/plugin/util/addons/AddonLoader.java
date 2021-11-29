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
                    privateMines.getLogger().info("Loading addon... " + plugin.getName());
                } else {
                    privateMines.getLogger().warning("Failed to load file: " + file);
                }
            } catch (InvalidPluginException | InvalidDescriptionException e) {
                e.printStackTrace();
            }
        }
    }

    public void unload(String name) {
        if (pluginManager != null) {
            boolean isEnabled = pluginManager.isPluginEnabled(name);
            if (!isEnabled) {
                privateMines.getLogger().warning("The plugin " + name + " wasn't enabled!");
            } else {
                Plugin plugin = pluginManager.getPlugin(name);
                privateMines.getLogger().info("Unloading plugin " + name);
                if (plugin != null) {
                    pluginManager.disablePlugin(plugin);
                }
            }
        }
    }
}
