package me.untouchedodin0.privatemines.we_7.worldedit;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.SessionManager;
import me.untouchedodin0.privatemines.compat.WorldEditUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import redempt.redlib.multiblock.MultiBlockStructure;
import redempt.redlib.region.CuboidRegion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@SuppressWarnings("unused")
public class WorldEditUtils extends WorldEditUtilities {

    private final SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();

    public Location blockVector3toBukkit(World world, BlockVector3 blockVector3) {
        Block block = world.getBlockAt(blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ());
        return block.getLocation();
    }

    public CuboidRegion getRegion(Player player) {
        LocalSession localSession = sessionManager.get(BukkitAdapter.adapt(player));

        com.sk89q.worldedit.world.World world = localSession.getSelectionWorld();

        try {
            if (world == null) {
                throw new IllegalStateException("Invalid world!");
            }

            BlockVector3 minBV3 = localSession.getSelection(world).getMinimumPoint();
            BlockVector3 maxBV3 = localSession.getSelection(world).getMaximumPoint();
            Location minLocation = blockVector3toBukkit(BukkitAdapter.adapt(world), minBV3);
            Location maxLocation = blockVector3toBukkit(BukkitAdapter.adapt(world), maxBV3);
            return new CuboidRegion(minLocation, maxLocation);
        } catch (IncompleteRegionException incompleteRegionException) {
            incompleteRegionException.printStackTrace();
        }
        return null;
    }

    private WorldEditPlugin getWorldEdit() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        return (WorldEditPlugin) plugin;
    }

    @Override
    public void createMultiBlockStructure(Player player, String name) {
        LocalSession localSession = getWorldEdit().getSession(player);
        com.sk89q.worldedit.world.World selectionWorld = localSession.getSelectionWorld();
        org.bukkit.World bukkitWorld = player.getWorld();

        try {
            if (selectionWorld == null) throw new IncompleteRegionException();
            Clipboard clipboard = localSession.getClipboard().getClipboard();
            BlockVector3 minimum = clipboard.getMinimumPoint();
            Location minimumBukkit = blockVector3toBukkit(bukkitWorld, minimum);
            BlockVector3 maximum = clipboard.getMaximumPoint();
            Location maximumBukkit = blockVector3toBukkit(bukkitWorld, maximum);

            String multiBlockStructure = MultiBlockStructure.stringify(minimumBukkit, maximumBukkit);
            try {
                Path path = Paths.get("plugins/PrivateMines" + name + ".dat");
                player.sendMessage("Attempting to write the file " + path.getFileName() + "...");
                Files.write(
                        path,
                        multiBlockStructure.getBytes(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (IncompleteRegionException | EmptyClipboardException incompleteRegionException) {
            player.sendMessage("Please make a full selection");
        }
    }
}
