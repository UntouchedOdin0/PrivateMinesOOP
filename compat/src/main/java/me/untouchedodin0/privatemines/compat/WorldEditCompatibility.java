package me.untouchedodin0.privatemines.compat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class WorldEditCompatibility {
    private WorldEditCompatibility() {}
    public static @Nullable WorldEditAdapter loadWorldEdit() {
        final Plugin worldEditPlugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        final Plugin fawePlugin = Bukkit.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit");

        // 1.7.10 - 1.12 21.03.26-5ff3a9b-1286-22.3.9
        // 1.13 1.13-45;0cad7f2
        // 1.14 1.14-361;f5f5a55
        // 1.15+ 2.0.2-SNAPSHOT-89;7da993d

        String version = "";

        if (worldEditPlugin == null && fawePlugin == null) {
            return null;
        }

        if (worldEditPlugin != null) {
            version = worldEditPlugin.getDescription().getVersion();
        }

        if (fawePlugin != null) {
            version = fawePlugin.getDescription().getVersion();
        }

        if (
                version.startsWith("6")   ||
                version.startsWith("19")  ||
                version.startsWith("21")) {
            return Reflect.instantiate("me.untouchedodin0.privatemines.we_6.worldedit.WE6Adapter");
        }
        if (
                                version.startsWith("2")    ||
                                version.startsWith("7")    ||
                                version.startsWith("1.13") ||
                                version.startsWith("1.14") ||
                                version.startsWith("1.17")) {
            return Reflect.instantiate("me.untouchedodin0.privatemines.we_7.worldedit.WE7Adapter");
        }
        return null;
    }
}
