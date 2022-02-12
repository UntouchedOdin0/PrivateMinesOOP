package me.untouchedodin0.privatemines.we_7.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import me.untouchedodin0.privatemines.compat.WorldEditAdapter;
import org.bukkit.Location;
import org.bukkit.Material;
import redempt.redlib.region.CuboidRegion;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public class WE7Adapter implements WorldEditAdapter {
    @Override
    public CuboidRegion pasteSchematic(Location location, Path file) {
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file.toFile());
        World world = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));

        if (clipboardFormat == null) {
            throw new IllegalArgumentException("File is not a valid schematic");
        }
        try (InputStream fix = Files.newInputStream(file); ClipboardReader clipboardReader =
                clipboardFormat.getReader(fix)) {
            Clipboard clipboard = clipboardReader.read();
            if (clipboard == null) {
                throw new IllegalArgumentException("Clipboard is null");
            }

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                BlockVector3 centerVector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

                // If the clipboard isn't null prepare to create a paste operation, complete it and set the region stuff.
                Operation operation =
                        new ClipboardHolder(clipboard)
                                .createPaste(editSession)
                                .to(centerVector)
                                .ignoreAirBlocks(true)
                                .build();
                Operations.complete(operation);
                Region region = clipboard.getRegion();

                region.shift(centerVector.subtract(clipboard.getOrigin()));
                return new CuboidRegion(BukkitAdapter.adapt(location.getWorld(), region.getMinimumPoint()), BukkitAdapter.adapt(location.getWorld(), region.getMaximumPoint()));
            }
        } catch (IOException | WorldEditException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void fillRegion(CuboidRegion region, Map<Material, Double> materials) {
        World world = new BukkitWorld(region.getWorld());
        try (final EditSession editSession =
                     WorldEdit.getInstance().newEditSession(world)) {
            editSession.setReorderMode(EditSession.ReorderMode.MULTI_STAGE);
            final RandomPattern randomPattern = new RandomPattern();

            materials.forEach((material, chance) -> {
                Pattern pattern = BukkitAdapter.adapt(material.createBlockData()).toBaseBlock();
                randomPattern.add(pattern, chance);
            });

            final com.sk89q.worldedit.regions.CuboidRegion worldEditRegion = new com.sk89q.worldedit.regions.CuboidRegion(
                    BukkitAdapter.asBlockVector(region.getStart()),
                    BukkitAdapter.asBlockVector(region.getEnd())
            );

            editSession.setBlocks(worldEditRegion, randomPattern);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
            // this shouldn't happen
        }
    }

}
