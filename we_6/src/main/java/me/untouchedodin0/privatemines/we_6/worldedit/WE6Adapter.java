package me.untouchedodin0.privatemines.we_6.worldedit;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.function.pattern.Patterns;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionIntersection;
import com.sk89q.worldedit.regions.RegionOperationException;
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

public class WE6Adapter implements WorldEditAdapter {

    BlockVector spawnPoint;
    List<BlockVector> corners = new ArrayList<>(2);

    @Override
    public CuboidRegion pasteSchematic(Location location, Path file) {
        World world = new BukkitWorld(location.getWorld());
        final EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);

        try (InputStream fis = Files.newInputStream(file)) {
            Clipboard clipboard =
                    ClipboardFormat.SCHEMATIC.getReader(fis).read(editSession.getWorld().getWorldData());
            Vector to = BukkitUtil.toVector(location)
                    .setY(clipboard.getOrigin().getY());

            final ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard, editSession.getWorld().getWorldData());
            Operation operation = clipboardHolder
                    .createPaste(editSession, editSession.getWorld().getWorldData())
                    .to(to)
                    .ignoreAirBlocks(true)
                    .build();
            Operations.completeBlindly(operation);

            final Region region = clipboard.getRegion();
            final Vector diff = to.subtract(clipboard.getOrigin());
            region.shift(diff);

            return new CuboidRegion(
                    BukkitUtil.toLocation(location.getWorld(), region.getMinimumPoint()),
                    BukkitUtil.toLocation(location.getWorld(), region.getMaximumPoint())
            );
        } catch (IOException | RegionOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void fillRegion(CuboidRegion region, Map<Material, Double> materials) {
        World world = new BukkitWorld(region.getWorld());
        final EditSession editSession =
                WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
        editSession.enableQueue();
        final RandomPattern randomPattern = new RandomPattern();

        materials.forEach((material, chance) -> {
            //noinspection deprecation SHUT UP
            final BlockPattern pattern = new BlockPattern(new BaseBlock(material.getId()));
            randomPattern.add(pattern, chance);
        });

        final Region worldEditRegion = new com.sk89q.worldedit.regions.CuboidRegion(
                BukkitUtil.toVector(region.getStart()),
                BukkitUtil.toVector(region.getEnd())
        );
        try {
            editSession.setBlocks(worldEditRegion, Patterns.wrap(randomPattern));
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
            // this shouldn't happen
        }
        editSession.flushQueue();
    }

    public BlockVector findRelativeSpawnPoint(Region region, Material spawnMaterial) {
        Utils utils = new Utils();
        org.bukkit.World world = Bukkit.getWorld(Objects.requireNonNull(region.getWorld()).getName());
        region.forEach(blockVector -> {
            if (utils.getType(world, blockVector).equals(spawnMaterial)) {
                spawnPoint = blockVector;
            }
        });
        return spawnPoint;
    }

    public List<BlockVector> findCornerPoints(Region region, Material cornerMaterial) {
        Utils utils = new Utils();
        org.bukkit.World world = Bukkit.getWorld(Objects.requireNonNull(region.getWorld()).getName());
        region.forEach(blockVector -> {
            if (utils.getType(world, blockVector).equals(cornerMaterial)) {
                corners.add(blockVector);
            }
        });
        return corners;
    }
}
