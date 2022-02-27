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
import com.sk89q.worldedit.regions.RegionIntersection;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import me.untouchedodin0.privatemines.compat.WorldEditAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import redempt.redlib.region.CuboidRegion;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WE7Adapter implements WorldEditAdapter {

    BlockVector3 spawnPoint;
    List<BlockVector3> corners = new ArrayList<>(2);

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

            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build()) {
                BlockVector3 centerVector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

                // If the clipboard isn't null prepare to create a paste operation, complete it and set the region stuff.
                Operation operation = new ClipboardHolder(clipboard)
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

    public Clipboard loadClipboard(Path file) throws IOException {
        Clipboard clipboard;
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file.toFile());
        if (clipboardFormat == null) throw new IllegalArgumentException("File is not a valid schematic");
        try (InputStream inputStream = Files.newInputStream(file);
             ClipboardReader clipboardReader = clipboardFormat.getReader(inputStream)) {
            clipboard = clipboardReader.read();
            if (clipboard == null) {
                throw new IllegalArgumentException("Clipboard is null");
            }
        }
        return clipboard;
    }

    @Override
    public void fillRegion(CuboidRegion region, Map<Material, Double> materials) {
        World world = new BukkitWorld(region.getWorld());

        try (final EditSession editSession =
                     WorldEdit.getInstance().newEditSessionBuilder().world(world).build()) {
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

            editSession.setBlocks((Region) worldEditRegion, randomPattern);
            editSession.flushQueue();
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
            // this shouldn't happen
        }
    }

    public BlockVector3 findRelativeSpawnPoint(Region region, Material spawnMaterial) {
        Utils utils = new Utils();
        World world = region.getWorld();
        region.forEach(blockVector3 -> {
            Material material = utils.getTypeAtBlockVector3(BukkitAdapter.adapt(world), blockVector3);
            Bukkit.getLogger().info("material: " + material);
            if (material.equals(spawnMaterial)) spawnPoint = blockVector3;
        });
        return spawnPoint;
    }

    public List<BlockVector3> findCornerPoints(Region region, Material cornerMaterial) {
        Utils utils = new Utils();
        World world = region.getWorld();

        region.forEach(blockVector3 -> {
            Material material = utils.getTypeAtBlockVector3(BukkitAdapter.adapt(world), blockVector3);

        });
        return corners;
    }

    public static Region walls(com.sk89q.worldedit.regions.CuboidRegion region) {
        BlockVector3 pos1 = region.getPos1();
        BlockVector3 pos2 = region.getPos2();

        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        return new RegionIntersection(
                // Project to Z-Y plane
                new com.sk89q.worldedit.regions.CuboidRegion(pos1.withX(min.getX()), pos2.withX(min.getX())),
                new com.sk89q.worldedit.regions.CuboidRegion(pos1.withX(max.getX()), pos2.withX(max.getX())),

                // Project to X-Y plane
                new com.sk89q.worldedit.regions.CuboidRegion(pos1.withZ(min.getZ()), pos2.withZ(min.getZ())),
                new com.sk89q.worldedit.regions.CuboidRegion(pos1.withZ(max.getZ()), pos2.withZ(max.getZ())),

                // Project to the X-Z plane
                new com.sk89q.worldedit.regions.CuboidRegion(pos1.withY(min.getY()), pos2.withY(min.getY())));
    }

    public static Region walls(CuboidRegion region) {
        BlockVector3 pos1 = BlockVector3.at(region.getStart().getBlockX(), region.getStart().getBlockY(), region.getStart().getBlockZ());
        BlockVector3 pos2 = BlockVector3.at(region.getEnd().getBlockX(), region.getEnd().getBlockY(), region.getEnd().getBlockZ());

        BlockVector3 min = BlockVector3.at(region.getStart().getBlockX(), region.getStart().getBlockY(), region.getStart().getBlockZ());
        BlockVector3 max = BlockVector3.at(region.getEnd().getBlockX(), region.getEnd().getBlockY(), region.getEnd().getBlockZ());

        return new RegionIntersection(
                // Project to Z-Y plane
                new com.sk89q.worldedit.regions.CuboidRegion(pos1.withX(min.getX()), pos2.withX(min.getX())),
                new com.sk89q.worldedit.regions.CuboidRegion(pos1.withX(max.getX()), pos2.withX(max.getX())),

                // Project to X-Y plane
                new com.sk89q.worldedit.regions.CuboidRegion(pos1.withZ(min.getZ()), pos2.withZ(min.getZ())),
                new com.sk89q.worldedit.regions.CuboidRegion(pos1.withZ(max.getZ()), pos2.withZ(max.getZ())),

                // Project to the X-Z plane
                new com.sk89q.worldedit.regions.CuboidRegion(pos1.withY(min.getY()), pos2.withY(min.getY())));
    }
}
