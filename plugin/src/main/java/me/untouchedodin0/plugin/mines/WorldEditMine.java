package me.untouchedodin0.plugin.mines;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.mines.data.WorldEditMineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Utils;
import me.untouchedodin0.plugin.util.worldedit.Adapter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import redempt.redlib.blockdata.DataBlock;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class WorldEditMine {

    final Utils utils;
    private final PrivateMines privateMines;
    private WorldEditMineType worldEditMineType;
    private UUID mineOwner;
    private CuboidRegion cuboidRegion;
    private Region bedrockCubeRegion;
    private Region region;
    private BlockVector3 min;
    private BlockVector3 max;
    private Location spawnLocation;
    private World world;
    private Location location;
    private Material material;
    private DataBlock dataBlock;
    private MineStorage mineStorage;
    private WorldEditMineData worldEditMineData;

    public static final List<BlockVector3> EXPANSION_VECTORS = List.of(BlockVector3.UNIT_X, BlockVector3.UNIT_MINUS_X,
                                                                       BlockVector3.UNIT_Z, BlockVector3.UNIT_MINUS_Z);

    public WorldEditMine(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.utils = new Utils(privateMines);
        this.mineStorage = privateMines.getMineStorage();
    }

    public WorldEditMineData getWorldEditMineData() {
        return worldEditMineData;
    }

    public void setWorldEditMineData(WorldEditMineData worldEditMineData) {
        this.worldEditMineData = worldEditMineData;
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

    public WorldEditMineType getWorldEditMineType() {
        return worldEditMineType;
    }

    public CuboidRegion getCuboidRegion() {
        return cuboidRegion;
    }

    public void setCuboidRegion(CuboidRegion cuboidRegion) {
        this.cuboidRegion = cuboidRegion;
    }

    public Region getBedrockCubeRegion() {
        return region;
    }

    public void setBedrockCubeRegion(Region region) {
        this.bedrockCubeRegion = region;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void setMin(BlockVector3 min) {
        this.min = min;
    }

    public BlockVector3 getMin() {
        return min;
    }

    public void setMax(BlockVector3 max) {
        this.max = max;
    }

    public BlockVector3 getMax() {
        return max;
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

    public DataBlock getDataBlock() {
        return dataBlock;
    }

    public void setDataBlock(DataBlock dataBlock) {
        this.dataBlock = dataBlock;
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

        this.world = privateMines.getMineWorldManager().getMinesWorld();
        if (world == null) {
            privateMines.getLogger().warning("Failed to delete the mine due to the world being null");
        }

        int minX = getDataBlock().getInt("minX");
        int minY = getDataBlock().getInt("minY");
        int minZ = getDataBlock().getInt("minZ");

        int maxX = getDataBlock().getInt("maxX");
        int maXY = getDataBlock().getInt("maxY");
        int maxZ = getDataBlock().getInt("maxZ");

        BlockVector3 min = BlockVector3.at(minX, minY, minZ);
        BlockVector3 max = BlockVector3.at(maxX, maXY, maxZ);

        final var cuboidRegion = new CuboidRegion(min, max);

        final var air = utils.bukkitToBlockType(Material.AIR);
        final var dataBlock = getDataBlock();
        final var cuboid = new CuboidRegion(min, max);


        // Creates edit session, sets the blocks and flushes it!
        try (final var session = WorldEdit.getInstance()
                .newEditSession(BukkitAdapter.adapt(world))) {
            session.setBlocks(cuboid, utils.getBlockState(air));
//            session.setBlocks(region, utils.getBlockState(air));
        } catch (MaxChangedBlocksException exception) {
            exception.printStackTrace();
        }
        setCuboidRegion(null);
        setRegion(null);
        this.cuboidRegion = null;
        this.region = null;
        dataBlock.remove();
    }

    public void upgrade() {
        Utils utils = privateMines.getUtils();
        MineFactory mineFactory = privateMines.getMineFactory();
        MineStorage mineStorage = privateMines.getMineStorage();
        WorldEditMineType worldEditMineType = utils.getNextMineType(this);
        DataBlock dataBlock = getDataBlock();

        int minX = getDataBlock().getInt("minX");
        int minY = getDataBlock().getInt("minY");
        int minZ = getDataBlock().getInt("minZ");

        int maxX = getDataBlock().getInt("maxX");
        int maXY = getDataBlock().getInt("maxY");
        int maxZ = getDataBlock().getInt("maxZ");

        BlockVector3 min = BlockVector3.at(minX, minY, minZ);
        BlockVector3 max = BlockVector3.at(maxX, maXY, maxZ);

        final var cuboid = new CuboidRegion(min, max);
        final var air = utils.bukkitToBlockType(Material.AIR);

        try (final var session = WorldEdit.getInstance()
                .newEditSession(BukkitAdapter.adapt(world))) {
            session.setBlocks(cuboid, utils.getBlockState(air));
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
        setCuboidRegion(null);
        setRegion(null);
        this.cuboidRegion = null;
        this.region = null;
    }

    private BlockVector3[] expansionVectors(final int amount) {
        return EXPANSION_VECTORS.stream().map(it -> it.multiply(amount)).toArray(BlockVector3[]::new);
    }

    public void expand(final int amount) {
        final var fillType = BlockTypes.DIAMOND_BLOCK;
        final var wallType = BlockTypes.BEDROCK;
        final var min = getCuboidRegion().getMinimumPoint();
        final var max = getCuboidRegion().getMaximumPoint();

        if (fillType == null || wallType == null) {
            return;
        }

        final var mine = getCuboidRegion(); //Adapter.adapt(getCuboidRegion());
        privateMines.getLogger().info("expand mine cuboid : " + mine);

        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            mine.expand(expansionVectors(amount));
            setCuboidRegion(null);
            setCuboidRegion(mine);
//            this.cuboidRegion = mine;

            session.setBlocks(mine, fillType.getDefaultState());
            session.setBlocks(Adapter.walls(mine), wallType.getDefaultState());

        } catch (MaxChangedBlocksException ex) {
            ex.printStackTrace();
        }

        privateMines.getLogger().info("expand min: " + min);
        privateMines.getLogger().info("expand max: " + max);

        final var stupidWallCuboid = Adapter.walls(mine);

//        final var stupidWallCuboid = new CuboidRegion(BukkitAdapter.adapt(world, mine.getMinimumPoint()),
//                BukkitAdapter.adapt(world, mine.getMaximumPoint()));

        privateMines.getLogger().info("expand mine min: " + mine.getMinimumPoint());
        privateMines.getLogger().info("expand mine max: " + mine.getMaximumPoint());

//        privateMines.getLogger().info("expand stupidWallCuboid: " + stupidWallCuboid);

        setBedrockCubeRegion(stupidWallCuboid);
    }

//    public void expand(final int amount) {
//        final var fillType = BlockTypes.DIAMOND_BLOCK;
//        final var wallType = BlockTypes.BEDROCK;
//
//        if (fillType == null || wallType == null) {
//            return;
//        }
//
//        final var mine = getCuboidRegion();
//        final var fillCuboid = mine.clone();
//
//        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
//
//            mine.expand(expansionVectors(amount));
//
//            session.setBlocks(mine, fillType.getDefaultState());
//            session.setBlocks(Adapter.walls(mine), wallType.getDefaultState());
//        } catch (MaxChangedBlocksException ex) {
//            ex.printStackTrace();
//        }
//
//        final var stupidWallCuboid = new CuboidRegion(mine.getMinimumPoint(), mine.getMaximumPoint());
//        setCuboidRegion(stupidWallCuboid);
//        setBedrockCubeRegion(stupidWallCuboid);
//
////        setBedrockCubeRegion(stupidWallCuboid);
//    }
}

//    public void teleport(Player player) {
//        player.teleport(location);
//    }

