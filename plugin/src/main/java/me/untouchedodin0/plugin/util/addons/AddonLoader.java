package me.untouchedodin0.plugin.util.addons;

import me.untouchedodin0.plugin.PrivateMines;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddonLoader {

    PrivateMines privateMines;
    PluginManager pluginManager;
    private final List<String> addons = new ArrayList<>();

    public AddonLoader(PrivateMines privateMines,
                       PluginManager pluginManager) {
        this.privateMines = privateMines;
        this.pluginManager = pluginManager;
    }

    // Load the addons from the addons folder

    public void load(File file) {
        if (pluginManager != null) {
            try {
                Plugin plugin = pluginManager.loadPlugin(file);
                if (plugin != null) {
                    privateMines.getLogger().info("Loading addon... " + plugin.getName());
                    addons.add(plugin.getName());
                } else {
                    privateMines.getLogger().warning("Failed to load file: " + file);
                }
            } catch (InvalidPluginException | InvalidDescriptionException e) {
                e.printStackTrace();
            }
        }
    }

    // Unload a specific addon via it's name.

    public void unload(String name) {
        if (pluginManager != null) {
            boolean isEnabled = pluginManager.isPluginEnabled(name);
            if (!isEnabled) {
                privateMines.getLogger().warning("The addon " + name + " wasn't enabled!");
            } else {
                Plugin plugin = pluginManager.getPlugin(name);
                privateMines.getLogger().info("Unloading addon " + name);
                if (plugin != null) {
                    pluginManager.disablePlugin(plugin);
                }
            }
        }
    }

    public List<String> getAddons() {
        return addons;
    }
}
