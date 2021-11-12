package me.untouchedodin0.plugin.mines;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class WorldEditMine {

    private final PrivateMines privateMines;
    private WorldEditMineType worldEditMineType;

    private UUID mineOwner;
    private CuboidRegion cuboidRegion;
    private Location spawnLocation;
    private World world;

    private final BlockType fillType = BlockTypes.DIAMOND_BLOCK;

    // sick this works
    private final BlockType test = BukkitAdapter.asBlockType(Material.AIR);

    final Utils utils;

    public WorldEditMine(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.utils = new Utils(privateMines);
    }

    public void setMineOwner(UUID mineOwner) {
        this.mineOwner = mineOwner;
    }

    public UUID getMineOwner() {
        return mineOwner;
    }

    public void setWorldEditMineType(WorldEditMineType worldEditMineType) {
        this.worldEditMineType = worldEditMineType;
    }

    public void setCuboidRegion(CuboidRegion cuboidRegion) {
        this.cuboidRegion = cuboidRegion;
    }

    public CuboidRegion getCuboidRegion() {
        return cuboidRegion;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public File getSchematicFile() {
        return worldEditMineType.getSchematicFile();
    }

    public void teleport(Player player) {
        player.teleport(getSpawnLocation());
    }

    public void setWorld(World world) {
        this.world = world;
    }

    // Resets the mine
    public void reset() {

        final var fillType = BlockTypes.BONE_BLOCK;
        final BlockType test = BukkitAdapter.asBlockType(Material.STONE); // make thing from this
        final BlockType type = utils.bukkitToBlockType(Material.DIAMOND_BLOCK);

        if (world == null) {
            privateMines.getLogger().warning("Failed to reset due to the mine being null");
        }

        // Makes sure everything isn't null
        if (cuboidRegion != null && fillType != null) {

            // Creates edit session, sets the blocks and flushes it!
            try (final var session = WorldEdit.getInstance()
                    .newEditSession(BukkitAdapter.adapt(world))) {
                session.setBlocks(getCuboidRegion(), fillType.getDefaultState());
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            }
        }
    }

//    public void teleport(Player player) {
//        player.teleport(location);
//    }
}
