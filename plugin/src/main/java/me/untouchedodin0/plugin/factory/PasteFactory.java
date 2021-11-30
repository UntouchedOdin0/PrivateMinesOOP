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
import org.bukkit.Location;
import org.bukkit.World;

public class PasteFactory {

    PrivateMines privateMines;

    public PasteFactory(PrivateMines privateMines) {
        this.privateMines = privateMines;
    }

    // Pastes a clipboard at a location
    public Region paste(Clipboard clipboard, Location location) {

        // Logs a warning if the clipboard or location is null
        if (clipboard == null) {
            privateMines.getLogger().warning("Failed to paste schematic due to clipboard being null");
        } else if (location == null) {
            privateMines.getLogger().warning("Failed to paste schematic due to location being null");
        }

        // Set up the variables.
        World world = null;
        BlockVector3 centerVector = null;
        Operation operation;
        Region region;

        // If the location isn't null, set the world;
        if (location != null) {
            world = location.getWorld();
        }

        // Try and make a new edit session with the world to do stuff
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {

            // If the location isn't null then set the center vector to the location
            if (location != null) {
                centerVector = BlockVector3.at(location.getX(), location.getY(), location.getZ());
            }
            // If the clipboard isn't null prepare to create a paste operation, complete it and set the region stuff.
            if (clipboard != null) {
                operation = new ClipboardHolder(clipboard).createPaste(editSession).to(centerVector).ignoreAirBlocks(true).build();
                try {
                    // Complete the paste
                    Operations.complete(operation);
                    // Set the region to the clipboard region
                    region = clipboard.getRegion();
                    // Check if the center vector isn't null and if it's not then set it to the Origin - centerVector.
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

