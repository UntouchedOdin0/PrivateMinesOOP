/*
MIT License

Copyright (c) 2021 Kyle Hicks

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

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
