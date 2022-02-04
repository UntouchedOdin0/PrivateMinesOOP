package me.untouchedodin0.privatemines.compat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class WorldEditCompatability {
    private WorldEditCompatability() {}
    public static @Nullable WorldEditAdapter loadWorldEdit() {
        final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin == null) {
            return null;
        }
        String version = plugin.getDescription().getVersion();
        if (version.startsWith("6")) {
            return Reflect.instantiate("me.untouchedodin0.privatemines.we_6.worldedit.WE6Adapter");
        }
        if (version.startsWith("7")) {
            return Reflect.instantiate("me.untouchedodin0.privatemines.we_7.worldedit.WE7Adapter");
        }
        return null;
    }
}
