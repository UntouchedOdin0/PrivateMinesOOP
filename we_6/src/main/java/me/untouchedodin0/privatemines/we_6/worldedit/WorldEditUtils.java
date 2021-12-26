package me.untouchedodin0.privatemines.we_6.worldedit;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import me.untouchedodin0.privatemines.compat.WorldEditUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;
import redempt.redlib.region.CuboidRegion;

import java.io.File;

@SuppressWarnings("unused")
public class WorldEditUtils extends WorldEditUtilities {

    private final SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();

//    public CuboidRegion getRegion(Player player) {
//        LocalSession localSession = sessionManager.get((SessionOwner) player);
//
//        com.sk89q.worldedit.world.World world = localSession.getSelectionWorld();
//        World bukkitWorld = player.getWorld();
//
//        try {
//            if (world == null) {
//                throw new IllegalStateException("Invalid world!");
//            }
//
//            BlockVector minBV = localSession.getSelection(world).getMinimumPoint();
//            BlockVector maxBV = localSession.getSelection(world).getMaximumPoint().toBlockPoint();
//
//            Location minLocation = blockVectorToBukkit(bukkitWorld, toBlockVector(minBV));
//            Location maxLocation = blockVectorToBukkit(bukkitWorld, toBlockVector(maxBV));
//
//            return new CuboidRegion(minLocation, maxLocation);
//        } catch (IncompleteRegionException incompleteRegionException) {
//            incompleteRegionException.printStackTrace();
//        }
//        return null;
//    }

    public org.bukkit.util.BlockVector toBlockVector(BlockVector vector3) {
        return new org.bukkit.util.BlockVector(vector3.getX(), vector3.getY(), vector3.getZ());
    }

    private WorldEditPlugin getWorldEdit() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        return (WorldEditPlugin) plugin;
    }

    @Override
    public CuboidRegion getCuboidRegion(Player player) {
        return null;
    }


//    @Override
//    public void createMultiBlockStructure(Player player, String name) {
//        Selection selection = getWorldEdit().getSelection(player);
//        Location minimum = selection.getMinimumPoint();
//        Location maximum = selection.getMaximumPoint();
//        if (minimum == null || maximum == null) {
//            player.sendMessage("Failed to make a selection!");
//        } else {
//            String multiBlockStructure = MultiBlockStructure.stringify(minimum, maximum);
//            try {
//                Path path = Paths.get("plugins/PrivateMines/" + name + ".dat");
//                player.sendMessage("Attempting to write the file " + path.getFileName() + "...");
//                Files.write(
//                        path,
//                        multiBlockStructure.getBytes(),
//                        StandardOpenOption.CREATE,
//                        StandardOpenOption.TRUNCATE_EXISTING);
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        }
//    }

    @Override
    public void setBlocks(CuboidRegion cuboidRegion, String blockType) {

    }

    @Override
    public void setBlock(Location location, String blockType) {

    }

    @Override
    public void flushQueue() {

    }

    // we 6 paste schem
    @Override
    public Clipboard pasteSchematic(Location location, File file) {
        return null;
    }
}
