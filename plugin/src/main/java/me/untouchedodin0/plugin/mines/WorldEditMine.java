package me.untouchedodin0.plugin.mines;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
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

    final Utils utils;
    private final PrivateMines privateMines;
    private final BlockType fillType = BlockTypes.DIAMOND_BLOCK;
    // sick this works
    private final BlockType test = BukkitAdapter.asBlockType(Material.AIR);
    private WorldEditMineType worldEditMineType;
    private UUID mineOwner;
    private CuboidRegion cuboidRegion;
    private Region region;
    private Location spawnLocation;
    private World world;
    private Location location;
    private Material material;

    public WorldEditMine(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.utils = new Utils(privateMines);
    }

    public UUID getMineOwner() {
        return mineOwner;
    }

    public void setMineOwner(UUID mineOwner) {
        this.mineOwner = mineOwner;
    }

    public void setWorldEditMineType(WorldEditMineType worldEditMineType) {
        this.worldEditMineType = worldEditMineType;
    }

    public CuboidRegion getCuboidRegion() {
        return cuboidRegion;
    }

    public void setCuboidRegion(CuboidRegion cuboidRegion) {
        this.cuboidRegion = cuboidRegion;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public BlockState getFillState() {
        final BlockType blockType = utils.bukkitToBlockType(getMaterial());
        return utils.getBlockState(blockType);
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

        final var fillType = utils.bukkitToBlockType(getMaterial());

        if (world == null) {
            privateMines.getLogger().warning("Failed to reset due to the mine world being null");
        }

        // Makes sure everything isn't null
        if (cuboidRegion != null && fillType != null) {

            // Creates edit session, sets the blocks and flushes it!
            try (final var session = WorldEdit.getInstance()
                    .newEditSession(BukkitAdapter.adapt(world))) {
                session.setBlocks(getCuboidRegion(), getFillState());
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete() {

        if (world == null) {
            privateMines.getLogger().warning("Failed to delete the mine due to the world being null");
        }

        final var region = getRegion();
        final var cuboidRegion = getCuboidRegion();
        final var air = utils.bukkitToBlockType(Material.AIR);

        // Creates edit session, sets the blocks and flushes it!
        try (final var session = WorldEdit.getInstance()
                .newEditSession(BukkitAdapter.adapt(world))) {
            session.setBlocks(region, utils.getBlockState(air));
            session.setBlocks(cuboidRegion, utils.getBlockState(air));
        } catch (MaxChangedBlocksException exception) {
            exception.printStackTrace();
        }
        this.cuboidRegion = null;
        this.region = null;
    }
}

//    public void teleport(Player player) {
//        player.teleport(location);
//    }

