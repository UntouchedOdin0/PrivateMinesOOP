package me.untouchedodin0.plugin.factory;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.util.worldedit.WorldEditRegion;
import org.bukkit.Location;
import org.bukkit.World;

public class PasteFactory {

    PrivateMines privateMines;

    public PasteFactory(PrivateMines privateMines) {
        this.privateMines = privateMines;
    }

    public Region paste(Clipboard clipboard, Location location) {
        if (clipboard == null) {
            privateMines.getLogger().warning("Failed to paste schematic due to clipboard being null");
        } else if (location == null) {
            privateMines.getLogger().warning("Failed to paste schematic due to location being null");
        }

        World world = null;
        BlockVector3 centerVector = null;
        Operation operation;
        Region region;

        if (location != null) {
            world = location.getWorld();
        }

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            if (location != null) {
                centerVector = BlockVector3.at(location.getX(), location.getY(), location.getZ());
            }
            if (clipboard != null) {
                operation = new ClipboardHolder(clipboard).createPaste(editSession).to(centerVector).ignoreAirBlocks(true).build();
                try {
                    Operations.complete(operation);
                    region = clipboard.getRegion();
                    if (centerVector != null) {
                        region.shift(centerVector.subtract(clipboard.getOrigin()));
                        return region;
                    }
                } catch (WorldEditException worldEditException) {
                    worldEditException.printStackTrace();
                }
            }
        }
        return null;
    }
}

