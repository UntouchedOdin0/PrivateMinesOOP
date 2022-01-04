package me.untouchedodin0.privatemines.we_6.worldedit;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;

public class MineFactoryWE6 {

    // create the world edit mine

    public WorldEditMine6 createMine(Player player, Location location, WorldEditMineType6 worldEditMineType6) {
        WorldEditMine6 worldEditMine6 = new WorldEditMine6();
        WorldEditMine6Data worldEditMine6Data = new WorldEditMine6Data();
        File file = worldEditMineType6.getSchematicFile();
        ClipboardFormat clipboardFormat = ClipboardFormat.findByFile(file);

        return worldEditMine6;
    }
}
