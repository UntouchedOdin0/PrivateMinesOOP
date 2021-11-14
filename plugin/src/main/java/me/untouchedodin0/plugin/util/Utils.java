package me.untouchedodin0.plugin.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.MineType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.multiblock.Structure;
import redempt.redlib.region.CuboidRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class Utils {

    private final PrivateMines privateMines;
    private final boolean debugMode;

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

    public void setMineFlags(Optional<IWrappedRegion> region) {
        final WorldGuardWrapper w = WorldGuardWrapper.getInstance();

        Stream.of(
                w.getFlag("block-place", WrappedState.class),
                w.getFlag("block-break", WrappedState.class)
        ).filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(flag -> region.ifPresent(iWrappedRegion -> iWrappedRegion.setFlag(flag, WrappedState.ALLOW)));

        Stream.of(
                w.getFlag("mob-spawning", WrappedState.class)
        ).filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(flag -> region.ifPresent(iWrappedRegion -> iWrappedRegion.setFlag(flag, WrappedState.DENY)));
    }

    // Converts a bukkit material to a world edit block type

    public BlockType bukkitToBlockType(Material material) {
        return BukkitAdapter.asBlockType(material);
    }

    public List<BlockType> bukkitToBlockType(List<Material> materials) {
        List<BlockType> converted = new ArrayList<>();

        for (Material material : materials) {
            converted.add(bukkitToBlockType(material));
        }
        return converted;
    }

    public BlockState getBlockState(BlockType blockType) {
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
}
