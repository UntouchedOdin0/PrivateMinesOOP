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

package me.untouchedodin0.plugin.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.mines.WorldEditMine;
import me.untouchedodin0.plugin.mines.WorldEditMineType;
import me.untouchedodin0.plugin.mines.data.WorldEditMineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.blockdata.DataBlock;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.misc.LocationUtils;

import java.io.*;
import java.util.*;

public class MineFactory {

    private final boolean debugMode;
    PrivateMines privateMines;
    Utils utils;
    MineStorage mineStorage;
    MineFactory mineFactory;
    MineType defaultMineType;
    BlockDataManager blockDataManager;
    Location spawnLocation;

    public MineFactory(PrivateMines privateMines, BlockDataManager blockDataManager) {
        this.privateMines = privateMines;
        this.utils = privateMines.getUtils();
        this.mineStorage = privateMines.getMineStorage();
        this.mineFactory = privateMines.getMineFactory();
        this.defaultMineType = privateMines.getDefaultMineType();
        this.blockDataManager = blockDataManager;
        this.debugMode = privateMines.isDebugMode();
    }


    public Mine createMine(Player player, Location location) {
        if (defaultMineType == null) {
            privateMines.getLogger().warning("Failed to create mine due to defaultMineData being null");
        }

        final WorldGuardWrapper worldGuardWrapper = WorldGuardWrapper.getInstance();

        Block block = location.getBlock();
        String userUUID = player.getUniqueId().toString();
        Mine mine = new Mine(privateMines);
        mine.setMineOwner(player.getUniqueId());
        mine.setMineLocation(location);
        mine.setMineType(defaultMineType);
        mine.setWeightedRandom(defaultMineType.getWeightedRandom());
        mine.build();

        mineStorage.addMine(player.getUniqueId(), mine);

        DataBlock dataBlock = getDataBlock(block, player, location, mine);

        String regionName = String.format("mine-%s", userUUID);

//        IWrappedRegion mineRegion = WorldGuardWrapper.getInstance()
//                .addCuboidRegion(regionName, mine.getCorner1(), mine.getCorner2())
//                .orElseThrow(() -> new RuntimeException("Could not create the mine WorldGuard region!"));
//        mineRegion.getOwners().addPlayer(player.getUniqueId());

        MineType mineType = mine.getMineType();

        List<String> allowFlags = mineType.getAllowFlags();
        List<String> denyFlags = mineType.getDenyFlags();

//        Stream.of(worldGuardWrapper.getFlag("block-place", WrappedState.class),
//                        worldGuardWrapper.getFlag("mob-spawning", WrappedState.class)
//                ).filter(Optional::isPresent)
//                .map(Optional::get)
//                .forEach(flag -> mineRegion.setFlag(flag, WrappedState.DENY));
//
//        Stream.of(worldGuardWrapper.getFlag("block-break", WrappedState.class)
//                ).filter(Optional::isPresent)
//                .map(Optional::get)
//                .forEach(wrappedStateIWrappedFlag -> mineRegion.setFlag(wrappedStateIWrappedFlag, WrappedState.ALLOW));

        blockDataManager.save();
        mine.reset();
        mine.startAutoResetTask();
        return mine;
    }

    private DataBlock getDataBlock(Block block, Player player, Location location, Mine mine) {
        UUID ownerUUID = player.getUniqueId();
        String ownerUUIDString = ownerUUID.toString();

        DataBlock dataBlock = blockDataManager.getDataBlock(block);
        dataBlock.set("owner", ownerUUIDString);
        dataBlock.set("type", defaultMineType.getName());
        dataBlock.set("location", LocationUtils.toString(location));
        dataBlock.set("spawnLocation", LocationUtils.toString(mine.getSpawnLocation()));
        dataBlock.set("npcLocation", LocationUtils.toString(mine.getNpcLocation()));
        dataBlock.set("corner1", LocationUtils.toString(mine.getCorner1()));
        dataBlock.set("corner2", LocationUtils.toString(mine.getCorner2()));
        dataBlock.set("structure", mine.getStructure());
        return dataBlock;
    }

    private DataBlock getWorldEditDataBlock(Block block,
                                            Player player,
                                            Location location,
                                            WorldEditMine worldEditMine) {
        UUID ownerUUID = player.getUniqueId();
        String ownerUUIDString = ownerUUID.toString();
        String worldName = Objects.requireNonNull(location.getWorld()).getName();
        WorldEditMineType worldEditMineType = worldEditMine.getWorldEditMineType();

        DataBlock dataBlock = blockDataManager.getDataBlock(block);

        int corner1X = worldEditMine.getCuboidRegion().getMinimumPoint().getBlockX();
        int corner1Y = worldEditMine.getCuboidRegion().getMinimumPoint().getBlockY();
        int corner1Z = worldEditMine.getCuboidRegion().getMinimumPoint().getBlockZ();

        int corner2X = worldEditMine.getCuboidRegion().getMaximumPoint().getBlockX();
        int corner2Y = worldEditMine.getCuboidRegion().getMaximumPoint().getBlockY();
        int corner2Z = worldEditMine.getCuboidRegion().getMaximumPoint().getBlockZ();

        BlockVector3 minimumPoint = worldEditMine.getRegion().getMinimumPoint();
        BlockVector3 maximumPoint = worldEditMine.getRegion().getMaximumPoint();

        int spawnX = location.getBlockX();
        int spawnY = location.getBlockY();
        int spawnZ = location.getBlockZ();

        dataBlock.set("owner", ownerUUIDString);
        dataBlock.set("type", worldEditMineType.getName());
        dataBlock.set("corner1X", Integer.toString(corner1X));
        dataBlock.set("corner1Y", Integer.toString(corner1Y));
        dataBlock.set("corner1Z", Integer.toString(corner1Z));

        dataBlock.set("corner2X", Integer.toString(corner2X));
        dataBlock.set("corner2Y", Integer.toString(corner2Y));
        dataBlock.set("corner2Z", Integer.toString(corner2Z));

        dataBlock.set("spawnX", Integer.toString(spawnX));
        dataBlock.set("spawnY", Integer.toString(spawnY));
        dataBlock.set("spawnZ", Integer.toString(spawnZ));

        dataBlock.set("minX", minimumPoint.getBlockX());
        dataBlock.set("minY", minimumPoint.getBlockY());
        dataBlock.set("minZ", minimumPoint.getBlockZ());

        dataBlock.set("maxX", maximumPoint.getBlockX());
        dataBlock.set("maxY", maximumPoint.getBlockY());
        dataBlock.set("maxZ", maximumPoint.getBlockZ());

        dataBlock.set("world", location.getWorld());
        dataBlock.set("worldName", worldName);
        dataBlock.set("location", LocationUtils.toString(location));
        dataBlock.set("spawnLocation", LocationUtils.toString(spawnLocation));

        return dataBlock;
    }

    /**
     * @param player   - The target player to be given a mine
     * @param location - The spigot world location where to create the mine
     * @param mineType - The mine data such as the MultiBlockStructure and the Materials
     */

    public Mine createMine(Player player, Location location, MineType mineType) {
        if (mineType == null) {
            privateMines.getLogger().warning("Failed to create mine due to the minetype being null");
        } else {

            Utils utils = privateMines.getUtils();
            Mine mine = new Mine(privateMines);
            mine.setMineOwner(player.getUniqueId());
            mine.setMineLocation(location);
            mine.setMineType(mineType);
            mine.setWeightedRandom(mineType.getWeightedRandom());
            mine.build();

            Location corner1 = utils.getRelative(mine.getStructure(), mineType.getCorner1());
            Location corner2 = utils.getRelative(mine.getStructure(), mineType.getCorner2());

            Location spawnLocation = utils.getRelative(mine.getStructure(), mineType.getSpawnLocation());
            Location npcLocation = utils.getRelative(mine.getStructure(), mineType.getNpcLocation());

            mine.setCorner1(corner1);
            mine.setCorner2(corner2);
            mine.setSpawnLocation(spawnLocation);
            mine.setNpcLocation(npcLocation);

            mineStorage.addMine(player.getUniqueId(), mine);
            Block block = location.getBlock();
            DataBlock dataBlock = getDataBlock(block, player, location, mine);
            blockDataManager.save();
            mine.reset();
            mine.startAutoResetTask();

            if (debugMode) {
                privateMines.getLogger().info("createMine block: " + block);
                privateMines.getLogger().info("createMine dataBlock: " + dataBlock);
                privateMines.getLogger().info("createMine dataBlock getData: " + dataBlock.getData());
            }
            return mine;
        }
        return null;
    }

    public WorldEditMine createMine(Player player, Location location, WorldEditMineType worldEditMineType) {
        Clipboard clipboard;
        Utils utils = new Utils(privateMines);
        World world;
        UUID uuid = player.getUniqueId();
        List<Location> corners = new ArrayList<>();
        String recievedMine = "recievedMine";
        String toSend = Messages.msg(recievedMine);

        final var fillType = BlockTypes.DIAMOND_BLOCK;
        final var block = location.getBlock();

        if (worldEditMineType == null) {
            privateMines.getLogger().warning("Failed to create mine due to the worldedit mine type being null");
        } else {
            if (fillType == null) {
                privateMines.getLogger().warning("Failed to fill mine due to fillType being null");
            } else {

                File file = worldEditMineType.getSchematicFile();
                PasteFactory pasteFactory = new PasteFactory(privateMines);
                WorldEditMine worldEditMine = new WorldEditMine(privateMines);
                WorldEditMineData worldEditMineData = new WorldEditMineData();

                ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);

                if (clipboardFormat != null) {
                    try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(file))) {
                        clipboard = clipboardReader.read();
                        if (clipboard == null) {
                            privateMines.getLogger().warning("Clipboard was null");
                            return null;
                        }

                        world = location.getWorld();

                        // Pastes the schematic and loops the region finding blocks

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
                                corner1.getBlockZ());

                        BlockVector3 blockVectorCorner2 = BlockVector3.at(
                                corner2.getBlockX(),
                                corner2.getBlockY(),
                                corner2.getBlockZ());

                        // Makes the cuboid region to fill with blocks

                        CuboidRegion cuboidRegion = new CuboidRegion(blockVectorCorner1, blockVectorCorner2);

                        spawnLocation.getBlock().setType(Material.AIR, false);

                        // Set the cuboid region and mine and then reset it and then teleport the player

                        worldEditMine.setCuboidRegion(cuboidRegion);
                        worldEditMine.setRegion(region);
                        worldEditMine.setLocation(location);
                        worldEditMine.setSpawnLocation(spawnLocation);
                        worldEditMine.setWorld(spawnLocation.getWorld());
//                        worldEditMine.setMaterials(worldEditMineType.getMaterials());
//                        worldEditMine.setMaterial(worldEditMineType.getMaterial());
                        worldEditMine.setWorldEditMineType(worldEditMineType);
                        worldEditMine.setMineOwner(player.getUniqueId());

                        worldEditMineData.setMineOwner(uuid);
                        worldEditMineData.setSpawnX(spawnLocation.getBlockX());
                        worldEditMineData.setSpawnY(spawnLocation.getBlockY());
                        worldEditMineData.setSpawnZ(spawnLocation.getBlockZ());

                        worldEditMineData.setMinX(corner2.getBlockX());
                        worldEditMineData.setMinY(corner2.getBlockY());
                        worldEditMineData.setMinZ(corner2.getBlockZ());

                        worldEditMineData.setMaxX(corner1.getBlockX());
                        worldEditMineData.setMaxY(corner1.getBlockY());
                        worldEditMineData.setMaxZ(corner1.getBlockZ());

                        worldEditMineData.setRegionMinX(region.getMinimumPoint().getBlockX());
                        worldEditMineData.setRegionMinY(region.getMinimumPoint().getBlockY());
                        worldEditMineData.setRegionMinZ(region.getMinimumPoint().getBlockZ());

                        worldEditMineData.setRegionMaxX(region.getMaximumPoint().getBlockX());
                        worldEditMineData.setRegionMaxY(region.getMaximumPoint().getBlockY());
                        worldEditMineData.setRegionMaxZ(region.getMaximumPoint().getBlockZ());

                        if (world != null) {
                            worldEditMineData.setWorldName(world.getName());
                        }

                        if (worldEditMineType.getMaterial() != null) {
                            worldEditMineData.setMaterial(worldEditMineType.getMaterial().toString());
                        }

                        if (worldEditMineType.getName() != null) {
                            worldEditMineData.setMineType(worldEditMineType.getName());
                        }

                        worldEditMineData.setMaterials(worldEditMineType.getMaterials());

                        File minesDirectory = privateMines.getMinesDirectory();

                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        String fileName = player.getUniqueId() + ".json";

                        File jsonFile = new File(minesDirectory, fileName);

                        Writer fileWriter = new FileWriter(jsonFile);
                        fileWriter.write(gson.toJson(worldEditMineData));
                        fileWriter.close();

                        DataBlock dataBlock = getWorldEditDataBlock(block, player, location, worldEditMine);

                        worldEditMine.setWorldEditMineData(worldEditMineData);
                        worldEditMine.setDataBlock(dataBlock);
                        worldEditMine.reset();

                        // Tell the player it's been created and teleport them
                        player.sendMessage(toSend);
                        player.teleport(spawnLocation);
                        mineStorage.addWorldEditMine(uuid, worldEditMine);

                        IWrappedRegion iWrappedRegion = utils.createWorldGuardRegion(player, world, cuboidRegion);
                        utils.setMineFlags(java.util.Optional.ofNullable(iWrappedRegion));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}


