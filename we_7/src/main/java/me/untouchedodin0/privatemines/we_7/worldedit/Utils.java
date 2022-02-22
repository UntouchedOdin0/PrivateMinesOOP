package me.untouchedodin0.privatemines.we_7.worldedit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Utils {

    public Map<String, File> fileMap = new HashMap<>();
    public Map<String, File> test = new TreeMap<>();

    public Map<File, RelativePointsWE7> relativePointsWE7Map = new HashMap<>();

    public void loadFile(String name, File file, World world) {

        Utils utils = new Utils();
        if (file.exists()) {
            fileMap.put(name, file);

            fileMap.forEach((s, file1) -> {
                Clipboard clipboard = getClipboard(file);
                Region region = clipboard.getRegion();
                Bukkit.getLogger().info("region: " + region);
                com.sk89q.worldedit.world.World WEWorld = BukkitAdapter.adapt(world);
                Bukkit.getLogger().info("WEWorld: " + WEWorld);

                clipboard.getRegion().forEach(blockVector3 -> {
                    BlockState blockState = clipboard.getBlock(blockVector3);
                    if (blockState.toBaseBlock().getBlockType().equals(BlockTypes.SPONGE)) {
                        Bukkit.getLogger().info("FOUND SPONGE AT: " + blockVector3);
                    } else if (blockState.toBaseBlock().getBlockType().equals(BlockTypes.POWERED_RAIL)) {
                        Bukkit.getLogger().info("FOUND POWERED RAIL AT: " + blockVector3);
                    }
                });
            });

            Bukkit.getLogger().info("Loaded file: " + file.getName() + "!");
        } else {
            Bukkit.getLogger().warning("File : " + file + " didn't exist!");
        }
    }

    public Clipboard getClipboard(File file) {
        Clipboard clipboard = null;
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);
        if (clipboardFormat != null) {
            try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(file))) {
                clipboard = clipboardReader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return clipboard;
    }

    public RelativePointsWE7 findRelativePoints(Region region, Material spawnMaterial, Material cornerMaterial) {
        Utils utils = new Utils();
        World world = BukkitAdapter.adapt(region.getWorld());
        RelativePointsWE7 relativePointsWE7 = new RelativePointsWE7();

        region.iterator().forEachRemaining(blockVector3 -> {
            Material material = world.getBlockAt(blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ()).getType(); //utils.getType(world, blockVector3);
            BlockVector blockVector = new BlockVector(blockVector3.getBlockX(),
                                                      blockVector3.getBlockY(),
                                                      blockVector3.getBlockZ());
            relativePointsWE7.setWorld(region.getWorld());
            if (material.equals(spawnMaterial)) {
                relativePointsWE7.setSpawn(blockVector);
            } else if (material.equals(cornerMaterial) && relativePointsWE7.corner1 == null) {
                relativePointsWE7.setCorner1(blockVector);
            } else if (material.equals(cornerMaterial) && relativePointsWE7.corner2 == null) {
                relativePointsWE7.setCorner2(blockVector);
            }
        });
        return relativePointsWE7;
    }

    public void putPoints(File file, RelativePointsWE7 relativePointsWE7) {
        relativePointsWE7Map.put(file, relativePointsWE7);
    }

    public void loadAndIterateFiles(Material spawnMaterial, Material cornerMaterial) {
        Bukkit.getLogger().info("file map size: " + fileMap.size());
        Bukkit.getLogger().info("test map size: " + test.size());

        fileMap.forEach((name, file) -> {
            Bukkit.getLogger().info("Loading file " + file);
            Clipboard clipboard = getClipboard(file);
            Region region = clipboard.getRegion();
            WE7Adapter we7Adapter = new WE7Adapter();
            BlockVector3 spawnPoint = we7Adapter.findRelativeSpawnPoint(region, spawnMaterial);

            //RelativePointsWE7 relativePointsWE7 = findRelativePoints(region, spawnMaterial, cornerMaterial);
            //Bukkit.getLogger().info("relativePointsWE7: " + relativePointsWE7);
            //putPoints(file, relativePointsWE7);
        });
    }

    public Material getTypeAtBlockVector3(World world, BlockVector3 blockVector3) {
        BlockState blockState = BukkitAdapter.adapt(world).getBlock(blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ()).getBlockType().getDefaultState();
        return BukkitAdapter.adapt(blockState).getMaterial();
    }
}
