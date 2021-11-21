package me.untouchedodin0.plugin.commands;

import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.mines.WorldEditMine;
import me.untouchedodin0.plugin.mines.WorldEditMineType;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.TreeMap;
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
    public void give(CommandSender commandSender, Player target) {

        String alreadyOwnsMine = "targetAlreadyOwnsAMine";
        String targetAlreadyOwnsAMine = Messages.msg(alreadyOwnsMine);

        try {
            if (mineStorage.hasMine(target.getUniqueId())) {
                utils.sendMessage(commandSender, targetAlreadyOwnsAMine);
                return;
            }
            commandSender.sendMessage(ChatColor.GREEN + "Giving " + target.getName() + " a private mine!");
            Location location = mineWorldManager.getNextFreeLocation();
            if (privateMines.isWorldEditEnabled()) {
                @SuppressWarnings("unused")
                WorldEditMine worldEditMine = mineFactory.createMine(target, location, privateMines.getDefaultWorldEditMineType());
            }
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            arrayIndexOutOfBoundsException.printStackTrace();
        }
    }

    @CommandHook("delete")
    public void delete(CommandSender commandSender, Player target) {
        Player player = (Player) commandSender;
        UUID uuid = target.getUniqueId();

        String targetDoesNotOwnMine = "targetDoesNotOwnMine";
        String deletedPlayersMine = "deletedPlayersMine";
        String yourMineHasBeenDeleted = "deletedMine";

        String doesntOwnMine = Messages.msg(targetDoesNotOwnMine);

        File minesDirectory = privateMines.getMinesDirectory();
        String fileName = target.getUniqueId() + ".json";
        File jsonFile = new File(minesDirectory, fileName);

        if (!privateMines.getMineStorage().hasWorldEditMine(uuid)) {
            player.sendMessage(ChatColor.RED + "Player doesn't own a mine!");
//            utils.sendMessage(commandSender, doesntOwnMine);
        } else {
            WorldEditMine worldEditMine = privateMines.getMineStorage().getWorldEditMine(uuid);
            worldEditMine.delete();
            privateMines.getMineStorage().removeWorldEditMine(uuid);
            if (jsonFile.exists()) {
                boolean deleted = jsonFile.delete();
                if (deleted) {
                    privateMines.getLogger().info("The file has been deleted!");
                }
            }
            utils.sendMessage(player, deletedPlayersMine);
            utils.sendMessage(target, yourMineHasBeenDeleted);
        }
    }

    @CommandHook("reset")
    public void reset(CommandSender commandSender) {
        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();
        String doNotOwnMine = "doNotOwnMine";
        String mineReset = "mineReset";
        String teleportedToMine = "teleportedToMine";

        if (!mineStorage.hasWorldEditMine(uuid)) {
            utils.sendMessage(player, doNotOwnMine);
        } else {
            WorldEditMine worldEditMine = mineStorage.getWorldEditMine(uuid);
            worldEditMine.reset();
            utils.sendMessage(player, mineReset);
            worldEditMine.teleport(player);
            utils.sendMessage(player, teleportedToMine);
        }
    }


    @CommandHook("teleport")
    public void teleport(CommandSender commandSender) {
        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();
        String doNotOwnMine = "doNotOwnMine";
        String teleportedToMine = "teleportedToMine";

        if (!mineStorage.hasWorldEditMine(uuid)) {
            utils.sendMessage(player, doNotOwnMine);
            return;
        }

        WorldEditMine worldEditMine = mineStorage.getWorldEditMine(uuid);
        worldEditMine.teleport(player);
        utils.sendMessage(player, teleportedToMine);
    }

    @CommandHook("teleportOther")
    public void teleportOther(CommandSender commandSender, Player target) {
        UUID uuid = target.getUniqueId();
        String targetDoesNotOwnMine = Messages.msg("targetDoesNotOwnMine");
        String teleportedToTargetsMine = Messages.msg("teleportedToTargetsMine");

        if (!mineStorage.hasWorldEditMine(uuid)) {
            utils.sendMessage(commandSender, targetDoesNotOwnMine);
            return;
        }

        Player player = (Player) commandSender;
        WorldEditMine worldEditMine = mineStorage.getWorldEditMine(uuid);
        worldEditMine.teleport(player);
        commandSender.sendMessage(ChatColor.GREEN + "Teleporting to " + target.getName() + " a private mine!");

//        Mine mine = mineStorage.getMine(target.getUniqueId());
//        mine.teleportPlayer(player);
    }

    @CommandHook("upgrade")
    public void upgrade(CommandSender commandSender, Player target) {
        String targetDoesNotOwnMine = Messages.msg("targetDoesNotOwnMine");
        String attemptingMineUpgrade = Messages.msg("attemptingMineUpgrade");

        TreeMap<String, WorldEditMineType> worldEditMineTypeTreeMap =  privateMines.getWorldEditMineTypeTreeMap();

        if (!mineStorage.hasWorldEditMine(target.getUniqueId())) {
            utils.sendMessage(commandSender, targetDoesNotOwnMine);
            return;
        }

        Player player = (Player) commandSender;
        WorldEditMine worldEditMine = mineStorage.getWorldEditMine(target.getUniqueId());
        WorldEditMineType worldEditMineType = worldEditMine.getWorldEditMineType();
        String worldEditMineTypeName = worldEditMineType.getName();

        player.sendMessage("upgrade worldEditMine: " + worldEditMine);
        player.sendMessage("upgrade worldEditMineType before: " + worldEditMineType);
        player.sendMessage("upgrade worldEditMineTypeName before: " + worldEditMineTypeName);

        if (Objects.equals(worldEditMineTypeTreeMap.lastKey(), worldEditMineTypeName)) {
            player.sendMessage("You're already at the highest tier!!!!!!");
        } else {
            String upgradeTypeString = worldEditMineTypeTreeMap.higherKey(worldEditMineTypeName);
            WorldEditMineType upgradeType = privateMines.getWorldEditMineTypeTreeMap().get(upgradeTypeString);
            player.sendMessage("upgradeType: " + upgradeType);
            player.sendMessage("upgrade type name: " + upgradeType.getName());
            worldEditMine.setWorldEditMineType(upgradeType);
            worldEditMine.upgrade();
        }

//        Mine mine = mineStorage.getMine(target.getUniqueId());r
//        utils.sendMessage(commandSender, attemptingMineUpgrade);
//        mine.upgrade();
    }

    // Add 1 to whatever args you put so if you wanna expand by one do /pmine expand 2

    @CommandHook("expand")
    public void expand(CommandSender commandSender, Player target, int amount) {
        Player player = (Player) commandSender;
        String targetDoesNotOwnMine = Messages.msg("targetDoesNotOwnMine");

        if (!mineStorage.hasWorldEditMine(target.getUniqueId())) {
            utils.sendMessage(commandSender, targetDoesNotOwnMine);
            return;
        }
        WorldEditMine worldEditMine = mineStorage.getWorldEditMine(target.getUniqueId());
//        Mine mine = mineStorage.getMine(target.getUniqueId());
        player.sendMessage("attempting to expand your mine");
        worldEditMine.expand(amount);
        mineStorage.replaceMine(player.getUniqueId(), worldEditMine);
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
        String targetDoesNotOwnMine = Messages.msg("targetDoesNotOwnMine");

        for (Material material : materials) {
            weightedRandom.set(material, 1);
        }

        if (!mineStorage.hasMine(target.getUniqueId())) {
            utils.sendMessage(commandSender, targetDoesNotOwnMine);
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
        String invalidMineType = Messages.msg("invalidMineType");

        if (mineStorage.hasMine(target.getUniqueId())) {
            mine = mineStorage.getMine(target.getUniqueId());
            newType = privateMines.getMineType(type);
            if (newType == null) {
                player.sendMessage(ChatColor.RED + "Invalid mine type!");
                utils.sendMessage(commandSender, invalidMineType);
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
        String doNotOwnMine = Messages.msg("doNotOwnMine");
        String mineAlreadyOpen = Messages.msg("mineAlreadyOpen");
        String mineOpened = Messages.msg("mineOpened");

        if (!mineStorage.hasMine(uuid)) {
            utils.sendMessage(commandSender, doNotOwnMine);
            return;
        }
        mine = mineStorage.getMine(player.getUniqueId());

        if (mine.isOpen()) {
            player.sendMessage(mineAlreadyOpen);
        } else if (!mine.isOpen()) {
            mine.setIsOpen(true);
            Messages.msg(mineOpened);
        }
    }


    @CommandHook("close")
    public void close(CommandSender commandSender) {
        Player player = (Player) commandSender;
        Mine mine;
        UUID uuid = player.getUniqueId();
        String mineAlreadyClosed = Messages.msg("mineAlreadyClosed");
        String mineOpened = Messages.msg("mineOpened");

        if (!mineStorage.hasMine(uuid)) {
            utils.sendMessage(commandSender, "doNotOwnMine");
            return;
        }
        mine = mineStorage.getMine(uuid);
        if (!mine.isOpen()) {
            player.sendMessage(mineAlreadyClosed);
        } else {
            player.sendMessage(mineOpened);
        }
    }

    @CommandHook("whitelist")
    public void whitelist(CommandSender commandSender, Player target) {
        Player player = (Player) commandSender;
        Mine mine;
        UUID uuid = player.getUniqueId();
        String doNotOwnMine = Messages.msg("doNotOwnMine");

        if (!mineStorage.hasMine(uuid)) {
            player.sendMessage(doNotOwnMine);
        }
        mine = mineStorage.getMine(uuid);
        player.sendMessage("whitelist mine: " + mine);
    }

    @CommandHook("unwhitelist")
    public void unWhitelist(CommandSender commandSender, Player target) {
        Player player = (Player) commandSender;
        Mine mine;
        UUID uuid = player.getUniqueId();
        String doNotOwnMine = Messages.msg("doNotOwnMine");

        if (!mineStorage.hasMine(uuid)) {
            player.sendMessage(doNotOwnMine);
        }
        mine = mineStorage.getMine(uuid);
        player.sendMessage("un-whitelist mine: " + mine);
    }
}
