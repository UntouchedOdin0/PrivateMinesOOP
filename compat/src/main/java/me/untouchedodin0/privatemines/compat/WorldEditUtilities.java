package me.untouchedodin0.privatemines.compat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;
import redempt.redlib.region.CuboidRegion;

public abstract class WorldEditUtilities {
    private static WorldEditUtilities INSTANCE;

    protected WorldEditUtilities() {
    }

    public static WorldEditUtilities getInstance() {
        if (INSTANCE == null) {
            final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
            if (plugin == null) {
                return INSTANCE;
            }
            final String version = plugin.getDescription().getVersion();
            if (version.startsWith("6")) {
                try {
                    INSTANCE = (WorldEditUtilities) Class.forName("me.untouchedodin0.privatemines.we_6.worldedit.WorldEditUtils").getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    INSTANCE = (WorldEditUtilities) Class.forName("me.untouchedodin0.privatemines.we_7.worldedit.WorldEditUtils").getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        }
        return INSTANCE;
    }

    public Location blockVectorToBukkit(World world, BlockVector blockVector) {
        Block block = world.getBlockAt(
                blockVector.getBlockX(),
                blockVector.getBlockY(),
                blockVector.getBlockZ());
        return block.getLocation();
    }

    public abstract CuboidRegion getCuboidRegion(Player player);

    public abstract void createMultiBlockStructure(Player player, String name);
}
