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

package me.untouchedodin0.plugin.commands;

import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.config.MenuConfig;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.mines.WorldEditMine;
import me.untouchedodin0.plugin.mines.WorldEditMineType;
import me.untouchedodin0.plugin.mines.data.WorldEditMineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Utils;
import me.untouchedodin0.plugin.world.MineWorldManager;
import me.untouchedodin0.privatemines.compat.WorldEditUtilities;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.blockdata.DataBlock;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
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
import java.util.*;

public class PrivateMinesCommand {

    private final MineFactory mineFactory;
    private final MineStorage mineStorage;
    private final MineWorldManager mineWorldManager;
    private final PrivateMines privateMines;
    Utils utils;
    Path path;
    List<UUID> whitelistedPlayers;

    public PrivateMinesCommand(PrivateMines privateMine) {
        this.privateMines = privateMine;
        this.mineFactory = privateMine.getMineFactory();
        this.mineStorage = privateMine.getMineStorage();
        this.mineWorldManager = privateMine.getMineWorldManager();
        this.utils = privateMine.getUtils();
    }

    public void hi(Player player) {
        player.sendMessage("hi");
    }

    @CommandHook("main")
    public void mainHook(Player player) {
        Map<String, MenuConfig> menuConfig = privateMines.getInventory();
        String inventoryTitle = privateMines.getMainMenuTitle();
        String inventoryTitleColored = utils.color(inventoryTitle);

        InventoryGUI gui = new InventoryGUI(Bukkit.createInventory(null, 9, inventoryTitleColored));
        player.sendMessage(menuConfig.toString());
        WorldEditMine worldEditMine = mineStorage.getWorldEditMine(player.getUniqueId());

        menuConfig.forEach((s, c) -> {
            String name = utils.color(c.getName());
            List<String> lore = utils.color(c.getLore());

            int slot = c.getSlot();

            ItemStack itemStack = new ItemStack(c.getType());
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta != null) {
                itemMeta.setDisplayName(name);
                itemMeta.setLore(lore);
            }

            itemStack.setItemMeta(itemMeta);

            ItemButton itemButton = ItemButton.create(itemStack, inventoryClickEvent -> {
                if (c.getSlot() == inventoryClickEvent.getSlot()) {
                    String action = c.getAction();
                    player.sendMessage("action: " + action);
                    utils.doAction(player, worldEditMine, action);
                }
            });
            gui.addButton(slot, itemButton);
        });

        gui.open(player);

//        menuConfig.forEach((s, c) -> {
//
//            ItemButton button = ItemButton.create(new ItemBuilder(Material.EMERALD_BLOCK).setName("Click Me"), inventoryClickEvent -> {
//                HumanEntity humanEntity = inventoryClickEvent.getWhoClicked();
//                if (!(humanEntity instanceof Player)) return; // makes sure the entity who clicked is a player idk how something else could click it
//                String action = c.getAction();
//
//
//                switch (action) {
//                    case "teleporttomine":
//                        player.sendMessage("switch case teleport to mine!!! yay!!!?");
//                    case "reset":
//                        player.sendMessage("switch case reset mine.... yay?");
//                    case "lock":
//                        player.sendMessage("locking pmine lol");
//                    case "unlock":
//                        player.sendMessage("unlocking pmine");
//                    case "whitelistedplayers":
//                        player.sendMessage("opening whitelisted players gui");
//                    case "publicmines":
//                        player.sendMessage("lets see who has their mine open");
//                }
//
////                privateMines.getLogger().info("item: " + c.getItem());
////                privateMines.getLogger().info("lore: " + c.getLore());
////                privateMines.getLogger().info("type: " + c.getType());
////                privateMines.getLogger().info("slot: " + c.getSlot());
//                privateMines.getLogger().info("action: " + c.getAction());
//            });
//
//            player.sendMessage("button: " + button);
//            gui.addButton(c.getSlot(), button);
//        });
        //todo https://github.com/Redempt/RedLib/wiki/InventoryGUI
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
                WorldEditMine worldEditMine = mineFactory.createMine(target, location, privateMines.getDefaultWorldEditMineType(), false);
            }
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            arrayIndexOutOfBoundsException.printStackTrace();
        }
    }

    @CommandHook("delete")
    public void delete(CommandSender commandSender, Player target) {
        UUID uuid = target.getUniqueId();

        String deletedPlayersMine = "deletedPlayersMine";
        String yourMineHasBeenDeleted = "deletedMine";

        File minesDirectory = privateMines.getMinesDirectory();
        String fileName = target.getUniqueId() + ".json";
        File jsonFile = new File(minesDirectory, fileName);

        if (!privateMines.getMineStorage().hasWorldEditMine(uuid)) {
            commandSender.sendMessage(ChatColor.RED + "Player doesn't own a mine!");
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
            utils.sendMessage(commandSender, deletedPlayersMine);
            utils.sendMessage(target, yourMineHasBeenDeleted);
        }
    }

    @CommandHook("reset")
    public void reset(Player player) {
        UUID uuid = player.getUniqueId();
        String doNotOwnMine = "doNotOwnMine";
        String mineReset = "mineReset";
        String teleportedToMine = "teleportedToMine";

        if (!mineStorage.hasWorldEditMine(uuid)) {
            utils.sendMessage(player, doNotOwnMine);
        } else {
            WorldEditMine worldEditMine = mineStorage.getWorldEditMine(uuid);
            WorldEditMineData worldEditMineData = worldEditMine.getWorldEditMineData();
            String mineType = worldEditMineData.getMineType();
            WorldEditMineType worldEditMineType = privateMines.getWorldEditMineType(mineType);
            worldEditMine.fill(worldEditMineType.getMaterials());
            utils.sendMessage(player, mineReset);
            worldEditMine.teleport(player);
            utils.sendMessage(player, teleportedToMine);
        }
    }

    @CommandHook("teleport")
    public void teleport(Player player) {
        UUID uuid = player.getUniqueId();
        String doNotOwnMine = "doNotOwnMine";
        String teleportedToMine = Messages.msg("teleportedToMine");

        if (!mineStorage.hasWorldEditMine(uuid)) {
            utils.sendMessage(player, doNotOwnMine);
            return;
        }

        WorldEditMine worldEditMine = mineStorage.getWorldEditMine(uuid);
        worldEditMine.teleport(player);
        player.sendMessage(teleportedToMine);
    }

    @CommandHook("teleportOther")
    public void teleportOther(Player player, Player target) {
        UUID uuid = target.getUniqueId();
        String targetDoesNotOwnMine = Messages.msg("targetDoesNotOwnMine");
        String notWhitelisted = Messages.msg("notWhitelisted");

        if (!mineStorage.hasWorldEditMine(uuid)) {
            utils.sendMessage(player, targetDoesNotOwnMine);
            return;
        }

        WorldEditMine worldEditMine = mineStorage.getWorldEditMine(uuid);
        WorldEditMineData worldEditMineData = worldEditMine.getWorldEditMineData();

        List<UUID> whitelistedPlayers = worldEditMineData.getWhitelistedPlayers();
        boolean isOpen = worldEditMineData.isOpen();

        if (!isOpen) {
            boolean isWhitelisted = whitelistedPlayers.contains(player.getUniqueId());
            if (isWhitelisted) {
                worldEditMine.teleport(player);
            } else {
                player.sendMessage(notWhitelisted);
            }
        } else {
            worldEditMine.teleport(player);
        }
    }

    @CommandHook("upgrade")
    public void upgrade(CommandSender commandSender, Player target) {
        Player player = (Player) commandSender;
        String targetDoesNotOwnMine = Messages.msg("targetDoesNotOwnMine");

        TreeMap<String, WorldEditMineType> worldEditMineTypeTreeMap = privateMines.getWorldEditMineTypeTreeMap();

        if (!mineStorage.hasWorldEditMine(target.getUniqueId())) {
            utils.sendMessage(player, targetDoesNotOwnMine);
            return;
        }

        WorldEditMine worldEditMine = mineStorage.getWorldEditMine(target.getUniqueId());
        WorldEditMineType worldEditMineType = worldEditMine.getWorldEditMineType();
        WorldEditMineType nextWorldEditMineType = worldEditMineTypeTreeMap.higherEntry(worldEditMineType.getName()).getValue();

        if (nextWorldEditMineType == null) {
            privateMines.getLogger().info("Failed to upgrade players mine as they're at max type!");
        } else {
            worldEditMine.upgrade(player, nextWorldEditMineType);
        }
    }

    // Add 1 to whatever args you put so if you want to expand by one do /pmine expand 2

    @CommandHook("expand")
    public void expand(CommandSender commandSender, Player target, int amount) {
        Player player = (Player) commandSender;
        String targetDoesNotOwnMine = Messages.msg("targetDoesNotOwnMine");

        if (!mineStorage.hasWorldEditMine(target.getUniqueId())) {
            utils.sendMessage(commandSender, targetDoesNotOwnMine);
            return;
        }
        WorldEditMine worldEditMine = mineStorage.getWorldEditMine(target.getUniqueId());
        worldEditMine.expand(amount);
        mineStorage.replaceMine(player.getUniqueId(), worldEditMine);
    }

    @CommandHook("create")
    public void create(Player player, String name) {
        CuboidRegion cuboidRegion;
        Location minimum;
        Location maximum;
        String multiBlockStructure;
        WorldEditUtilities worldEditUtilities;

        worldEditUtilities = privateMines.getWorldEditUtils();
        cuboidRegion = worldEditUtilities.getCuboidRegion(player);

        minimum = cuboidRegion.getStart();
        maximum = cuboidRegion.getEnd();

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
    }

    @CommandHook("setblocks")
    public void setBlocks(CommandSender commandSender, Player target, Material[] materials) {
        Player player = (Player) commandSender;
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
    public void open(Player player) {
        WorldEditMine worldEditMine;
        WorldEditMineData worldEditMineData;
        UUID uuid = player.getUniqueId();
        String doNotOwnMine = Messages.msg("doNotOwnMine");
        String mineAlreadyOpen = Messages.msg("mineAlreadyOpen");
        String mineOpened = Messages.msg("mineOpened");

        if (!mineStorage.hasWorldEditMine(uuid)) {
            utils.sendMessage(player, doNotOwnMine);
            return;
        }

        worldEditMine = mineStorage.getWorldEditMine(player.getUniqueId());
        worldEditMineData = worldEditMine.getWorldEditMineData();
        boolean isOpen = worldEditMineData.isOpen();

        if (isOpen) {
            player.sendMessage(mineAlreadyOpen);
        } else {
            worldEditMineData.setOpen(true);
            worldEditMine.setWorldEditMineData(worldEditMineData);
            privateMines.getMineStorage().replaceMine(uuid, worldEditMine);
            player.sendMessage(mineOpened);
        }
    }


    @CommandHook("close")
    public void close(Player player) {
        WorldEditMine worldEditMine;
        WorldEditMineData worldEditMineData;
        UUID uuid = player.getUniqueId();
        String doNotOwnMine = Messages.msg("doNotOwnMine");
        String mineAlreadyClosed = Messages.msg("mineAlreadyClosed");
        String mineClosed = Messages.msg("mineClosed");

        if (!mineStorage.hasWorldEditMine(uuid)) {
            utils.sendMessage(player, doNotOwnMine);
            return;
        }

        worldEditMine = mineStorage.getWorldEditMine(player.getUniqueId());
        worldEditMineData = worldEditMine.getWorldEditMineData();
        boolean isOpen = worldEditMineData.isOpen();

        if (!isOpen) {
            player.sendMessage(mineAlreadyClosed);
        } else {
            worldEditMineData.setOpen(false);
            worldEditMine.setWorldEditMineData(worldEditMineData);
            privateMines.getMineStorage().replaceMine(uuid, worldEditMine);
            player.sendMessage(mineClosed);
        }
    }

    @CommandHook("whitelist")
    public void whitelist(Player player, Player target) {
        WorldEditMine worldEditMine;
        WorldEditMineData worldEditMineData;
        UUID uuid = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        String doNotOwnMine = Messages.msg("doNotOwnMine");
        String youHaveBeenWhitelisted = Messages.msg("youHaveBeenWhitelistedAtMine");
        String youHaveAddedPlayerToYourMine = Messages.msg("youHaveAddedPlayerToYourMine");

        String replacedAdded = youHaveAddedPlayerToYourMine.replace("%name%", target.getName());
        String addedPlayerReplaced = replacedAdded.replace("%name%", player.getName());
        String addedReplaced = youHaveBeenWhitelisted.replace("%owner%", player.getName());

        if (!privateMines.getMineStorage().hasWorldEditMine(uuid)) {
            player.sendMessage(doNotOwnMine);
        }
        worldEditMine = privateMines.getMineStorage().getWorldEditMine(uuid);
        worldEditMineData = worldEditMine.getWorldEditMineData();
        worldEditMineData.addWhitelistedPlayer(targetUUID);
        player.sendMessage(addedPlayerReplaced);
        target.sendMessage(addedReplaced);

        worldEditMine.setWorldEditMineData(worldEditMineData);
        privateMines.getMineStorage().replaceMine(uuid, worldEditMine);
    }

    @CommandHook("unwhitelist")
    public void unWhitelist(Player player, Player target) {
        WorldEditMine worldEditMine;
        WorldEditMineData worldEditMineData;
        UUID uuid = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        String doNotOwnMine = Messages.msg("doNotOwnMine");
        String youHaveBeenUnWhitelisted = Messages.msg("youHaveBeenUnWhitelistedFromUsersMine");
        String youHaveRemovedPlayerFromYourMine = Messages.msg("youHaveUnWhitelistedPlayerFromYourMine");

        String replacedYouHaveBeenUnwhitelisted = youHaveBeenUnWhitelisted.replace("%name%", target.getName());
        String YouHaveBeenUnwhitelistedPlayerReplaced = replacedYouHaveBeenUnwhitelisted.replace("%name%", player.getName());

        String replacedYouHaveRemoved = youHaveRemovedPlayerFromYourMine.replace("%name%", target.getName());
        String YouHaveRemovedPlayerReplaced = replacedYouHaveRemoved.replace("%name%", player.getName());

        if (!privateMines.getMineStorage().hasWorldEditMine(uuid)) {
            player.sendMessage(doNotOwnMine);
        }
        worldEditMine = privateMines.getMineStorage().getWorldEditMine(uuid);
        worldEditMineData = worldEditMine.getWorldEditMineData();
        worldEditMineData.removeWhitelistedPlayer(targetUUID);
        player.sendMessage(YouHaveRemovedPlayerReplaced);
        target.sendMessage(YouHaveBeenUnwhitelistedPlayerReplaced);
        worldEditMine.setWorldEditMineData(worldEditMineData);
        privateMines.getMineStorage().replaceMine(uuid, worldEditMine);
    }
}

