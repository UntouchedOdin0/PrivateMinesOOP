package me.untouchedodin0.plugin.mines;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
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
import org.bukkit.entity.Player;
import redempt.redlib.blockdata.DataBlock;
import redempt.redlib.misc.Task;

import java.io.*;
import java.util.*;

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

    private Map<Material, Double> materials = new HashMap<>();

    private DataBlock dataBlock;
    private WorldEditMineData worldEditMineData;

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

    public Map<Material, Double> getMaterials() {
        return materials;
    }

    public void setMaterials(Map<Material, Double> materials) {
        this.materials = materials;
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

//    public List<BlockState> getMultipleFillState() {
//        Material[] materials = getMaterials();
//        List<BlockState> blockStates = new ArrayList<>();
//
//        for (Material material : materials) {
//            BlockType blockType = utils.bukkitToBlockType(material);
//            BlockState blockState = utils.getBlockState(blockType);
//            blockStates.add(blockState);
//        }
//        return blockStates;
//    }

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

    /*
    public void reset() {

        final var fillType = utils.bukkitToBlockType(material);

        final RandomPattern randomPattern = new RandomPattern();
        final Map<Material, Double> materials = worldEditMineData.getMaterials();

        privateMines.getLogger().info("reset debug");
        privateMines.getLogger().info("materials: " + materials);
        privateMines.getLogger().info("randomPattern: " + randomPattern);

//        materials.forEach((material, percentage) -> {
//            Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
//            randomPattern.add(pattern, percentage);
//        });

        this.world = privateMines.getMineWorldManager().getMinesWorld();

        if (world == null) {
            privateMines.getLogger().warning("Failed to reset due to the mine world being null");
        }

        // Makes sure everything isn't null
        if (cuboidRegion != null && fillType != null) {

            // Creates edit session, sets the blocks and flushes it!
            try (final var session = WorldEdit.getInstance()
                    .newEditSession(BukkitAdapter.adapt(world))) {
                session.setBlocks(getCuboidRegion(), (Pattern) fillType);
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            }
        }
    }
     */


//    public void reset() {
//
//        final var fillType = utils.bukkitToBlockType(material);
//        Map<Material, Double> map = new HashMap<>();
//        final RandomPattern pattern = new RandomPattern();
//
//        worldEditMineData.getMaterials().forEach((material1, aDouble) -> {
//            privateMines.getLogger().info("adding material " + material1 + " with percentage " + aDouble);
//            map.put(material1, aDouble);
//        });
//
//        privateMines.getLogger().info("reset map: " + map);
//
//        privateMines.getLogger().info("pattern: " + pattern);
//        try (final var session = WorldEdit.getInstance()
//                .newEditSession(BukkitAdapter.adapt(world))) {
//            map.forEach((itemStack, aDouble) -> {
//                Pattern pat = BukkitAdapter.adapt(itemStack.createBlockData());
//                privateMines.getLogger().info("pat: " + pat);
//                pattern.add(pat, aDouble);
//            });
//
//            privateMines.getLogger().info(pattern.toString());
//            session.setBlocks(getCuboidRegion(), (Pattern) fillType);
//        } catch (MaxChangedBlocksException e) {
//            e.printStackTrace();
//        }
//    }

    public void fill(Map<Material, Double> blocks) {
        try (final var session = WorldEdit.getInstance()
                .newEditSession(BukkitAdapter.adapt(world))) {
            final RandomPattern pattern = new RandomPattern();

            blocks.forEach((material1, aDouble) -> {
                Pattern pat = (Pattern) BukkitAdapter.adapt(material1.createBlockData());
                pattern.add(pattern, 1.0);
            });

            session.setBlocks(getCuboidRegion(), pattern);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    // Ignore the Casting 'BukkitAdapter.adapt(...)' to 'Pattern' is redundant warning it makes the code work lol
    public void reset() {

        final RandomPattern pattern = new RandomPattern();
        Map<Material, Double> mineDataMaterials = worldEditMineData.getMaterials();

        Map<Material, Double> materials = new HashMap<>(mineDataMaterials);

        privateMines.getLogger().info("reset mineDataMaterials: " + mineDataMaterials);

        materials.forEach((material1, aDouble) -> {
            privateMines.getLogger().info("blockData: " + material1.createBlockData());
            Pattern blockPattern = (Pattern) BukkitAdapter.adapt(material1.createBlockData());
            pattern.add(blockPattern, aDouble);
        });

        privateMines.getLogger().info("set blocks pattern: " + pattern);

        try (final var session = WorldEdit.getInstance()
                .newEditSession(BukkitAdapter.adapt(world))) {
            session.setBlocks(getCuboidRegion(), pattern);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
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

        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            session.setBlocks(cuboidRegion, utils.getBlockState(air));
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }

        mineStorage.removeWorldEditMine(getMineOwner());
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

        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            session.setBlocks(cuboidRegion, utils.getBlockState(air));
        } catch (MaxChangedBlocksException exception) {
            exception.printStackTrace();
        }

        WorldEditMineType worldEditMineType = getWorldEditMineType();
        File schematicFile = worldEditMineType.getSchematicFile();
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        Player player = Bukkit.getPlayer(getMineOwner());

        if (clipboardFormat != null) {
            try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {
                clipboard = clipboardReader.read();
                Region region = pasteFactory.paste(clipboard, location);

                region.iterator().forEachRemaining(blockVector3 -> {
                    if (world != null) {
                        Location bukkitLocation = utils.blockVector3toBukkit(world, blockVector3);
                        Material bukkitMaterial = bukkitLocation.getBlock().getType();

                        if (bukkitMaterial == Material.CHEST) {
                            this.spawnLocation = utils.blockVector3toBukkit(world, blockVector3);
                        } else if (bukkitMaterial == Material.POWERED_RAIL) {
                            corners.add(bukkitLocation);
                        }
                    }
                });

                // Gets the corner locations
                Location corner1 = corners.get(0);
                Location corner2 = corners.get(1);

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

                // Makes the cuboid region to fill with blocks

                CuboidRegion fillCuboidRegion = new CuboidRegion(blockVectorCorner1, blockVectorCorner2);
                setCuboidRegion(fillCuboidRegion);

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

    /*
      for here + 1; < amount
      if block is expected theme
      return -1;
      else
      return amount - expected theme

      expand (returned amount) -> update theme -> expand the rest

     return canExpand;
     return -1;
     */

    // WORKING, DON'T FUCK WITH THIS ANYMORE!
    public boolean canExpand(final int amount) {
        this.world = privateMines.getMineWorldManager().getMinesWorld();
        final var mine = getCuboidRegion();
        final int toCheck = 3 + amount;

        BlockVector3 min  = mine.getMinimumPoint();
        BlockVector3 max = mine.getMaximumPoint();
        CuboidRegion minMaxCuboid = new CuboidRegion(min, max);

        redempt.redlib.region.CuboidRegion cuboidRegion = utils.worldEditRegionToRedLibRegion(minMaxCuboid);
        cuboidRegion.expand(3, 0, 3, 0, 3, 0);

        return cuboidRegion.stream().noneMatch(block -> {
            return block.getType() == Material.OBSIDIAN;
        });
    }

    public void expand(final int amount) {

        this.world = privateMines.getMineWorldManager().getMinesWorld();
        boolean canExpand = canExpand(amount);
        MineStorage mineStorage = privateMines.getMineStorage();
        File minesDirectory = privateMines.getMinesDirectory();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String fileName = getMineOwner() + ".json";
        File jsonFile = new File(minesDirectory, fileName);

        if (world == null) {
            privateMines.getLogger().warning("Failed to expand the mine due to the world being null!");
        }

        if (!canExpand) {
            privateMines.getLogger().info("The private mine can't expand anymore!");
            Task.asyncDelayed(task -> {
                Bukkit.broadcastMessage("hi, i'm an upgrade thing!");
            }, utils.secondsToBukkit(5));
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
                mine.contract(expansionVectors(1));

                BlockVector3 min = mine.getMinimumPoint();
                BlockVector3 max = mine.getMaximumPoint();

                worldEditMineData.setMinX(min.getBlockX());
                worldEditMineData.setMinY(min.getBlockY());
                worldEditMineData.setMinZ(min.getBlockZ());

                worldEditMineData.setMaxX(max.getBlockX());
                worldEditMineData.setMaxY(max.getBlockY());
                worldEditMineData.setMaxZ(max.getBlockZ());
            } catch (MaxChangedBlocksException exception) {
                exception.printStackTrace();
            }
            worldEditMineData.setMineOwner(getMineOwner());
            worldEditMineData.setSpawnX(spawnLocation.getBlockX());
            worldEditMineData.setSpawnY(spawnLocation.getBlockY());
            worldEditMineData.setSpawnZ(spawnLocation.getBlockZ());

            try {
                Writer writer = new FileWriter(jsonFile);
                writer.write(gson.toJson(worldEditMineData));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            setCuboidRegion(null);
            setCuboidRegion(mine);
            privateMines.getMineStorage().replaceMine(getMineOwner(), this);
        }
        mineStorage.replaceMine(getMineOwner(), this);
    }

    // fuck sake idk if i should remove this or not, advice?

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

