package me.untouchedodin0.privatemines.worldedit;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.session.SessionOwner;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import redempt.redlib.region.CuboidRegion;

public class Utils {

    SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
    LocalSession localSession;

    public Location blockVectorToBukkit(World world, BlockVector blockVector) {
        Block block = world.getBlockAt(
                blockVector.getBlockX(),
                blockVector.getBlockY(),
                blockVector.getBlockZ());
        return block.getLocation();
    }

    public CuboidRegion getRegion(Player player) {
        localSession = sessionManager.get((SessionOwner) player);

        com.sk89q.worldedit.world.World world = localSession.getSelectionWorld();
        World bukkitWorld = player.getWorld();

        BlockVector minBV;
        BlockVector maxBV;
        Location minLocation;
        Location maxLocation;

        try {
            if (world == null) {
                throw new IllegalStateException("Invalid world!");
            }

            minBV = localSession.getSelection(world).getMinimumPoint().toBlockVector();
            maxBV = localSession.getSelection(world).getMaximumPoint().toBlockPoint();

            minLocation = blockVectorToBukkit(bukkitWorld, minBV);
            maxLocation = blockVectorToBukkit(bukkitWorld, maxBV);

            return new CuboidRegion(minLocation, maxLocation);
        } catch (IncompleteRegionException incompleteRegionException) {
            incompleteRegionException.printStackTrace();
        }
        return null;
    }
}
