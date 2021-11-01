package me.untouchedodin0.privatemines.worldedit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
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

public class LegacyWorldEdit {

    String multiBlockStructure;
    Path path;
    Selection selection;

    public WorldEditPlugin getWorldEdit() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin instanceof WorldEditPlugin) {
            return (WorldEditPlugin) plugin;
        } else {
            return null;
        }
    }

    public void createMultiBlockStructure(Player player, String name) {

        selection = getWorldEdit().getSelection(player);
        Location minimum = selection.getMinimumPoint();
        Location maximum = selection.getMaximumPoint();
        if (minimum == null || maximum == null) {
            player.sendMessage("Failed to make a selection!");
        } else {
            multiBlockStructure = MultiBlockStructure.stringify(minimum, maximum);
            try {
                path = Paths.get("plugins/PrivateMines/" + name + ".dat");
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
    }
}
