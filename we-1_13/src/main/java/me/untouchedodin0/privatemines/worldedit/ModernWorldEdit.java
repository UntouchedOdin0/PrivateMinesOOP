package me.untouchedodin0.privatemines.worldedit;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import redempt.redlib.multiblock.MultiBlockStructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ModernWorldEdit {

    String multiBlockStructure;
    Path path;

    public WorldEditPlugin getWorldEdit() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin instanceof WorldEditPlugin) {
            return (WorldEditPlugin) plugin;
        } else {
            return null;
        }
    }

    public void createFile(Player player, String name) {
        LocalSession localSession = getWorldEdit().getSession(player);
        Utils utils;
        World selectionWorld = localSession.getSelectionWorld();
        Clipboard clipboard;
        BlockVector3 minimum;
        BlockVector3 maximum;
        Location minimumBukkit;
        Location maximumBukkit;

        org.bukkit.World bukkitWorld = player.getWorld();

        try {
            if (selectionWorld == null) throw new IncompleteRegionException();
            utils = new Utils();
            clipboard = localSession.getClipboard().getClipboard();
            minimum = clipboard.getMinimumPoint();
            minimumBukkit = utils.blockVector3toBukkit(bukkitWorld, minimum);
            maximum = clipboard.getMaximumPoint();
            maximumBukkit = utils.blockVector3toBukkit(bukkitWorld, maximum);

            if (minimumBukkit == null || maximumBukkit == null) {
                player.sendMessage("Failed to make a selection!");
            } else {
                multiBlockStructure = MultiBlockStructure.stringify(minimumBukkit, maximumBukkit);
                try {
                    path = Paths.get("plugins/PrivateMines" + name + ".dat");
                    player.sendMessage("Attempting to write the file " + path.getFileName() + "...");
                    Files.write(
                            path,
                            multiBlockStructure.getBytes(),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        } catch (IncompleteRegionException | EmptyClipboardException incompleteRegionException) {
            player.sendMessage("Please make a full selection");
        }
    }
}
