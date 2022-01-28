package me.untouchedodin0.privatemines.compat;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;
import redempt.redlib.region.CuboidRegion;

import java.io.File;


public abstract class WorldEditUtilities {
    private static WorldEditUtilities INSTANCE;
    private static String version;

    protected WorldEditUtilities() {
    }

    public static WorldEditUtilities getInstance() {
        if (INSTANCE == null) {
            final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
            if (plugin == null) {
                return INSTANCE;
            }
            version = plugin.getDescription().getVersion();
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

//    public abstract void createMultiBlockStructure(Player player, String name);

    public abstract void setBlocks(CuboidRegion cuboidRegion, String blockType);

    public abstract void setBlock(Location location, String blockType);

    // This is the thing what should allow for world edit 6 & 7.

    public abstract Region pasteSchematic(Location location, File file);
}
