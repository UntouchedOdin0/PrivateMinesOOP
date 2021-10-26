package me.untouchedodin0.privatemines.worldedit;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.SessionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import redempt.redlib.region.CuboidRegion;

public class Utils {

    SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
    LocalSession localSession;

    public Location blockVector3toBukkit(World world, BlockVector3 blockVector3) {
        Block block = world.getBlockAt(blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ());
        return block.getLocation();
    }

    public CuboidRegion getRegion(Player player) {
        localSession = sessionManager.get(BukkitAdapter.adapt(player));

        com.sk89q.worldedit.world.World world = localSession.getSelectionWorld();
        BlockVector3 minBV3;
        BlockVector3 maxBV3;
        Location minLocation;
        Location maxLocation;

        try {
            if (world == null) {
                throw new IllegalStateException("Invalid world!");
            }

            minBV3 = localSession.getSelection(world).getMinimumPoint();
            maxBV3 = localSession.getSelection(world).getMaximumPoint();
            minLocation = blockVector3toBukkit(BukkitAdapter.adapt(world), minBV3);
            maxLocation = blockVector3toBukkit(BukkitAdapter.adapt(world), maxBV3);
            return new CuboidRegion(minLocation, maxLocation);
        } catch (IncompleteRegionException incompleteRegionException) {
            incompleteRegionException.printStackTrace();
        }
        return null;
    }
}
