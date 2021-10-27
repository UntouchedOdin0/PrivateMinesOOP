package me.untouchedodin0.privatemines.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mines.Mine;
import me.untouchedodin0.privatemines.mines.MineType;
import me.untouchedodin0.privatemines.storage.MineStorage;
import me.untouchedodin0.privatemines.util.Utils;
import me.untouchedodin0.privatemines.world.MineWorldManager;
import net.royawesome.jlibnoise.module.combiner.Min;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.blockdata.DataBlock;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.misc.WeightedRandom;
import redempt.redlib.multiblock.MultiBlockStructure;
import redempt.redlib.multiblock.Structure;
import redempt.redlib.region.CuboidRegion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class PrivateMinesCommand {

    private final MineFactory mineFactory;
    private final MineStorage mineStorage;
    private final MineWorldManager mineWorldManager;
    private final PrivateMines privateMines;
    Utils utils;
    Path path;

    public PrivateMinesCommand(PrivateMines privateMine) {
        this.privateMines = privateMine;
        this.mineFactory = privateMine.getMineFactory();
        this.mineStorage = privateMine.getMineStorage();
        this.mineWorldManager = privateMine.getMineWorldManager();
        this.utils = privateMine.getUtils();
    }

    @CommandHook("give")
    public void give(CommandSender commandSender, Player target) {
        if (mineStorage.hasMine(target.getUniqueId())) {
            Messages.msg("targetAlreadyOwnsAMine");
            return;
        }
        commandSender.sendMessage(ChatColor.GREEN + "Giving " + target.getName() + " a private mine!");
        Mine mine = mineFactory.createMine(target, mineWorldManager.getNextFreeLocation());
        mine.teleportPlayer(target);
    }

    @CommandHook("delete")
    public void delete(CommandSender commandSender, Player target) {
        if (!mineStorage.hasMine(target.getUniqueId())) {
            Messages.msg("targetDoesNotOwnMine");
            return;
        }
        commandSender.sendMessage(ChatColor.YELLOW + "Deleting " + target.getName() + "'s Private Mine");
        Mine mine = mineStorage.getMine(target.getUniqueId());
        mine.delete();
    }

    @CommandHook("reset")
    public void reset(CommandSender commandSender) {
        Player player = (Player) commandSender;
        boolean hasMine = mineStorage.hasMine(player.getUniqueId());
        Mine mine = mineStorage.getMine(player.getUniqueId());
        if (mine == null) return;
        if (!hasMine) return;

        MineType mineType = mine.getMineType();

        if (mineType.getWeightedRandom().getWeights().isEmpty()) {
            privateMines.getLogger().warning("There were no materials in the weighted random!");
            return;
        }

        CuboidRegion cuboidRegion = mine.getCuboidRegion();
        cuboidRegion.forEachBlock(block -> block.setType(mineType.getWeightedRandom().roll(), false));
        Messages.msg("mineReset");
    }


    @CommandHook("teleport")
    public void teleport(CommandSender commandSender) {
        Player player = (Player) commandSender;
        if (!mineStorage.hasMine(player.getUniqueId())) {
            Messages.msg("doNotOwnMine");
            return;
        }
        Mine mine = mineStorage.getMine(player.getUniqueId());
        mine.teleportPlayer(player);
        Messages.msg("teleportedToMine");
    }

    @CommandHook("teleportOther")
    public void teleportOther(CommandSender commandSender, Player target) {
        if (!mineStorage.hasMine(target.getUniqueId())) {
            Messages.msg("targetDoesNotOwnMine");
        }
        Mine mine = mineStorage.getMine(target.getUniqueId());
        Player player = (Player) commandSender;
        mine.teleportPlayer(player);
        Messages.msg("teleportedToTargetsMine");
    }

    @CommandHook("upgrade")
    public void upgrade(CommandSender commandSender, Player target) {
        if (!mineStorage.hasMine(target.getUniqueId())) {
            Messages.msg("targetDoesNotOwnMine");
            return;
        }
        Mine mine = mineStorage.getMine(target.getUniqueId());
        Messages.msg("attemptingMineUpgrade");
        mine.upgrade();
    }

    @CommandHook("create")
    public void create(CommandSender commandSender, String name) {
        Player player = (Player) commandSender;
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(BukkitAdapter.adapt(player));
        Region region; // declare the region variable
        World selectionWorld = localSession.getSelectionWorld();
        BlockVector3 minimum;
        BlockVector3 maximum;
        Location minimumBukkit;
        Location maximumBukkit;
        String multiBlockStructure;

        try {
            if (selectionWorld == null) throw new IncompleteRegionException();
            region = localSession.getSelection(selectionWorld);

            minimum = region.getMinimumPoint();
            maximum = region.getMaximumPoint();
            minimumBukkit = utils.blockVector3toBukkit(BukkitAdapter.adapt(selectionWorld), minimum);
            maximumBukkit = utils.blockVector3toBukkit(BukkitAdapter.adapt(selectionWorld), maximum);

            // Credits to redempt for this part
            multiBlockStructure = MultiBlockStructure.stringify(minimumBukkit, maximumBukkit);
            try {
                path = Paths.get("plugins/PrivateMines/").resolve(name + ".dat");
                player.sendMessage(ChatColor.YELLOW + "Attempting to write the file, " + path.getFileName() + "...");
                Files.write(path, multiBlockStructure.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            player.sendMessage(ChatColor.GREEN + "Successfully created the file " + path.getFileName());
        } catch (IncompleteRegionException incompleteRegionException) {
            player.sendMessage("Please make a full selection!");
        }
    }

    @CommandHook("setblocks")
    public void setBlocks(CommandSender commandSender, Player target, Material[] materials) {
        Player player = (Player) commandSender;
        ArgType<Material> materialArgType = ArgType.of("material", Material.class);
        WeightedRandom<Material> weightedRandom = new WeightedRandom<>();
        Mine mine;

        for (Material material : materials) {
            Bukkit.broadcastMessage("Material[]: " + material.name());
            weightedRandom.set(material, 1);
        }
        player.sendMessage("materialArgType: " + materialArgType);
        player.sendMessage("weightedRandom: " + weightedRandom.getWeights());

        if (!mineStorage.hasMine(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Target doesn't have a mine!");
        } else {
            mine = mineStorage.getMine(target.getUniqueId());
            player.sendMessage("Setting " + target.getName() + "'s blocks to " + weightedRandom.getWeights());
            mine.setWeightedRandom(weightedRandom);

            BlockDataManager blockDataManager = privateMines.getBlockDataManager();
            DataBlock dataBlock = privateMines.getBlockDataManager().getDataBlock(mine.getMineLocation().getBlock());
            dataBlock.set("weightedRandom", weightedRandom);
            mine.reset();
            blockDataManager.save();
        }
    }

    @CommandHook("settype")
    public void setType(CommandSender commandSender, Player target, String type) {
        Player player = (Player) commandSender;
        Mine mine;
        MineType newType;
        Structure structure;

        MineStorage mineStorage = privateMines.getMineStorage();
        if (mineStorage.hasMine(target.getUniqueId())) {
            mine = mineStorage.getMine(target.getUniqueId());
            newType = privateMines.getMineType(type);
            if (newType == null) {
                player.sendMessage(ChatColor.RED + "Invalid mine type!");
                return;
            }
            mine.cancelResetTask();
            structure = mine.getStructure();
            structure.getRegion().forEachBlock(block -> block.setType(Material.AIR, false));
            mine.setMineType(newType);
            mine.build();
            mine.startAutoResetTask();
            mineStorage.replaceMine(player.getUniqueId(), mine);
            mine.teleportPlayer(target);
        }
        player.sendMessage("done lol");
    }
}
