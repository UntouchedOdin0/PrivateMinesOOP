package me.untouchedodin0.privatemines.we_7.worldedit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.util.BlockVector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class Utils {

    public Map<String, File> fileMap = new HashMap<>();
    public Map<String, File> test = new TreeMap<>();

    public Map<File, RelativePointsWE7> relativePointsWE7Map = new HashMap<>();

    Material getType(World world, BlockVector3 blockVector3) {
        return BukkitAdapter.adapt(Objects.requireNonNull(world).getFullBlock(blockVector3).getBlockType());
    }

    public void loadFile(String name, File file) {
        if (file.exists()) {
            fileMap.put(name, file);

            fileMap.forEach((s, file1) -> {
                Clipboard clipboard = getClipboard(file);
                Region region = clipboard.getRegion();

                Bukkit.getLogger().info("clipboard: " + clipboard);
                Bukkit.getLogger().info("region: " + region);
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
        World world = region.getWorld();
        RelativePointsWE7 relativePointsWE7 = new RelativePointsWE7();

        region.iterator().forEachRemaining(blockVector3 -> {
            Material material = utils.getType(world, blockVector3);
            BlockVector blockVector = new BlockVector(blockVector3.getBlockX(),
                                                      blockVector3.getBlockY(),
                                                      blockVector3.getBlockZ());
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
            RelativePointsWE7 relativePointsWE7 = findRelativePoints(region, spawnMaterial, cornerMaterial);
            Bukkit.getLogger().info("relativePointsWE7: " + relativePointsWE7);
            putPoints(file, relativePointsWE7);
        });
    }
}
