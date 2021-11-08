/*
MIT License

Copyright (c) 2021 Kyle Hicks

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
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Utils;
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

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class Mine {

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

    public void build() {


        // Simple check to make sure the type isn't null

        if (mineType == null) {
            Bukkit.getLogger().info("Failed to build structure due to the mine type being null!");
        }

        // Get the main class
        PrivateMines privateMines = PrivateMines.getPlugin(PrivateMines.class);

        // Set the debugMode field to true or false
        this.debugMode = privateMines.isDebugMode();

        // Set to use world edit or not, makes it faster
        boolean useWorldEdit = privateMines.isWorldEditEnabled();

        // Initialise the util class
        Utils utils = new Utils(privateMines);

        // Print out more debug stuff if the mode is enabled
        if (debugMode) {
            privateMines.getLogger().info("MultiBlockStructure: " + mineType.getMultiBlockStructure());
            privateMines.getLogger().info("Location " + mineLocation);
            privateMines.getLogger().info("using worldedit?: " + useWorldEdit);
        }

        String userUUID = getMineOwner().toString();
        String regionName = String.format("mine-%s", userUUID);

        World world = mineLocation.getWorld();

        // Paste the structure using the world edit

        if (useWorldEdit) {
            if (world != null) {

                this.editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));

                mineType.getMultiBlockStructure().forEachBlock(mineLocation, blockState -> {
                    Location location = blockState.getLocation();

                    try {
                        editSession.setBlock(BlockVector3.at(
                                        location.getBlockX(),
                                        location.getBlockY(),
                                        location.getBlockZ()),
                                BukkitAdapter.adapt(blockState.getBlockData()),
                                EditSession.Stage.BEFORE_HISTORY);
                    } catch (WorldEditException e) {
                        e.printStackTrace();
                    }
                });
                editSession.close();
            }

            // Assume the structure is at the location
            this.structure = mineType.getMultiBlockStructure().assumeAt(mineLocation);
        } else {

            // Paste the structure using redlib
            this.structure = mineType.getMultiBlockStructure().build(mineLocation);
        }

        // Simple check to make sure the structure isn't null

        if (this.structure == null) {
            Bukkit.getLogger().info("Structure is null");
        }

        // Set the mines spawn and npc locations using the relative location from the mineType's

        this.spawnLocation = utils.getRelative(structure, mineType.getSpawnLocation());
        this.npcLocation = utils.getRelative(structure, mineType.getNpcLocation());
        this.corner1 = utils.getRelative(structure, mineType.getCorner1());
        this.corner2 = utils.getRelative(structure, mineType.getCorner2());
        this.world = mineLocation.getWorld();

        // Initialize the block manager

        BlockDataManager blockDataManager = privateMines.getBlockDataManager();

        // Initialize the data block

        DataBlock dataBlock = blockDataManager.getDataBlock(mineLocation.getBlock());

        /*
            Create a cuboid region with the two corners
            Expand the region (small fix for the mines not fully filling)
            Set the mines cuboid region with the created region
            set the spawn and npc location blocks to air
            Finally set and save the values into the block data manager
         */

        CuboidRegion cuboidRegion = new CuboidRegion(corner1, corner2);
        cuboidRegion.expand(1, 0, 1, 0, 1, 0);
        setCuboidRegion(cuboidRegion);

        int x1 = getCorner1().getBlockX();
        int x2 = getCorner2().getBlockX();

        int y1 = getCorner1().getBlockY();
        int y2 = getCorner2().getBlockY();

        int z1 = getCorner2().getBlockZ();
        int z2 = getCorner2().getBlockZ();

        BlockVector3 vector1 = BlockVector3.at(x1, y1, z1);
        BlockVector3 vector2 = BlockVector3.at(x2, y2, z2);

        this.cornerVector1 = vector1;
        this.cornerVector2 = vector2;

        this.worldEditCube = new com.sk89q.worldedit.regions.CuboidRegion(
                BlockVector3.at(
                        cuboidRegion.getStart().getBlockX(),
                        cuboidRegion.getStart().getBlockY(),
                        cuboidRegion.getStart().getBlockZ()),
                BlockVector3.at(
                        cuboidRegion.getEnd().getBlockX(),
                        cuboidRegion.getEnd().getBlockY(),
                        cuboidRegion.getEnd().getBlockZ()));

        iWrappedRegion = WorldGuardWrapper.getInstance().addCuboidRegion(regionName, corner1, corner2);
        if (airMaterial != null) {
            spawnLocation.getBlock().setType(airMaterial, false);
            npcLocation.getBlock().setType(airMaterial, false);

            dataBlock.set("owner", getMineOwner());
            dataBlock.set("type", getMineType().getName());
//            dataBlock.set("weightedRandom", getMineType().getMaterials());
            blockDataManager.save();
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
        if (mineType == null) {
            privateMines.getLogger().info("Failed to delete the mine due to mine data being null!");
        }
        MineStorage mineStorage = privateMines.getMineStorage();

        this.structure = getStructure();

        if (mineOwner != null) {
            mineStorage.removeMine(mineOwner);
            if (airMaterial != null) {
                structure.getRegion().forEachBlock(block -> block.setType(airMaterial, false));
                bedrockCubeRegion.forEachBlock(block -> block.setType(airMaterial, false));
            }
        }
        cancelResetTask();
        this.cuboidRegion = null;
        this.bedrockCubeRegion = null;
    }

    // Nice little reset system for filling in the cuboid region using the mine type's weighted random.
    public void reset() {
        if (mineLocation == null) return;
        if (mineType == null) {
            privateMines.getLogger().warning("Failed to reset mine due to the type being null!");
            return;
        }

        if (mineType.getWeightedRandom().getWeights().isEmpty()) {
            privateMines.getLogger().warning("There were no materials in the weighted random!");
            return;
        }

//        Material material = mineType.getWeightedRandom().roll();
//        String blockType = Objects.requireNonNull(BlockType.REGISTRY.get(material.name())).getId().toLowerCase();
//
//        cuboidRegion.forEachBlock(block -> {
//            privateMines.getWorldEditUtils().setBlock(block.getLocation(), blockType.toLowerCase());
//        });

//        privateMines.getWorldEditUtils().setBlocks(getCuboidRegion(), blockType);

        CuboidRegion cuboidRegion = getCuboidRegion();
        cuboidRegion.forEachBlock(block -> block.setType(mineType.getWeightedRandom().roll(), false));
        teleportPlayer(Bukkit.getPlayer(getMineOwner()));
    }

    public void startAutoResetTask() {
        int time;
        long intervalTime;

        if (mineType != null) {
            time = mineType.getResetTime();
            intervalTime = utils.minutesToBukkit(time);

            this.resetTask = Task.syncRepeating(privateMines, () -> {
                CuboidRegion cuboidRegion = getCuboidRegion();
                cuboidRegion.forEachBlock(block -> block.setType(mineType.getWeightedRandom().roll(), false));
            }, 0L, intervalTime);
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
            utils.setMineFlags(iWrappedRegion);
        }
    }

    public void expandMine(int amount) {
        Player player = Bukkit.getPlayer(mineOwner);
        Material borderMaterial = Material.OBSIDIAN;

        CuboidRegion mineCube = getCuboidRegion();
        CuboidRegion bedrockCube;

        bedrockCube = mineCube.clone();
        this.editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));

        Material outsideStart;
        Material outsideEnd;

        outsideStart = bedrockCube.getStart().getBlock().getRelative(BlockFace.NORTH).getType();
        outsideEnd = bedrockCube.getEnd().getBlock().getRelative(BlockFace.SOUTH).getType();

        if (outsideStart.equals(Material.OBSIDIAN) || outsideEnd.equals(Material.OBSIDIAN)) {
            Bukkit.broadcastMessage("Upgrading the mine...");
        } else {
            Bukkit.broadcastMessage("Expanding the mine...");
            mineCube.expand(amount, amount, 0, 0, amount, amount); // We no touch Y levels!
            bedrockCube.expand(amount, amount, 0, 0, amount, amount); // We no touch Y levels!
        }

        Location mineStart = mineCube.getStart();
        Location mineEnd = mineCube.getEnd();

        Location bedrockStart = bedrockCube.getStart();
        Location bedrockEnd = bedrockCube.getEnd();

        BlockVector3 mineCorner1 = BlockVector3.at(mineStart.getBlockX(), mineStart.getBlockY(), mineStart.getBlockZ());
        BlockVector3 mineCorner2 = BlockVector3.at(mineEnd.getBlockX(), mineEnd.getBlockY(), mineEnd.getBlockZ());

        BlockVector3 bedrockCorner1 = BlockVector3.at(bedrockStart.getBlockX(), bedrockStart.getBlockY(), bedrockStart.getBlockZ());
        BlockVector3 bedrockCorner2 = BlockVector3.at(bedrockEnd.getBlockX(), bedrockEnd.getBlockY(), bedrockEnd.getBlockZ());

        Location bukkitCorner1 = new Location(mineStart.getWorld(), mineStart.getBlockX(), mineStart.getBlockY(), mineStart.getBlockZ());
        Location bukkitCorner2 = new Location(mineEnd.getWorld(), mineEnd.getBlockX(), mineEnd.getBlockY(), mineEnd.getBlockZ());

        Region cube = new com.sk89q.worldedit.regions.CuboidRegion(mineCorner1, mineCorner2);
        Region wallsCube = new com.sk89q.worldedit.regions.CuboidRegion(bedrockCorner1, bedrockCorner2);

        BlockType type = BlockType.REGISTRY.get(BlockTypes.BEDROCK.getId());
        BlockType type2 = BlockType.REGISTRY.get(BlockTypes.DIAMOND_BLOCK.getId());

        try {
            editSession.setBlocks(cube, (Pattern) type2);
            editSession.makeCuboidWalls(wallsCube, (Pattern) type);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }

//        editSession.flushSession();

        editSession.close();

        int minX = cube.getMinimumPoint().getBlockX();
        int minY = cube.getMinimumPoint().getBlockY();
        int minZ = cube.getMinimumPoint().getBlockZ();

        int maxX = cube.getMaximumPoint().getBlockX();
        int maxY = cube.getMaximumPoint().getBlockY();
        int maxZ = cube.getMaximumPoint().getBlockZ();

        Location min = new Location(bukkitCorner1.getWorld(), minX, minY, minZ);
        Location max = new Location(bukkitCorner2.getWorld(), maxX, maxY, maxZ);

        CuboidRegion mineRegion = new CuboidRegion(min, max);
        setCuboidRegion(mineRegion);
        setBedrockCubeRegion(bedrockCube);

        if (player != null) {
            player.sendMessage("Executed correctly...");
        }
    }
}
