/*
MIT License

Copyright (c) 2021 - 2022 Kyle Hicks

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package me.untouchedodin0.plugin.mines;

import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
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
import com.sk89q.worldedit.world.block.BlockTypes;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Utils;
import me.untouchedodin0.plugin.util.worldedit.Adapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.blockdata.DataBlock;
import redempt.redlib.misc.Task;
import redempt.redlib.misc.WeightedRandom;
import redempt.redlib.multiblock.Structure;
import redempt.redlib.region.CuboidRegion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class Mine {

    public static final List<BlockVector3> EXPANSION_VECTORS = List.of(BlockVector3.UNIT_X, BlockVector3.UNIT_MINUS_X,
                                                                       BlockVector3.UNIT_Z, BlockVector3.UNIT_MINUS_Z);

    // https://paste.bristermitten.me/xevumodazo.java

    /*
        mineType: The type of mine (of which where the blocks come from etc..)
        mineLocation: Where the blocks are actual mine is within the world
        cuboidRegion: The mining area of the actual mine, (could this go to MineType?)
     */

    private final PrivateMines privateMines;
    private final Material airMaterial = XMaterial.AIR.parseMaterial();
    private final Utils utils;
    Optional<IWrappedRegion> iWrappedRegion;
    BlockVector3 cornerVector1;
    BlockVector3 cornerVector2;
    Material expandMaterial;
    private MineType mineType;
    private Location mineLocation;
    private Location spawnLocation;
    private Location npcLocation;
    private Location corner1;
    private Location corner2;
    private CuboidRegion cuboidRegion;
    private CuboidRegion bedrockCubeRegion;
    private UUID mineOwner;
    private Structure structure;
    private WeightedRandom<Material> weightedRandom;
    private boolean debugMode;
    private boolean isAutoResetting;
    private boolean isOpen;
    private Task resetTask;
    private World world;
    private EditSession editSession;
    private com.sk89q.worldedit.regions.CuboidRegion worldEditCube;

    public Mine(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.utils = new Utils(privateMines);
    }

    /**
     * @return MineData - The mine data for the players mine
     */

    public MineType getMineType() {
        return mineType;
    }

    /**
     * @param mineType - The mine data to be set for the Mine
     */

    public void setMineType(MineType mineType) {
        this.mineType = mineType;
    }

    /**
     * @return Location - The location of where the mine is in the world
     */

    public Location getMineLocation() {
        return mineLocation;
    }

    /**
     * @param mineLocation - The location of where the mine should go
     */

    public void setMineLocation(Location mineLocation) {
        this.mineLocation = mineLocation;
    }

    /**
     * @return Location - The location of where the spawn location is in the world
     */

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    /**
     * @param spawnLocation - The location where the player should spawn
     */

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    /**
     * @return Location - The location of where the npc location is in the world
     */

    public Location getNpcLocation() {
        return npcLocation;
    }

    /**
     * @param npcLocation - The location of where the npc should go
     */

    public void setNpcLocation(Location npcLocation) {
        this.npcLocation = npcLocation;
    }

    /**
     * @return Location - The corner 1 location
     */

    public Location getCorner1() {
        return corner1;
    }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    /**
     * @return Location - The corner 2 location
     */

    public Location getCorner2() {
        return corner2;
    }

    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }

    /**
     * @return CuboidRegion - The cuboid region of the mining area
     */

    public CuboidRegion getCuboidRegion() {
        return cuboidRegion;
    }

    /**
     * @param cuboidRegion - The cuboid region for the mining region
     */

    public void setCuboidRegion(CuboidRegion cuboidRegion) {
        this.cuboidRegion = cuboidRegion;
    }

    public void setWorldEditCube(com.sk89q.worldedit.regions.CuboidRegion cuboidRegion) {
        this.worldEditCube = cuboidRegion;
    }

    public com.sk89q.worldedit.regions.CuboidRegion getWorldEditCube() {
        return worldEditCube;
    }

    /**
     * @param cuboidRegion - The cuboid region for the bedrock area
     */

    public void setBedrockCubeRegion(CuboidRegion cuboidRegion) {
        this.bedrockCubeRegion = cuboidRegion;
    }

    /**
     * @return UUID - The UUID of the mine owner.
     */

    public UUID getMineOwner() {
        return mineOwner;
    }

    /**
     * @param mineOwner - The UUID of the new mine owner
     */

    public void setMineOwner(UUID mineOwner) {
        this.mineOwner = mineOwner;
    }

    /**
     * @return Structure - Gets the mine's structure.
     */

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    @SuppressWarnings("unused")
    public WeightedRandom<Material> getWeightedRandom() {
        return weightedRandom;
    }

    public void setWeightedRandom(WeightedRandom<Material> weightedRandom) {
        this.weightedRandom = weightedRandom;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public World getWorld() {
        return world;
    }

    /*
        The method for creating the structures into the world
     */


//    public void build() {
//
//
//        // Simple check to make sure the type isn't null
//
//        if (mineType == null) {
//            Bukkit.getLogger().info("Failed to build structure due to the mine type being null!");
//        }
//
//        // Get the main class
//        PrivateMines privateMines = PrivateMines.getPlugin(PrivateMines.class);
//
//        // Set the debugMode field to true or false
//        this.debugMode = privateMines.isDebugMode();
//
//        // Set to use world edit or not, makes it faster
//        boolean useWorldEdit = privateMines.isWorldEditEnabled();
//
//        // Initialise the util class
//        Utils utils = new Utils(privateMines);
//
//        // Print out more debug stuff if the mode is enabled
//        if (debugMode) {
//            privateMines.getLogger().info("MultiBlockStructure: " + mineType.getMultiBlockStructure());
//            privateMines.getLogger().info("Location " + mineLocation);
//            privateMines.getLogger().info("using worldedit?: " + useWorldEdit);
//        }
//
//        String userUUID = getMineOwner().toString();
//        String regionName = String.format("mine-%s", userUUID);
//
//        World world = mineLocation.getWorld();
//
//        // Paste the structure using the world edit
//
//        if (useWorldEdit) {
//            if (world != null) {
//                this.editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));
//
//                File file = mineType.getFile();
////                pasteSchematic(file, mineLocation);
//
//
////                mineType.getMultiBlockStructure().forEachBlock(mineLocation, blockState -> {
////                    Location location = blockState.getLocation();
////
////                    try {
////                        editSession.setBlock(BlockVector3.at(
////                                                     location.getBlockX(),
////                                                     location.getBlockY(),
////                                                     location.getBlockZ()),
////                                             BukkitAdapter.adapt(blockState.getBlockData()),
////                                             EditSession.Stage.BEFORE_HISTORY);
////                    } catch (WorldEditException e) {
////                        e.printStackTrace();
////                    }
////                });
////                editSession.close();
//            }
//
//            // Assume the structure is at the location
//            this.structure = mineType.getMultiBlockStructure().assumeAt(mineLocation);
//        } else {
//
//            // Paste the structure using redlib
//            this.structure = mineType.getMultiBlockStructure().build(mineLocation);
//        }
//
//        // Simple check to make sure the structure isn't null
//
//        if (this.structure == null) {
//            Bukkit.getLogger().info("Structure is null");
//        }
//
//        // Set the mines spawn and npc locations using the relative location from the mineType's
//
//        this.spawnLocation = utils.getRelative(structure, mineType.getSpawnLocation());
//        this.npcLocation = utils.getRelative(structure, mineType.getNpcLocation());
//        this.corner1 = utils.getRelative(structure, mineType.getCorner1());
//        this.corner2 = utils.getRelative(structure, mineType.getCorner2());
//        this.world = mineLocation.getWorld();
//
//        // Initialize the block manager
//
//        BlockDataManager blockDataManager = privateMines.getBlockDataManager();
//
//        // Initialize the data block
//
//        DataBlock dataBlock = blockDataManager.getDataBlock(mineLocation.getBlock());
//
//        /*
//            Create a cuboid region with the two corners
//            Expand the region (small fix for the mines not fully filling)
//            Set the mines cuboid region with the created region
//            set the spawn and npc location blocks to air
//            Finally set and save the values into the block data manager
//         */
//
//        CuboidRegion cuboidRegion = new CuboidRegion(corner1, corner2);
////        cuboidRegion.expand(1, 0, 1, 0, 1, 0);
//        setCuboidRegion(cuboidRegion);
//
//        final var mine = Adapter.adapt(getCuboidRegion());
//        final var stupidWallCuboid = new CuboidRegion(BukkitAdapter.adapt(world, mine.getMinimumPoint()),
//                                                      BukkitAdapter.adapt(world, mine.getMaximumPoint()));
//
//        int x1 = getCorner1().getBlockX();
//        int x2 = getCorner2().getBlockX();
//
//        int y1 = getCorner1().getBlockY();
//        int y2 = getCorner2().getBlockY();
//
//        int z1 = getCorner2().getBlockZ();
//        int z2 = getCorner2().getBlockZ();
//
//        BlockVector3 vector1 = BlockVector3.at(x1, y1, z1);
//        BlockVector3 vector2 = BlockVector3.at(x2, y2, z2);
//
//        this.cornerVector1 = vector1;
//        this.cornerVector2 = vector2;
//
//        this.worldEditCube = new com.sk89q.worldedit.regions.CuboidRegion(
//                BlockVector3.at(
//                        cuboidRegion.getStart().getBlockX(),
//                        cuboidRegion.getStart().getBlockY(),
//                        cuboidRegion.getStart().getBlockZ()),
//                BlockVector3.at(
//                        cuboidRegion.getEnd().getBlockX(),
//                        cuboidRegion.getEnd().getBlockY(),
//                        cuboidRegion.getEnd().getBlockZ()));
//        setWorldEditCube(worldEditCube);
//        setBedrockCubeRegion(cuboidRegion);
//
//        this.editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));
//
//        mine.expand(expansionVectors(1));
//        mine.contract(BlockVector3.UNIT_X, BlockVector3.UNIT_MINUS_X, BlockVector3.UNIT_MINUS_Y, BlockVector3.UNIT_Z, BlockVector3.UNIT_MINUS_Z);
//
//        iWrappedRegion = WorldGuardWrapper.getInstance().addCuboidRegion(regionName, cuboidRegion.getStart(), cuboidRegion.getEnd());
////        utils.setMineFlags(iWrappedRegion);
//
//        if (airMaterial != null) {
//            spawnLocation.getBlock().setType(airMaterial, false);
//            npcLocation.getBlock().setType(airMaterial, false);
//
//            dataBlock.set("owner", getMineOwner());
//            dataBlock.set("type", getMineType().getName());
////            dataBlock.set("weightedRandom", getMineType().getMaterials());
//            blockDataManager.save();
//        }
//    }


    public void pasteSchematic(File file, Location location) {
        privateMines.getLogger().info("pasteSchematic file: " + file);
        privateMines.getLogger().info("pasteSchematic location: " + location);

        if (file == null) {
            privateMines.getLogger().warning("Failed to paste schematic, file missing!");
            return;
        } else if (location == null) {
            privateMines.getLogger().warning("Failed to paste schematic, location missing!");
            return;
        }

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Clipboard clipboard;
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);
        BlockVector3 pasteLocation = BlockVector3.at(x, y, z);

        if (clipboardFormat != null) {
            try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(file))) {
                clipboard = clipboardReader.read();

                Operation operation =
                        new ClipboardHolder(clipboard)
                                .createPaste(editSession)
                                .to(pasteLocation)
                                .build();
                Operations.complete(operation);
            } catch (IOException | WorldEditException e) {
                e.printStackTrace();
            }
        }
    }

    public void teleportPlayer(Player player) {
        if (!isAutoResetting) {
            this.isAutoResetting = true;
        }
        player.teleport(spawnLocation);
    }

    public void delete() {
        PrivateMines privateMines = PrivateMines.getPlugin(PrivateMines.class);
        final var air = BlockTypes.AIR;

        if (mineType == null) {
            privateMines.getLogger().info("Failed to delete the mine due to mine data being null!");
        }

        if (air == null) return;

        MineStorage mineStorage = privateMines.getMineStorage();

        this.structure = getStructure();

        if (mineOwner != null) {
            mineStorage.removeMine(mineOwner);

            final var mine = Adapter.adapt(getCuboidRegion());

//            try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Objects.requireNonNull(world)))) {
//                session.setBlocks(mine, air.getDefaultState());
//            } catch (MaxChangedBlocksException exception) {
//                exception.printStackTrace();
//            }

//            if (airMaterial != null) {
//                structure.getRegion().forEachBlock(block -> block.setType(airMaterial, false));
//                bedrockCubeRegion.forEachBlock(block -> block.setType(airMaterial, false));
//            }
        }
        cancelResetTask();
        this.cuboidRegion = null;
        this.bedrockCubeRegion = null;
        this.worldEditCube = null;
    }

    // Nice little reset system for filling in the cuboid region using the mine type's weighted random.
    public void reset() {

        final var emerald = BlockTypes.EMERALD_BLOCK;
        final var worldEditCube = Adapter.adapt(cuboidRegion);
        final var mineArea = Adapter.adapt(getCuboidRegion());

        //Bukkit.broadcastMessage("corner1: " + worldEditCube.getPos1());
        //Bukkit.broadcastMessage("corner2: " + worldEditCube.getPos2());


        /*
                final var mine = Adapter.adapt(getCuboidRegion());

        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            mine.expand(expansionVectors(amount));

            session.setBlocks(mine, fillType.getDefaultState());
            session.setBlocks(Adapter.walls(mine), wallType.getDefaultState());
         */

        if (mineLocation == null) return;
        if (mineType == null) {
            privateMines.getLogger().warning("Failed to reset mine due to the type being null!");
            return;
        }

        if (mineType.getWeightedRandom().getWeights().isEmpty()) {
            privateMines.getLogger().warning("There were no materials in the weighted random!");
            return;
        }

//        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Objects.requireNonNull(world)))) {
//            if (emerald != null) {
////                mineArea.contract(BlockVector3.UNIT_X, BlockVector3.UNIT_MINUS_X, BlockVector3.UNIT_MINUS_Y, BlockVector3.UNIT_Z, BlockVector3.UNIT_MINUS_Z);
//                session.setBlocks(mineArea, emerald.getDefaultState());
//            }
//        } catch (MaxChangedBlocksException exception) {
//            exception.printStackTrace();
//        }

//        Material material = mineType.getWeightedRandom().roll();
//        String blockType = Objects.requireNonNull(BlockType.REGISTRY.get(material.name())).getId().toLowerCase();
//
//        cuboidRegion.forEachBlock(block -> {
//            privateMines.getWorldEditUtils().setBlock(block.getLocation(), blockType.toLowerCase());
//        });

//        privateMines.getWorldEditUtils().setBlocks(getCuboidRegion(), blockType);

//        CuboidRegion cuboidRegion = getCuboidRegion();
//        cuboidRegion.forEachBlock(block -> block.setType(mineType.getWeightedRandom().roll(), false));
        teleportPlayer(Bukkit.getPlayer(getMineOwner()));
    }

//    public void reset(com.sk89q.worldedit.regions.CuboidRegion cuboidRegion) {
//        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Objects.requireNonNull(world)))) {
//            final var emerald = BlockTypes.REDSTONE_BLOCK;
//            session.setBlocks(cuboidRegion, emerald.getDefaultState());
//        } catch (MaxChangedBlocksException exception) {
//            exception.printStackTrace();
//        }
//    }

    public void startAutoResetTask() {
        int time;
        long intervalTime;

        if (mineType != null) {
            time = mineType.getResetTime();
            intervalTime = utils.minutesToBukkit(time);

            //                CuboidRegion cuboidRegion = getCuboidRegion();
            //                cuboidRegion.forEachBlock(block -> block.setType(mineType.getWeightedRandom().roll(), false));
            this.resetTask = Task.syncRepeating(privateMines, this::reset, 0L, intervalTime);
        }
    }

    public void cancelResetTask() {
        this.resetTask.cancel();
    }


    /*
        This system upgrades the private mines onto the next MineData and
        replaces the structure at the location

        PLEASE DO NOT TOUCH, IT WORKS!
     */

    public void upgrade() {
        PrivateMines privateMines = PrivateMines.getPlugin(PrivateMines.class);
        Utils utils = new Utils(privateMines);
        MineFactory mineFactory = privateMines.getMineFactory();
        MineStorage mineStorage = privateMines.getMineStorage();
        MineType upgradeType = utils.getNextMineType(this);
        Player player = Bukkit.getPlayer(mineOwner);
        String regionName = String.format("mine-%s", mineOwner);

        if (debugMode) {
            Bukkit.getLogger().info("upgradeType: " + upgradeType);
            Bukkit.getLogger().info("upgradeType Name: " + upgradeType.getName());
        }

        if (privateMines.isAtLastMineType(mineType)) {
            Bukkit.getLogger().info("Can't upgrade anymore, at highest!");
            return;
        }

        if (player != null) {
            Structure structure = getStructure();
            if (airMaterial != null) {
                Mine mine = mineStorage.getMine(mineOwner);
                mine.cancelResetTask();
                structure.getRegion().forEachBlock(block -> block.setType(airMaterial, false));
                this.cuboidRegion = null;
            }

            setMineType(upgradeType);
            Mine mine = mineFactory.createMine(player, mineLocation, upgradeType);
            mine.teleportPlayer(player);
            mineStorage.replaceMine(player.getUniqueId(), mine);
            WorldGuardWrapper.getInstance().removeRegion(getWorld(), regionName);
            Location start = mine.getCorner1();
            Location end = mine.getCorner2();
            iWrappedRegion = WorldGuardWrapper.getInstance().addCuboidRegion(regionName, start, end);
//            utils.setMineFlags(iWrappedRegion);
        }
    }

    private BlockVector3[] expansionVectors(final int amount) {
        return EXPANSION_VECTORS.stream().map(it -> it.multiply(amount)).toArray(BlockVector3[]::new);
    }


    public int canExpand(final int amount) {

        // for here + 1; < amount
        // if block is expected theme
        // return -1;
        // else
        // return amount - expected theme


        // expand (returned amount) -> update theme -> expand the rest

        final var mine = Adapter.adapt(getCuboidRegion());

        expansionVectors(1);

        return -1;
    }

    public void expand(final int amount) {
        final var fillType = BlockTypes.DIAMOND_BLOCK;
        final var wallType = BlockTypes.BEDROCK;

        if (fillType == null || wallType == null) {
            return;
        }

        final var mine = Adapter.adapt(getCuboidRegion());

//        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
//            mine.expand(expansionVectors(amount));
//
//            session.setBlocks(mine, fillType.getDefaultState());
//            session.setBlocks(Adapter.walls(mine), wallType.getDefaultState());
//        } catch (MaxChangedBlocksException ex) {
//            ex.printStackTrace();
//        }

        final var stupidWallCuboid = new CuboidRegion(BukkitAdapter.adapt(world, mine.getMinimumPoint()),
                                                      BukkitAdapter.adapt(world, mine.getMaximumPoint()));

        final var test = new CuboidRegion(BukkitAdapter.adapt(world, mine.getMinimumPoint()),
                                          BukkitAdapter.adapt(world, mine.getMaximumPoint()));
        setCuboidRegion(stupidWallCuboid);
        setBedrockCubeRegion(stupidWallCuboid);
        setWorldEditCube(mine);
//        setBedrockCubeRegion(stupidWallCuboid);
    }

    public void expandMine(int amount) {
        Player player = Bukkit.getPlayer(mineOwner);

//        if (XMaterial.matchXMaterial(privateMines.getUpgradeMaterial()).isPresent()) {
//            expandMaterial = XMaterial.matchXMaterial(privateMines.getUpgradeMaterial()).get().parseMaterial();
//        }

        CuboidRegion mineCube = getCuboidRegion();
        CuboidRegion bedrockCube;

        bedrockCube = mineCube.clone();
//        this.editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));


        Material outsideStart;
        Material outsideEnd;

        outsideStart = bedrockCube.getStart().getBlock().getRelative(BlockFace.NORTH).getType();
        outsideEnd = bedrockCube.getEnd().getBlock().getRelative(BlockFace.SOUTH).getType();

        if (outsideStart.equals(expandMaterial) || outsideEnd.equals(expandMaterial)) {
            //Bukkit.broadcastMessage("Upgrading the mine...");
        } else {
            mineCube.expand(amount, amount, 0, 0, amount, amount); // We no touch Y levels!
            bedrockCube.expand(amount, amount, 0, 0, amount, amount); // We no touch Y levels!
        }

        Location mineStart = mineCube.getStart();
        Location mineEnd = mineCube.getEnd();

        BlockVector3 mineCorner1 = BlockVector3.at(mineStart.getBlockX(), mineStart.getBlockY(), mineStart.getBlockZ());
        BlockVector3 mineCorner2 = BlockVector3.at(mineEnd.getBlockX(), mineEnd.getBlockY(), mineEnd.getBlockZ());

        Location bukkitCorner1 = new Location(mineStart.getWorld(), mineStart.getBlockX(), mineStart.getBlockY(), mineStart.getBlockZ());
        Location bukkitCorner2 = new Location(mineEnd.getWorld(), mineEnd.getBlockX(), mineEnd.getBlockY(), mineEnd.getBlockZ());

        final var mine = Adapter.adapt(mineCube);
        final var wall = Adapter.adapt(bedrockCube);

        mine.expand(expansionVectors(amount));
        wall.expand(expansionVectors(amount));

        com.sk89q.worldedit.regions.CuboidRegion cube = new com.sk89q.worldedit.regions.CuboidRegion(mineCorner1, mineCorner2);
        try {
            editSession.setBlocks(Adapter.walls(wall), BlockTypes.BEDROCK.getDefaultState());
//            editSession.makeCuboidWalls(wallsCube, (Pattern) type);
//            editSession.makeCuboidFaces(wallsCube, (Pattern) type);

        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }

//        editSession.flushSession();

        editSession.close();

        mineCube.getFace(BlockFace.DOWN).forEachBlock(block -> {
            block.setType(Material.BEDROCK, false);
        });

//        floor.getFace(BlockFace.DOWN).forEachBlock(block -> {
//            Bukkit.getLogger().info(block.getLocation() + " : " + block.getType());
//        });

//        bedrockCube.getFace(BlockFace.UP).forEachBlock(block -> block.setType(Material.AIR, false));

        int minX = cube.getMinimumPoint().getBlockX();
        int minY = cube.getMinimumPoint().getBlockY();
        int minZ = cube.getMinimumPoint().getBlockZ();

        int maxX = cube.getMaximumPoint().getBlockX();
        int maxY = cube.getMaximumPoint().getBlockY();
        int maxZ = cube.getMaximumPoint().getBlockZ();

        Location min = new Location(bukkitCorner1.getWorld(), minX, minY, minZ);
        Location max = new Location(bukkitCorner2.getWorld(), maxX, maxY, maxZ);

        Location minCopy = new Location(bukkitCorner1.getWorld(), minX, minY, minZ);
        Location maxCopy = new Location(bukkitCorner2.getWorld(), maxX, maxY + 1, maxZ);

        CuboidRegion mineRegion = new CuboidRegion(min, max);
        CuboidRegion fillRegion = new CuboidRegion(minCopy, maxCopy);

        setCuboidRegion(mineRegion);
        setBedrockCubeRegion(bedrockCube);
    }
}
