package me.untouchedodin0.plugin.commands;

import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Utils;
import me.untouchedodin0.plugin.world.MineWorldManager;
import me.untouchedodin0.privatemines.compat.WorldEditUtilities;
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
import java.util.UUID;

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
    public void give(CommandSender commandSender, Player target) throws ArrayIndexOutOfBoundsException {
        try {
            if (mineStorage.hasMine(target.getUniqueId())) {
                Messages.msg("targetAlreadyOwnsAMine");
                return;
            }
            commandSender.sendMessage(ChatColor.GREEN + "Giving " + target.getName() + " a private mine!");
            Mine mine = mineFactory.createMine(target, mineWorldManager.getNextFreeLocation());
            mine.teleportPlayer(target);
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            arrayIndexOutOfBoundsException.printStackTrace();
        }
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

    // Add 1 to whatever args you put so if you wanna expand by one do /pmine expand 2

    @CommandHook("expand")
    public void expand(CommandSender commandSender, Player target, int amount) {
        Player player = (Player) commandSender;
        if (!mineStorage.hasMine(target.getUniqueId())) {
            Messages.msg("targetDoesNotOwnMine");
            return;
        }
        Mine mine = mineStorage.getMine(target.getUniqueId());
        player.sendMessage("attempting to expand your mine");
        mine.expand(amount);
    }

    @CommandHook("create")
    public void create(CommandSender commandSender, String name) {
        Player player = (Player) commandSender;
        CuboidRegion cuboidRegion;
        Location minimum;
        Location maximum;
        String multiBlockStructure;
        WorldEditUtilities worldEditUtilities;

        worldEditUtilities = privateMines.getWorldEditUtils();
        cuboidRegion = worldEditUtilities.getCuboidRegion(player);

        player.sendMessage("region: " + worldEditUtilities.getCuboidRegion(player));
        player.sendMessage("cuboid region: " + cuboidRegion);
        minimum = cuboidRegion.getStart();
        maximum = cuboidRegion.getEnd();
        player.sendMessage("cuboid region minimum: " + minimum);
        player.sendMessage("cuboid region maximum: " + maximum);

        worldEditUtilities.createMultiBlockStructure(player, name);

        if (minimum != null && maximum != null) {
            multiBlockStructure = MultiBlockStructure.stringify(minimum, maximum);
            try {
                path = Paths.get("plugins/PrivateMines/").resolve(name + ".dat");
                player.sendMessage(ChatColor.YELLOW + "Attempting to write the file, " + name + ".dat...");
                Files.write(path, multiBlockStructure.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ioException) {
                player.sendMessage("oh no");
                ioException.printStackTrace();
            }
        }

//        try {
//            if (selectionWorld == null) throw new IncompleteRegionException();
//            region = localSession.getSelection(selectionWorld);
//
//            minimum = region.getMinimumPoint();
//            maximum = region.getMaximumPoint();
//            minimumBukkit = utils.blockVector3toBukkit(BukkitAdapter.adapt(selectionWorld), minimum);
//            maximumBukkit = utils.blockVector3toBukkit(BukkitAdapter.adapt(selectionWorld), maximum);
//
//            // Credits to redempt for this part
//            multiBlockStructure = MultiBlockStructure.stringify(minimumBukkit, maximumBukkit);
//            try {
//                path = Paths.get("plugins/PrivateMines/").resolve(name + ".dat");
//                player.sendMessage(ChatColor.YELLOW + "Attempting to write the file, " + name + ".dat...");
//                Files.write(path, multiBlockStructure.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//            player.sendMessage(ChatColor.GREEN + "Successfully created the file " + path.getFileName());
//        } catch (IncompleteRegionException incompleteRegionException) {
//            player.sendMessage("Please make a full selection!");
//            Messages.msg("pleaseMakeFullSelection");
//        }
    }

    @CommandHook("setblocks")
    public void setBlocks(CommandSender commandSender, Player target, Material[] materials) {
        Player player = (Player) commandSender;
        ArgType<Material> materialArgType = ArgType.of("material", Material.class);
        WeightedRandom<Material> weightedRandom = new WeightedRandom<>();
        Mine mine;

        for (Material material : materials) {
            weightedRandom.set(material, 1);
        }

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

        if (mineStorage.hasMine(target.getUniqueId())) {
            mine = mineStorage.getMine(target.getUniqueId());
            newType = privateMines.getMineType(type);
            if (newType == null) {
                player.sendMessage(ChatColor.RED + "Invalid mine type!");
                Messages.msg("invalidMineType");
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
    }

    @CommandHook("open")
    public void open(CommandSender commandSender) {
        Player player = (Player) commandSender;
        Mine mine;
        UUID uuid = player.getUniqueId();

        if (!mineStorage.hasMine(uuid)) {
            Messages.msg("doNotOwnMine");
            return;
        }
        mine = mineStorage.getMine(player.getUniqueId());

        if (mine.isOpen()) {
            Messages.msg("mineAlreadyOpen");
        } else if (!mine.isOpen()) {
            mine.setIsOpen(true);
            Messages.msg("mineOpened");
        }
    }


    @CommandHook("close")
    public void close(CommandSender commandSender) {
        Player player = (Player) commandSender;
        Mine mine;
        UUID uuid = player.getUniqueId();

        if (!mineStorage.hasMine(uuid)) {
            Messages.msg("doNotOwnMine");
            return;
        }
        mine = mineStorage.getMine(uuid);
        if (!mine.isOpen()) {
            Messages.msg("mineAlreadyClosed");
            player.sendMessage(ChatColor.RED + "Your mine was already closed!");
        } else {
            Messages.msg("mineOpened");
            player.sendMessage("Opening your mine!");
        }
    }

    @CommandHook("whitelist")
    public void whitelist(CommandSender commandSender, Player target) {
        Player player = (Player) commandSender;
        Mine mine;
        UUID uuid = player.getUniqueId();

        if (!mineStorage.hasMine(uuid)) {
            Messages.msg("doNotOwnMine");
        }
        mine = mineStorage.getMine(uuid);
        player.sendMessage("whitelist mine: " + mine);
    }

    @CommandHook("unwhitelist")
    public void unWhitelist(CommandSender commandSender, Player target) {
        Player player = (Player) commandSender;
        Mine mine;
        UUID uuid = player.getUniqueId();

        if (!mineStorage.hasMine(uuid)) {
            Messages.msg("doNotOwnMine");
        }
        mine = mineStorage.getMine(uuid);
        player.sendMessage("un-whitelist mine: " + mine);
    }
}
