package me.untouchedodin0.plugin.mines;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.world.MineWorldManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

public class WorldEditMine {

    private final PrivateMines privateMines;
    private UUID mineOwner;
    private Location mineLocation;
    private Location location;
    private WorldEditMineType worldEditMineType;
    private EditSession editSession;
    private Clipboard clipboard;
    private MineWorldManager mineWorldManager;

    public WorldEditMine(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.mineWorldManager = privateMines.getMineWorldManager();
    }

    public void setMineOwner(UUID mineOwner) {
        this.mineOwner = mineOwner;
    }

    public void setMineLocation(Location location) {
        this.mineLocation = location;
    }

    public void setWorldEditMineType(WorldEditMineType worldEditMineType) {
        this.worldEditMineType = worldEditMineType;
    }

    public File getSchematicFile() {
        return worldEditMineType.getSchematicFile();
    }

    public void paste(Location location) {
        World world = location.getWorld();
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(getSchematicFile());
//        int x = mineLocation.getBlockX();
//        int y = mineLocation.getBlockY();
//        int z = mineLocation.getBlockZ();

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        BlockVector3 blockVector3 = BlockVector3.at(x, y, z);

        privateMines.getLogger().info("world: " + world);
        privateMines.getLogger().info("clipboardformat: " + clipboardFormat);
        privateMines.getLogger().info("x: " + x);
        privateMines.getLogger().info("y: " + y);
        privateMines.getLogger().info("z: " + z);
        privateMines.getLogger().info("blockvector3: " + blockVector3);

        if (clipboardFormat != null) {
            try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(getSchematicFile()))) {
                clipboard = clipboardReader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(blockVector3)
                        .build();
                Operations.completeLegacy(operation);
            } catch (WorldEditException worldEditException) {
                worldEditException.printStackTrace();
            }

            privateMines.getLogger().info("clipboard: " + clipboard);
        }
    }

//    public void teleport(Player player) {
//        player.teleport(location);
//    }
}
