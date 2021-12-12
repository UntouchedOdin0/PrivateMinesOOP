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

package me.untouchedodin0.plugin.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import me.clip.placeholderapi.PlaceholderAPI;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.mines.WorldEditMine;
import me.untouchedodin0.plugin.mines.WorldEditMineType;
import me.untouchedodin0.plugin.mines.data.WorldEditMineData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import org.jetbrains.annotations.NotNull;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.multiblock.Structure;
import redempt.redlib.region.CuboidRegion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Utils {

    private final PrivateMines privateMines;
    private final boolean debugMode;
    private final Pattern schemFilePattern = Pattern.compile("[a-zA-Z]\\.(schem)"); //Pattern.compile("(.*?)\\.(schem)");

    public Utils(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.debugMode = privateMines.isDebugMode();
    }

    public Location getRelative(Structure structure, int[] relative) {
        return structure
                .getRelative(relative[0], relative[1], relative[2])
                .getBlock()
                .getLocation();
    }

    public MineType getNextMineType(Mine mine) {
        MineType mineType = mine.getMineType();
        MineType upgradeMineType;
        boolean isAtLastMineType = privateMines.isAtLastMineType(mineType);
        if (isAtLastMineType) {
            privateMines.getLogger().info("Mine is already maxed out!");
            return mineType;
        }
        if (debugMode) {
            privateMines.getLogger().info("Current mine data Name: " + mineType.getName());
            upgradeMineType = privateMines.getNextMineType(mineType);
            privateMines.getLogger().info("Next mine data name: " + upgradeMineType.getName());
        }
        upgradeMineType = privateMines.getNextMineType(mineType);
        return upgradeMineType;
    }

    public WorldEditMineType getNextMineType(WorldEditMine worldEditMine) {
        WorldEditMineType worldEditMineType = worldEditMine.getWorldEditMineType();
        WorldEditMineType nextWorldEditMineType;
        boolean isAtLastType = privateMines.isAtLastMineType(worldEditMineType);
        if (isAtLastType) {
            privateMines.getLogger().info("Mine is already maxed out!");
            return worldEditMineType;
        }

        if (debugMode) {
            privateMines.getLogger().info("Current mine data Name: " + worldEditMineType.getName());
        }
        nextWorldEditMineType = privateMines.getNextMineType(worldEditMineType);
        return nextWorldEditMineType;
    }

    @SuppressWarnings("unused")
    public double getPercentageLeft(Mine mine) {
        CuboidRegion cuboidRegion = mine.getCuboidRegion();
        int totalBlocks = cuboidRegion.getBlockVolume();
        long airBlocks = cuboidRegion.stream().filter(Block::isEmpty).count();
        return (double) airBlocks * 100 / totalBlocks;
    }

    @SuppressWarnings("unused")
    public CuboidRegion getRegion(Clipboard clipboard) {
        final BlockVector3 minimumPoint = clipboard.getRegion().getMinimumPoint();
        final BlockVector3 maximumPoint = clipboard.getRegion().getMaximumPoint();

        final int minX = minimumPoint.getBlockX();
        final int maxX = maximumPoint.getBlockX();
        final int minY = minimumPoint.getBlockY();
        final int maxY = maximumPoint.getBlockY();
        final int minZ = minimumPoint.getBlockZ();
        final int maxZ = maximumPoint.getBlockZ();

        World world;
        Location start;
        Location end;

        if (clipboard.getRegion().getWorld() == null) {
            return null;
        }
        world = BukkitAdapter.adapt(Objects.requireNonNull(clipboard.getRegion().getWorld()));
        start = new Location(world, minX, minY, minZ);
        end = new Location(world, maxX, maxY, maxZ);

        return new CuboidRegion(start, end);
    }

    public Location blockVector3toBukkit(World world, BlockVector3 blockVector3) {
        Block block = world.getBlockAt(blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ());
        return block.getLocation();
    }

    public long minutesToBukkit(int minutes) {
        return 20L * 60L * minutes;
    }

    public IWrappedRegion createWorldGuardRegion(Player player, World world, com.sk89q.worldedit.regions.CuboidRegion cuboidRegion) {
        UUID uuid = player.getUniqueId();
        String regionName = String.format("mine-%s", uuid);

        IWrappedRegion iWrappedRegion = WorldGuardWrapper.getInstance().addCuboidRegion(
                regionName,
                blockVector3toBukkit(world,
                                     cuboidRegion.getMinimumPoint()),
                blockVector3toBukkit(world,
                                     cuboidRegion.getMaximumPoint())).orElseThrow(() -> new RuntimeException(""));
        privateMines.getLogger().info("Created worldguard region for " + player);
        privateMines.getLogger().info("cuboidRegion: " + cuboidRegion);
        privateMines.getLogger().info("iWrappedRegion: " + iWrappedRegion);
        return iWrappedRegion;
    }

    public void deleteWorldGuardRegion(IWrappedRegion iWrappedRegion) {
        World world = privateMines.getMineWorldManager().getMinesWorld();
        WorldGuardWrapper.getInstance().removeRegion(world, iWrappedRegion.getId());
    }

    public void setMineFlags(WorldEditMine worldEditMine) {
        final WorldGuardWrapper worldGuardWrapper = WorldGuardWrapper.getInstance();
        IWrappedRegion iWrappedRegion = worldEditMine.getiWrappedRegion();

        Stream.of(
                        worldGuardWrapper.getFlag("block-place", WrappedState.class),
                        worldGuardWrapper.getFlag("block-break", WrappedState.class)
                ).filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(flag -> iWrappedRegion.setFlag(flag, WrappedState.ALLOW));

        Stream.of(
                        worldGuardWrapper.getFlag("mob-spawning", WrappedState.class)
                ).filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(flag -> iWrappedRegion.setFlag(flag, WrappedState.DENY));
    }

    public void setGlobalFlags(IWrappedRegion iWrappedRegion) {
        final WorldGuardWrapper worldGuardWrapper = WorldGuardWrapper.getInstance();

        Stream.of(
            worldGuardWrapper.getFlag("build", WrappedState.class),
            worldGuardWrapper.getFlag("interact", WrappedState.class),
            worldGuardWrapper.getFlag("use", WrappedState.class)
        ).filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(flag -> iWrappedRegion.setFlag(flag, WrappedState.DENY));
    }

    // Converts a bukkit material to a world edit block type

    public BlockType bukkitToBlockType(Material material) {
        return BukkitAdapter.asBlockType(material);
    }

    public List<BlockType> bukkitToBlockType(@NotNull List<Material> materials) {
        List<BlockType> converted = new ArrayList<>();

        for (Material material : materials) {
            converted.add(bukkitToBlockType(material));
        }
        return converted;
    }

    public BlockState getBlockState(@NotNull BlockType blockType) {
        return blockType.getDefaultState();
    }

    public void sendMessage(CommandSender commandSender, String message) {
        String toSend = Messages.msg(message);
        if (commandSender != null && toSend != null) {
            commandSender.sendMessage(toSend);
        }
    }

    public void sendMessage(Player player, String message) {
        String toSend = Messages.msg(message);
        if (player != null && toSend != null) {
            player.sendMessage(toSend);
        }
    }

    public String getMineFileName(UUID uuid) {
        return String.format("mine-%s.json", uuid);
    }

    public CuboidRegion worldEditRegionToRedLibRegion(com.sk89q.worldedit.regions.CuboidRegion cuboidRegion) {
        BlockVector3 min = cuboidRegion.getMinimumPoint();
        BlockVector3 max = cuboidRegion.getMaximumPoint();
        World world = privateMines.getMineWorldManager().getMinesWorld();
        privateMines.getLogger().info("world: " + world);
        Location minLoc = blockVector3toBukkit(world, min);
        Location maxLoc = blockVector3toBukkit(world, max);
        return new CuboidRegion(minLoc, maxLoc);
    }

    public void moveSchematicFiles(File[] files) {
        File directory = privateMines.getSchematicsDirectory();
        File[] directoryFiles = directory.listFiles();

        if (files != null && directoryFiles != null) {
            for (File file : files) {
                if (file.toPath().endsWith(".schem")) {
                    for (File check : directoryFiles) {
                        if (check == file) {
                            privateMines.getLogger().info("The file " + file + " already exists so not moving!");
                        } else {
                            try {
                                privateMines.getLogger().info("Moving file " + file);
                                Files.move(Paths.get(file.toURI()), directory.toPath());
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public long secondsToBukkit(int seconds) {
        return 20L * seconds;
    }

    // Returns an arraylist of class names in a JarInputStream
    private ArrayList<String> getClassNamesFromJar(JarInputStream jarFile) throws Exception {
        ArrayList<String> classNames = new ArrayList<>();
        try {
            //JarInputStream jarFile = new JarInputStream(jarFileStream);
            JarEntry jar;

            //Iterate through the contents of the jar file
            while (true) {
                jar = jarFile.getNextJarEntry();
                if (jar == null) {
                    break;
                }
                //Pick file that has the extension of .class
                if ((jar.getName().endsWith(".class"))) {
                    String className = jar.getName().replaceAll("/", "\\.");
                    String myClass = className.substring(0, className.lastIndexOf('.'));
                    classNames.add(myClass);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error while getting class names from jar", e);
        }
        return classNames;
    }

    // Returns an arraylist of class names in a JarInputStream
    // Calls the above function by converting the jar path to a stream
    private ArrayList<String> getClassNamesFromJar(String jarPath) throws Exception {
        return getClassNamesFromJar(new JarInputStream(new FileInputStream(jarPath)));
    }

    // https://techitmore.com/java/dynamically-loading-classes-in-a-jar-file/
    private ArrayList<Class<?>> loadJarFile(String filePath) throws Exception {

        ArrayList<Class<?>> availableClasses = new ArrayList<>();

        ArrayList<String> classNames = getClassNamesFromJar(filePath);
        File f = new File(filePath);

        URLClassLoader classLoader = new URLClassLoader(new URL[]{f.toURI().toURL()});
        for (String className : classNames) {
            try {
                Class<?> cc = classLoader.loadClass(className);
                availableClasses.add(cc);
            } catch (ClassNotFoundException e) {
                privateMines.getLogger().warning("Class " + className + " was not found");
            }
        }
        return availableClasses;
    }

    public void doAction(Player player, WorldEditMine worldEditMine, String action) {
        if (worldEditMine != null) {
            WorldEditMineData worldEditMineData = worldEditMine.getWorldEditMineData();
            List<UUID> whitelistedPlayers = worldEditMineData.getWhitelistedPlayers();

            player.sendMessage("Doing action... " + action);

            switch (action.toLowerCase()) {
                case "reset":
                    worldEditMine.reset();
                case "teleporttomine":
                    worldEditMine.teleport(player);
                case "whitelistedplayers":
                    whitelistedPlayers.forEach(uuid -> {
                        player.sendMessage(String.valueOf(uuid));
                    });
            }
        }
    }

    public String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    // Credits to CapOfCave#5962 for this
    public List<String> color(@NotNull Collection<String> toConvert) {
        return toConvert.stream().map(this::color).toList();
    }

    public String convertPAPI(Player player, String message) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }
}
