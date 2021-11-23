package me.untouchedodin0.plugin.mines;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.factory.PasteFactory;
import me.untouchedodin0.plugin.mines.data.WorldEditMineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Utils;
import me.untouchedodin0.plugin.util.worldedit.Adapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import redempt.redlib.blockdata.DataBlock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorldEditMine {

    public static final List<BlockVector3> EXPANSION_VECTORS = List.of(BlockVector3.UNIT_X, BlockVector3.UNIT_MINUS_X,
                                                                       BlockVector3.UNIT_Z, BlockVector3.UNIT_MINUS_Z);
    public static final BlockVector3 positiveY = BlockVector3.UNIT_Y;

    final Utils utils;
    private final PrivateMines privateMines;
    private WorldEditMineType worldEditMineType;
    private UUID mineOwner;
    private CuboidRegion cuboidRegion;
    private Region region;
    private Location spawnLocation;
    private World world;
    private Location location;
    private Material material;
    private DataBlock dataBlock;
    private WorldEditMineData worldEditMineData;
    private boolean canExpand = true;

    public WorldEditMine(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.utils = new Utils(privateMines);
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

    public WorldEditMineType getWorldEditMineType() {
        return worldEditMineType;
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

    public Region getBedrockCubeRegion() {
        return region;
    }

    public void setBedrockCubeRegion(Region region) {
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

        final var fillType = utils.bukkitToBlockType(material);

        this.world = privateMines.getMineWorldManager().getMinesWorld();

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
        final BlockType air = utils.bukkitToBlockType(Material.AIR);

        int minX = worldEditMineData.getRegionMinX();
        int minY = worldEditMineData.getRegionMinY();
        int minZ = worldEditMineData.getRegionMinZ();

        int maxX = worldEditMineData.getRegionMaxX();
        int maxY = worldEditMineData.getRegionMaxY();
        int maxZ = worldEditMineData.getRegionMaxZ();

        BlockVector3 min = BlockVector3.at(minX, minY, minZ);
        BlockVector3 max = BlockVector3.at(maxX, maxY, maxZ);

        CuboidRegion cuboidRegion = new CuboidRegion(min, max);

        MineStorage mineStorage = privateMines.getMineStorage();

        if (world == null) {
            privateMines.getLogger().warning("Failed to delete the mine due to the world being null");
        }

        privateMines.getLogger().info("cuboidRegion: " + cuboidRegion);

        privateMines.getLogger().info("region: " + worldEditMineData.getRegion());

        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            privateMines.getLogger().info("delete session: " + session);
            session.setBlocks(cuboidRegion, utils.getBlockState(air));
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }

        mineStorage.removeWorldEditMine(getMineOwner());

//        Gson gson = new Gson();
//        String fileName = getMineOwner().toString() + ".json";
//        File minesDirectory = privateMines.getMinesDirectory();
//        File jsonFile = new File(minesDirectory, fileName);
//        Reader reader;
//        WorldEditMine worldEditMine;
//
//        try {
//            reader = Files.newBufferedReader(Paths.get(jsonFile.toURI()));
//            worldEditMine = gson.fromJson(reader, WorldEditMine.class);
//
//            privateMines.getLogger().info("delete reader: " + reader.toString());
//            privateMines.getLogger().info("delete worldEditMine: " + worldEditMine);
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }


//        int minX = getDataBlock().getInt("minX");
//        int minY = getDataBlock().getInt("minY");
//        int minZ = getDataBlock().getInt("minZ");
//
//        int maxX = getDataBlock().getInt("maxX");
//        int maXY = getDataBlock().getInt("maxY");
//        int maxZ = getDataBlock().getInt("maxZ");
//
//        BlockVector3 min = BlockVector3.at(minX, minY, minZ);
//        BlockVector3 max = BlockVector3.at(maxX, maXY, maxZ);
//
//        final var cuboidRegion = new CuboidRegion(min, max);
//
//        final var air = utils.bukkitToBlockType(Material.AIR);
//        final var dataBlock = getDataBlock();
//        final var cuboid = new CuboidRegion(min, max);
//
//
//        // Creates edit session, sets the blocks and flushes it!
//        try (final var session = WorldEdit.getInstance()
//                .newEditSession(BukkitAdapter.adapt(world))) {
//            session.setBlocks(cuboid, utils.getBlockState(air));
////            session.setBlocks(region, utils.getBlockState(air));
//        } catch (MaxChangedBlocksException exception) {
//            exception.printStackTrace();
//        }
//        setCuboidRegion(null);
//        setRegion(null);
//        this.cuboidRegion = null;
//        this.region = null;
//        dataBlock.remove();
    }

    public void upgrade() {

        this.world = privateMines.getMineWorldManager().getMinesWorld();
        final BlockType air = utils.bukkitToBlockType(Material.AIR);
        final WorldEditMineType currentWorldEditMineType = getWorldEditMineType();
        final PasteFactory pasteFactory = new PasteFactory(privateMines);
        final Location location = getLocation();

        int minX = worldEditMineData.getRegionMinX();
        int minY = worldEditMineData.getRegionMinY();
        int minZ = worldEditMineData.getRegionMinZ();

        int maxX = worldEditMineData.getRegionMaxX();
        int maxY = worldEditMineData.getRegionMaxY();
        int maxZ = worldEditMineData.getRegionMaxZ();

        BlockVector3 min = BlockVector3.at(minX, minY, minZ);
        BlockVector3 max = BlockVector3.at(maxX, maxY, maxZ);

        CuboidRegion cuboidRegion = new CuboidRegion(min, max);
        Clipboard clipboard;
        List<Location> corners = new ArrayList<>();

        if (world == null) {
            privateMines.getLogger().warning("Failed to upgrade the mine due to the world being null");
        }

        privateMines.getLogger().info("upgrade cuboidRegion: " + cuboidRegion);
        privateMines.getLogger().info("upgrade region: " + worldEditMineData.getRegion());
        privateMines.getLogger().info("upgrade current world edit type: " + currentWorldEditMineType);

        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            privateMines.getLogger().info("upgrade session: " + session);
            session.setBlocks(cuboidRegion, utils.getBlockState(air));
        } catch (MaxChangedBlocksException exception) {
            exception.printStackTrace();
        }

        WorldEditMineType worldEditMineType = getWorldEditMineType();
        File schematicFile = worldEditMineType.getSchematicFile();
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        Player player = Bukkit.getPlayer(getMineOwner());

        privateMines.getLogger().info("upgrade worldEditMineType: " + worldEditMineType);
        privateMines.getLogger().info("upgrade schematicFile: " + schematicFile);
        privateMines.getLogger().info("upgrade clipboardFormat: " + clipboardFormat);


        if (clipboardFormat != null) {
            try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {
                clipboard = clipboardReader.read();
                Region region = pasteFactory.paste(clipboard, location);

                privateMines.getLogger().info("upgrade clipboard: " + clipboardFormat);
                privateMines.getLogger().info("upgrade region: " + clipboardFormat);

                region.iterator().forEachRemaining(blockVector3 -> {
                    if (world != null) {
                        Location bukkitLocation = utils.blockVector3toBukkit(world, blockVector3);
                        Material bukkitMaterial = bukkitLocation.getBlock().getType();

                        if (bukkitMaterial == Material.CHEST) {
                            this.spawnLocation = utils.blockVector3toBukkit(world, blockVector3);
                            privateMines.getLogger().info("upgrade spawnLocation: " + spawnLocation);
                        } else if (bukkitMaterial == Material.POWERED_RAIL) {
                            corners.add(bukkitLocation);
                            privateMines.getLogger().info("upgrade corners: " + corners);
                        }
                    }
                });

                // Gets the corner locations
                Location corner1 = corners.get(0);
                Location corner2 = corners.get(1);

                privateMines.getLogger().info("upgrade corner1: " + corner1);
                privateMines.getLogger().info("upgrade corner2: " + corner2);


                // Makes the block vector 3 corner locations

                BlockVector3 blockVectorCorner1 = BlockVector3.at(
                        corner1.getBlockX(),
                        corner1.getBlockY(),
                        corner1.getBlockZ()
                );

                BlockVector3 blockVectorCorner2 = BlockVector3.at(
                        corner2.getBlockX(),
                        corner2.getBlockY(),
                        corner2.getBlockZ()
                );

                privateMines.getLogger().info("upgrade blockVectorCorner1: " + blockVectorCorner1);
                privateMines.getLogger().info("upgrade blockVectorCorner2: " + blockVectorCorner2);


                // Makes the cuboid region to fill with blocks

                CuboidRegion fillCuboidRegion = new CuboidRegion(blockVectorCorner1, blockVectorCorner2);
                setCuboidRegion(fillCuboidRegion);

                privateMines.getLogger().info("upgrade fillCuboidRegion: " + blockVectorCorner2);
                privateMines.getLogger().info("upgrade getCuboidRegion(): " + getCuboidRegion());
                spawnLocation.getBlock().setType(Material.AIR);
                reset();
                if (player != null) {
                    teleport(player);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private BlockVector3[] expansionVectors(final int amount) {
        return EXPANSION_VECTORS.stream().map(it -> it.multiply(amount)).toArray(BlockVector3[]::new);
    }

    public BlockVector3[] divideVectors(final int amount) {
        return EXPANSION_VECTORS.stream().map(it -> it.divide(amount)).toArray(BlockVector3[]::new);
    }

    public boolean canExpand(final int amount) {
        this.world = privateMines.getMineWorldManager().getMinesWorld();
        final var mine = getCuboidRegion();

        mine.expand(expansionVectors(amount));
        CuboidRegion cuboidRegion = CuboidRegion.makeCuboid(mine);

        cuboidRegion.expand(expansionVectors(1));

        cuboidRegion.forEach(blockVector3 -> {
            Location location = utils.blockVector3toBukkit(world, blockVector3);
            if (location.getBlock().getType().equals(Material.OBSIDIAN)) {
                privateMines.getLogger().info("Found obsidian at " + location);
                canExpand = false;
            } else {
                canExpand = true;
            }
        });

        // for here + 1; < amount
        // if block is expected theme
        // return -1;
        // else
        // return amount - expected theme

        // expand (returned amount) -> update theme -> expand the rest

        return canExpand;
//        return -1;
    }

    public void expand(final int amount) {

        this.world = privateMines.getMineWorldManager().getMinesWorld();
        boolean canExpand = canExpand(amount);

        if (world == null) {
            privateMines.getLogger().warning("Failed to expand the mine due to the world being null!");
        }

        if (!canExpand) {
            privateMines.getLogger().info("The private mine can't expand anymore!");
        } else {
            final var fillType = BlockTypes.DIAMOND_BLOCK;
            final var wallType = BlockTypes.BEDROCK;

            if (fillType == null || wallType == null) return;

            final var mine = getCuboidRegion();
            final var walls = getCuboidRegion();

            mine.expand(expansionVectors(amount));
            walls.expand(expansionVectors(amount));

            try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
                session.setBlocks(mine, fillType.getDefaultState());
                session.setBlocks(Adapter.walls(walls), wallType.getDefaultState());
            } catch (MaxChangedBlocksException exception) {
                exception.printStackTrace();
            }

            mine.contract(expansionVectors(amount));
            setCuboidRegion(mine);
        }
    }


//    public void expand(final int amount) {
//        final var fillType = BlockTypes.DIAMOND_BLOCK;
//        final var wallType = BlockTypes.BEDROCK;
//        final var min = getCuboidRegion().getMinimumPoint();
//        final var max = getCuboidRegion().getMaximumPoint();
//
//        if (fillType == null || wallType == null) {
//            return;
//        }
//
//        final var mine = getCuboidRegion(); //Adapter.adapt(getCuboidRegion());
//        privateMines.getLogger().info("expand mine cuboid : " + mine);
//
//        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
//            mine.expand(expansionVectors(amount));
//            setCuboidRegion(null);
//            setCuboidRegion(mine);
////            this.cuboidRegion = mine;
//
//            session.setBlocks(mine, fillType.getDefaultState());
//            session.setBlocks(Adapter.walls(mine), wallType.getDefaultState());
//
//        } catch (MaxChangedBlocksException ex) {
//            ex.printStackTrace();
//        }
//
//        privateMines.getLogger().info("expand min: " + min);
//        privateMines.getLogger().info("expand max: " + max);
//
//        final var stupidWallCuboid = Adapter.walls(mine);
//
////        final var stupidWallCuboid = new CuboidRegion(BukkitAdapter.adapt(world, mine.getMinimumPoint()),
////                BukkitAdapter.adapt(world, mine.getMaximumPoint()));
//
//        privateMines.getLogger().info("expand mine min: " + mine.getMinimumPoint());
//        privateMines.getLogger().info("expand mine max: " + mine.getMaximumPoint());
//
////        privateMines.getLogger().info("expand stupidWallCuboid: " + stupidWallCuboid);
//
//        setBedrockCubeRegion(stupidWallCuboid);
//    }

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

