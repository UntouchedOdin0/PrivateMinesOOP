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

package me.untouchedodin0.plugin.commands;

//import com.cryptomorin.xseries.XBiome;
//import com.cryptomorin.xseries.XMaterial;

import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.config.Config;
import me.untouchedodin0.plugin.events.PrivateMineCreationEvent;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.mines.data.MineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.storage.TimeStorage;
import me.untouchedodin0.plugin.util.Utils;
import me.untouchedodin0.plugin.util.border.PlayerBorder;
import me.untouchedodin0.plugin.util.exceptions.MineAlreadyMaxedException;
import me.untouchedodin0.plugin.util.inventory.PublicMinesGUI;
import me.untouchedodin0.plugin.world.MineWorldManager;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.misc.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PrivateMinesCommand {

    private final MineFactory mineFactory;
    private final MineStorage mineStorage;
    private final TimeStorage timeStorage;
    private final MineWorldManager mineWorldManager;
    private final PrivateMines privateMines;

    Utils utils;

    public PrivateMinesCommand(PrivateMines privateMine) {
        this.privateMines = privateMine;
        this.mineFactory = privateMine.getMineFactory();
        this.mineStorage = privateMine.getMineStorage();
        this.timeStorage = privateMine.getTimeStorage();
        this.mineWorldManager = privateMine.getMineWorldManager();
        this.utils = privateMine.getUtils();
    }


    @CommandHook("main")
    public void mainHook(Player player) {
        //Map<String, MenuConfig> menuConfig = privateMines.getInventory();
        String inventoryTitle = Config.getMainMenuTitle();
        String yourMineTitle = Config.getYourMineTitle();

        String inventoryTitleColored = utils.color(inventoryTitle);
        String yourMineColoured = utils.color(yourMineTitle);

        InventoryGUI initialMenu = new InventoryGUI(9, inventoryTitleColored);
        InventoryGUI yourMine = new InventoryGUI(Bukkit.createInventory(null, 27, yourMineTitle));

        Mine mine = mineStorage.getMine(player.getUniqueId());
        PublicMinesGUI publicMinesGUI = new PublicMinesGUI(mineStorage);

        List<String> yourMineLore = new ArrayList<>();
        List<String> publicMinesLore = new ArrayList<>();
        List<Mine> openMines = new ArrayList<>();
        AtomicInteger slot = new AtomicInteger();
        AtomicInteger totalPublicMines = new AtomicInteger();

        ItemBuilder filler = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setName(" ");
//        ItemBuilder filler = new ItemBuilder(Objects.requireNonNull(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial())).setName(" ");
        ItemButton fillerButton = ItemButton.create(filler, e -> e.setCancelled(true));

        yourMineLore.add(ChatColor.GRAY + "Your mine :)");

        ItemBuilder yourMineBuilder = new ItemBuilder(Material.MINECART).setName(ChatColor.GREEN + "Your Mine").addLore(yourMineLore);
        ItemButton yourMineButton = ItemButton.create(yourMineBuilder, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            initialMenu.destroy();
            Mine mine1 = mineStorage.getMine(player.getUniqueId());

            InventoryGUI yourMineGui = new InventoryGUI(9, yourMineColoured);

            ItemButton teleport = ItemButton.create(
                    new ItemBuilder(Material.DIAMOND_BOOTS)
                            .setName(ChatColor.GREEN + "Click to teleport")
                            .addLore(ChatColor.GRAY + "to your mine"),
                    clickEvent -> {
                        clickEvent.setCancelled(true);
                        player.sendMessage(ChatColor.GREEN + "Teleporting you to the mine");
                        mine1.teleport(player);
                    });
            ItemButton reset = ItemButton.create(
                    new ItemBuilder(Material.STONE_BUTTON)
                            .setName(ChatColor.GREEN + "Click to reset")
                            .addLore(ChatColor.GRAY + "your mine"),
                    clickEvent -> {
                        clickEvent.setCancelled(true);
                        player.sendMessage(ChatColor.GREEN + "Resetting your mine");
                        mine1.reset();
                    });

            initialMenu.addButton(0, fillerButton);
            initialMenu.addButton(1, fillerButton);
            initialMenu.addButton(2, fillerButton);
            initialMenu.addButton(4, fillerButton);
            initialMenu.addButton(6, fillerButton);
            initialMenu.addButton(7, fillerButton);
            initialMenu.addButton(8, fillerButton);

            if (!mineStorage.hasMine(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You don't own a mine");
                player.closeInventory();
            } else {
                yourMineGui.addButton(0, fillerButton);
                yourMineGui.addButton(1, fillerButton);
                yourMineGui.addButton(2, fillerButton);
                yourMineGui.addButton(3, teleport);
                yourMineGui.addButton(4, fillerButton);
                yourMineGui.addButton(5, reset);
                yourMineGui.addButton(6, fillerButton);
                yourMineGui.addButton(7, fillerButton);
                yourMineGui.addButton(8, fillerButton);
                yourMineGui.open(player);
            }
        });

        publicMinesLore.add(ChatColor.GRAY + "Click to open");
        publicMinesLore.add(ChatColor.GRAY + "The public mines menu");
        ItemBuilder publicMinesBuilder = new ItemBuilder(Material.MINECART).setName(ChatColor.GREEN + "Public Mines").addLore(publicMinesLore);
        ItemButton publicMinesButton = ItemButton.create(publicMinesBuilder, inventoryClickEvent -> {
            initialMenu.destroy();
            player.closeInventory();
            mineStorage.getMines().forEach((uuid, mine1) -> {
                MineData mineData = mine1.getMineData();
                if (mineData.isOpen()) {
                    totalPublicMines.incrementAndGet();
                }
            });
            int inventorySize = utils.getInventorySize(totalPublicMines.get());

            //todo maybe move this to it's own utils method somewhere?

            InventoryGUI publicMines = new InventoryGUI(inventorySize, "Public Mines");

            mineStorage.getMines().forEach((uuid, mine1) -> {
                Player owner = Bukkit.getOfflinePlayer(uuid).getPlayer();
                String playerName = owner.getName();
                MineData mineData = mine1.getMineData();
                MineType mineType = mine1.getMineType();

                List<String> lore = new ArrayList<>();
                if (mineData.isOpen()) {
                    lore.add(ChatColor.WHITE + "Left Click to " + ChatColor.GREEN + "Teleport");
                    lore.add(ChatColor.WHITE + "Blocks: ");
                    if (!mineData.getMaterials().isEmpty()) {
                        mineData.getMaterials().forEach((material, aDouble) -> {
                            lore.add(material.name() + ": " + aDouble * 100 + "%");
                        });
                    } else {
                        mineType.getMaterials().forEach((material, aDouble) -> {
                            lore.add(Utils.prettify(material.name()) + ": " + aDouble * 100 + "%");
                        });
                    }

                    ItemButton itemButton = ItemButton.create(
                            new ItemBuilder(Material.EMERALD_BLOCK)
                                    .setName(ChatColor.GREEN + playerName + "'s mine")
                                    .addLore(lore),
                            clickEvent -> {
                                player.sendMessage(ChatColor.GREEN + "Teleporting you to the mine");
                                mine1.teleport(player);
                            });
                    publicMines.addButton(slot.getAndIncrement(), itemButton);
                }
            });
//            openMines.forEach(mine1 -> {
//                List<String> lore = new ArrayList<>();
//                if (!mine1.getMaterials().isEmpty()) {
//                    mine1.getMaterials().forEach((material, aDouble) -> {
//                        lore.add(material.name() + " " + aDouble);
//                    });
//                }
//
//                ItemBuilder mineItemBuilder = new ItemBuilder(Material.EMERALD_BLOCK).setName("TEST!").addLore(lore);
//                ItemButton itemButton = ItemButton.create(mineItemBuilder, inventoryClickEvent1 -> {
//                });
//                privateMines.getLogger().info(String.valueOf(slot.getAndIncrement()));
//                publicMines.addButton(slot.getAndIncrement(), itemButton);
//            });
            initialMenu.destroy();

            if (mine == null) {
                publicMinesGUI.open(player, inventorySize, "Public Mines");
            }

            //publicMines.open(player);
        });

        initialMenu.addButton(3, yourMineButton);
        initialMenu.addButton(5, publicMinesButton);
        initialMenu.addButton(0, fillerButton);
        initialMenu.addButton(1, fillerButton);
        initialMenu.addButton(2, fillerButton);
        initialMenu.addButton(4, fillerButton);
        initialMenu.addButton(6, fillerButton);
        initialMenu.addButton(7, fillerButton);
        initialMenu.addButton(8, fillerButton);
        initialMenu.open(player);

        /*
        menuConfig.forEach((s, c) -> {
            String name = utils.color(c.getName());
            List<String> lore = utils.color(c.getLore());

            int slot = c.getSlot();
            Material material = XMaterial.matchXMaterial(c.getType()).parseMaterial();
            ItemStack itemStack = new ItemStack(Objects.requireNonNull(material));
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta != null) {
                itemMeta.setDisplayName(name);
                itemMeta.setLore(lore);
            }

            itemStack.setItemMeta(itemMeta);

            ItemButton itemButton = ItemButton.create(itemStack, inventoryClickEvent -> {
                if (c.getSlot() == inventoryClickEvent.getSlot()) {
                    String action = c.getAction();
                    utils.doAction(player, mine, action);
                    player.closeInventory();
                }
            });
            gui.addButton(slot, itemButton);
        });
         */

        //gui.open(player);
    }

    @CommandHook("give")
    public void give(CommandSender commandSender, Player target, MineType mineType) {
        if (mineStorage.hasMine(target.getUniqueId())) {
            commandSender.sendMessage(ChatColor.RED + "User already has a mine!");
            return;
        }
        commandSender.sendMessage(ChatColor.GREEN + "Giving " + target.getName() + " a private mine!");
        Location location = mineWorldManager.getNextFreeLocation();
        final MineType defaultMineType = privateMines.getMineTypeManager().getDefaultMineType();

        //mineFactory.createMine(target, location, Objects.requireNonNullElse(mineType, defaultMineType), false);

        Thread thread = new Thread(() -> {
            mineFactory.createMine(target, location, Objects.requireNonNullElse(mineType, defaultMineType), false);
        });
        thread.start();

        Mine mine = mineStorage.getMine(target.getUniqueId());
        PrivateMineCreationEvent privateMineCreationEvent = new PrivateMineCreationEvent(mine);
        Bukkit.getPluginManager().callEvent(privateMineCreationEvent);
    }

    @CommandHook("delete")
    public void delete(CommandSender commandSender, Player target) {
        UUID uuid = target.getUniqueId();

        String deletedPlayersMine = "deletedPlayersMine";
        String yourMineHasBeenDeleted = "deletedMine";

        Path minesDirectory = privateMines.getMinesDirectory();
        String fileName = target.getUniqueId() + ".json";
        Path jsonFile = minesDirectory.resolve(fileName);

        if (!privateMines.getMineStorage().hasMine(uuid)) {
            commandSender.sendMessage(ChatColor.RED + "Player doesn't own a mine!");
            return;
        }
        Mine mine = privateMines.getMineStorage().getMine(uuid);
        Task task = mine.getTask();
        if (task == null) {
            privateMines.getLogger().warning("Failed to delete mine as the task was null!" + "\nThis usually happens when the task isn't running");
        } else {
            mine.delete();
            task.cancel();
            privateMines.getMineStorage().removeMine(uuid);

            try {
                Files.deleteIfExists(jsonFile);
            } catch (IOException e) {
                e.printStackTrace();
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

        if (!mineStorage.hasMine(uuid)) {
            utils.sendMessage(player, doNotOwnMine);
            return;
        }
        Mine mine = mineStorage.getMine(uuid);
        mine.reset();
        mine.teleport(player);
        utils.sendMessage(player, mineReset);
        utils.sendMessage(player, teleportedToMine);
    }

    @CommandHook("teleport")
    public void teleport(Player player) {
        UUID uuid = player.getUniqueId();
        String doNotOwnMine = "doNotOwnMine";
        String teleportedToMine = Messages.msg("teleportedToMine");

        if (!mineStorage.hasMine(uuid)) {
            utils.sendMessage(player, doNotOwnMine);
            return;
        }

        Mine mine = mineStorage.getMine(uuid);
        if (!mine.getSpawnLocation().getBlock().isEmpty()) {
            mine.getSpawnLocation().getBlock().setType(Material.AIR);
        }
        mine.teleport(player);
        player.sendMessage(teleportedToMine);
    }

    //todo this should really be tidied up into its own simple method somewhere lol.

    @CommandHook("teleportOther")
    public void teleportOther(Player player, Player target) {
        UUID uuid = target.getUniqueId();
        String targetDoesNotOwnMine = Messages.msg("targetDoesNotOwnMine");
        String notWhitelisted = Messages.msg("notWhitelisted");

        if (!mineStorage.hasMine(uuid)) {
            utils.sendMessage(player, targetDoesNotOwnMine);
            return;
        }

        Mine mine = mineStorage.getMine(uuid);
        MineData mineData = mine.getMineData();
        boolean isOpen = mineData.isOpen();

        if (mineData.getCoOwner() == null) {
            mine.teleport(player, isOpen);
        } else {
            UUID coowner = mineData.getCoOwner();
            boolean isCoOwner = coowner.equals(player.getUniqueId());
            mine.teleport(player, isOpen, isCoOwner);
        }
    }

    @CommandHook("upgrade")
    public void upgrade(CommandSender commandSender, Player target) {
        String targetDoesNotOwnMine = Messages.msg("targetDoesNotOwnMine");

        if (!mineStorage.hasMine(target.getUniqueId())) {
            utils.sendMessage(commandSender, targetDoesNotOwnMine);
            return;
        }

        Mine mine = mineStorage.getMine(target.getUniqueId());
        try {
            mine.upgrade();
        } catch (MineAlreadyMaxedException e) {
            e.printStackTrace();
        }
    }

    // Add 1 to whatever args you put so if you want to expand by one do /pmine expand 2

    @CommandHook("expand")
    public void expand(CommandSender commandSender, Player target, int amount) {
        Player player = (Player) commandSender;
        String targetDoesNotOwnMine = Messages.msg("targetDoesNotOwnMine");

        if (!mineStorage.hasMine(target.getUniqueId())) {
            utils.sendMessage(commandSender, targetDoesNotOwnMine);
            return;
        }
        Mine mine = mineStorage.getMine(target.getUniqueId());
        mine.expand(amount);
        mineStorage.replaceMine(player.getUniqueId(), mine);
    }

    @CommandHook("setblocks")
    public void setBlocks(CommandSender commandSender, Player target, Material[] materials) {
        String targetDoesNotOwnMine = Messages.msg("targetDoesNotOwnMine");

        if (!mineStorage.hasMine(target.getUniqueId())) {
            utils.sendMessage(commandSender, targetDoesNotOwnMine);
            return;
        }

        Mine mine = mineStorage.getMine(target.getUniqueId());
        MineData mineData = mine.getMineData();
        UUID uuid = target.getUniqueId();

        Map<Material, Double> mineBlocks = new EnumMap<>(Material.class);
        for (Material material : materials) {
            if (material.isSolid()) {
                mineBlocks.put(material, 1.0);
            } else {
                commandSender.sendMessage(ChatColor.RED + "You specified a non-solid block " + Utils.prettify(material.name()));
                return;
            }
        }
        mineData.setMaterials(mineBlocks);
        mine.setMineData(mineData);
        mineStorage.replaceMine(uuid, mine);
        mine.reset();
        utils.saveMineData(uuid, mineData);
    }

    @CommandHook("settype")
    public void setType(CommandSender commandSender, Player target, String type) {
        String invalidMineType = Messages.msg("invalidMineType");

        if (!mineStorage.hasMine(target.getUniqueId())) {
            utils.sendMessage(commandSender, Utils.sendColorMessage(invalidMineType));
            return;
        }

        UUID uuid = target.getUniqueId();
        Mine mine = mineStorage.getMine(target.getUniqueId());
        MineData mineData = mine.getMineData();
        MineType newType = privateMines.getMineTypeManager().getMineType(type);

        if (newType == null) {
            commandSender.sendMessage(Utils.colorStatic(invalidMineType));
            return;
        }

        mine.cancelResetTask();
        mineData.setMineType(type);
        mine.setMineType(newType);
        mine.setMineData(mineData);
        utils.saveMineData(uuid, mineData);
        mine.reset();
        mine.startResetTask();
        mineStorage.replaceMine(target.getUniqueId(), mine);
        mine.teleport(target);
    }

    @CommandHook("open")
    public void open(Player player) {
        Mine mine;
        MineData mineData;
        UUID uuid = player.getUniqueId();
        String doNotOwnMine = Messages.msg("doNotOwnMine");
        String mineAlreadyOpen = Messages.msg("mineAlreadyOpen");
        String mineOpened = Messages.msg("mineOpened");

        if (!mineStorage.hasMine(uuid)) {
            utils.sendMessage(player, doNotOwnMine);
            return;
        }

        mine = mineStorage.getMine(player.getUniqueId());
        mineData = mine.getMineData();
        boolean isOpen = mineData.isOpen();

        if (isOpen) {
            player.sendMessage(mineAlreadyOpen);
        } else {
            mineData.setOpen(true);
            mine.setMineData(mineData);
            privateMines.getMineStorage().replaceMine(uuid, mine);
            player.sendMessage(mineOpened);
        }
    }

    @CommandHook("close")
    public void close(Player player) {
        Mine mine;
        MineData mineData;
        UUID uuid = player.getUniqueId();
        String doNotOwnMine = Messages.msg("doNotOwnMine");
        String mineAlreadyClosed = Messages.msg("mineAlreadyClosed");
        String mineClosed = Messages.msg("mineClosed");

        if (!mineStorage.hasMine(uuid)) {
            utils.sendMessage(player, doNotOwnMine);
            return;
        }

        mine = mineStorage.getMine(player.getUniqueId());
        mineData = mine.getMineData();
        boolean isOpen = mineData.isOpen();

        if (!isOpen) {
            player.sendMessage(mineAlreadyClosed);
        } else {
            mineData.setOpen(false);
            mine.setMineData(mineData);
            privateMines.getMineStorage().replaceMine(uuid, mine);
            player.sendMessage(mineClosed);
        }
    }

    @CommandHook("whitelist")
    public void whitelist(Player player, Player target) {
        Mine mine;
        MineData mineData;
        UUID uuid = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        String doNotOwnMine = Messages.msg("doNotOwnMine");
        String youHaveBeenWhitelisted = Messages.msg("youHaveBeenWhitelistedAtMine");
        String youHaveAddedPlayerToYourMine = Messages.msg("youHaveAddedPlayerToYourMine");

        String replacedAdded = youHaveAddedPlayerToYourMine.replace("%name%", target.getName());
        String addedPlayerReplaced = replacedAdded.replace("%name%", player.getName());
        String addedReplaced = youHaveBeenWhitelisted.replace("%owner%", player.getName());

        if (!privateMines.getMineStorage().hasMine(uuid)) {
            player.sendMessage(doNotOwnMine);
        }
        mine = privateMines.getMineStorage().getMine(uuid);
        mineData = mine.getMineData();
        mineData.addWhitelistedPlayer(targetUUID);
        player.sendMessage(addedPlayerReplaced);
        target.sendMessage(addedReplaced);

        mine.setMineData(mineData);
        privateMines.getMineStorage().replaceMine(uuid, mine);
    }

    @CommandHook("unwhitelist")
    public void unWhitelist(Player player, Player target) {
        Mine mine;
        MineData mineData;
        UUID uuid = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        String doNotOwnMine = Messages.msg("doNotOwnMine");
        String youHaveBeenUnWhitelisted = Messages.msg("youHaveBeenUnWhitelistedFromUsersMine");
        String youHaveRemovedPlayerFromYourMine = Messages.msg("youHaveUnWhitelistedPlayerFromYourMine");

        String replacedYouHaveBeenUnwhitelisted = youHaveBeenUnWhitelisted.replace("%name%", target.getName());
        String youHaveBeenUnwhitelistedPlayerReplaced = replacedYouHaveBeenUnwhitelisted.replace("%name%", player.getName());

        String replacedYouHaveRemoved = youHaveRemovedPlayerFromYourMine.replace("%name%", target.getName());
        String youHaveRemovedPlayerReplaced = replacedYouHaveRemoved.replace("%name%", player.getName());

        if (!privateMines.getMineStorage().hasMine(uuid)) {
            player.sendMessage(doNotOwnMine);
        }
        mine = privateMines.getMineStorage().getMine(uuid);
        mineData = mine.getMineData();
        mineData.removeWhitelistedPlayer(targetUUID);
        mine.setMineData(mineData);
        privateMines.getMineStorage().replaceMine(uuid, mine);
        player.sendMessage(youHaveRemovedPlayerReplaced);
        target.sendMessage(youHaveBeenUnwhitelistedPlayerReplaced);
    }

    @CommandHook("ban")
    public void ban(Player player, Player target) {

        Mine mine;
        MineData mineData;
        UUID uuid = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        String doNotOwnMine = Messages.msg("doNotOwnMine");
        String youHaveBeenBanned = Messages.msg("youHaveBeenBanned");
        String youHaveBannedPlayer = Messages.msg("youHaveBannedPlayer");

        String replacedYouHaveBeenBanned = youHaveBeenBanned.replace("%name%", player.getName());
        String replacedYouHaveBannedPlayer = youHaveBannedPlayer.replace("%name%", target.getName());

        if (!privateMines.getMineStorage().hasMine(uuid)) {
            player.sendMessage(doNotOwnMine);
        }
        mine = privateMines.getMineStorage().getMine(uuid);
        mineData = mine.getMineData();
        mineData.addBannedPlayer(targetUUID);
        mine.setMineData(mineData);
        privateMines.getMineStorage().replaceMine(uuid, mine);
        player.sendMessage(replacedYouHaveBannedPlayer);
        target.sendMessage(replacedYouHaveBeenBanned);
    }

    @CommandHook("unban")
    public void unban(Player player, Player target) {

        Mine mine;
        MineData mineData;
        UUID uuid = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        String doNotOwnMine = Messages.msg("doNotOwnMine");
        String youHaveBeenUnbanned = Messages.msg("youHaveBeenUnbanned");
        String youHaveUnbannedPlayer = Messages.msg("youHaveUnbanned");

        String replacedYouHaveBeenUnbanned = youHaveBeenUnbanned.replace("%name%", player.getName());
        String replacedYouHaveUnbannedPlayer = youHaveUnbannedPlayer.replace("%name%", target.getName());

        if (!privateMines.getMineStorage().hasMine(uuid)) {
            player.sendMessage(doNotOwnMine);
        }
        mine = privateMines.getMineStorage().getMine(uuid);
        mineData = mine.getMineData();
        mineData.addBannedPlayer(targetUUID);
        mine.setMineData(mineData);
        privateMines.getMineStorage().replaceMine(uuid, mine);
        player.sendMessage(replacedYouHaveBeenUnbanned);
        target.sendMessage(replacedYouHaveUnbannedPlayer);
    }

    @CommandHook("coowner")
    public void coOwner(Player player, Player target) {
        Mine mine;
        MineData mineData;
        UUID uuid = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        String doNotOwnMine = Messages.msg("doNotOwnMine");

        String youHaveSetUserAsCoOwner = Messages.msg("youHaveSetUserAsCoOwner");
        String youHaveBeenSetAsACoOwnerAtMine = Messages.msg("youHaveBeenSetAsACoOwnerAtMine");

        String replacedYouHaveSetUserAsCoOwner = youHaveSetUserAsCoOwner.replace("%name%", target.getName());
        String replacedYouHaveBeenSetAsCoOwner = youHaveBeenSetAsACoOwnerAtMine.replace("%name%", player.getName());

        String replacedYouHaveSetUser = replacedYouHaveSetUserAsCoOwner.replace("%name%", player.getName());
        String replacedYouHaveBeenSet = replacedYouHaveBeenSetAsCoOwner.replace("%name%", target.getName());

        if (!privateMines.getMineStorage().hasMine(uuid)) {
            player.sendMessage(doNotOwnMine);
        }

        mine = privateMines.getMineStorage().getMine(uuid);
        mineData = mine.getMineData();
        mineData.setCoOwner(targetUUID);
        player.sendMessage(replacedYouHaveSetUser);
        target.sendMessage(replacedYouHaveBeenSet);
        mine.setMineData(mineData);
        privateMines.getMineStorage().replaceMine(uuid, mine);
    }

    @CommandHook("tax")
    public void tax(Player player, Double tax) {
        UUID uuid = player.getUniqueId();

        player.sendMessage(ChatColor.GREEN + "Setting your tax to " + tax);
        if (!mineStorage.hasMine(uuid)) {
            player.sendMessage(ChatColor.RED + "You can't set tax as you don't own a mine!");
        }
        Mine worldEditMine = mineStorage.getMine(uuid);
        worldEditMine.setTax(tax);
        mineStorage.replaceMine(uuid, worldEditMine);
    }

    @CommandHook("list")
    public void list(Player player) {
        InventoryGUI inventoryGUI = new InventoryGUI(Bukkit.createInventory(null, 27, "Public Mines"));
        AtomicInteger slot = new AtomicInteger();

        mineStorage.getMines().forEach((uuid, mine) -> {
            Player owner = Bukkit.getOfflinePlayer(uuid).getPlayer();
            String playerName = owner.getName();
            MineData mineData = mine.getMineData();
            if (mineData.isOpen()) {
                List<String> lore = List.of(ChatColor.WHITE + "Left Click to " + ChatColor.GREEN + "Teleport");
                ItemButton itemButton = ItemButton.create(new ItemBuilder(Material.EMERALD_BLOCK).setName(ChatColor.GREEN + playerName + "'s Mine").addLore(lore), inventoryClickEvent -> {
                    player.sendMessage(ChatColor.GREEN + "Teleporting you to the mine");
                    mine.teleport(player);
                });
                inventoryGUI.addButton(slot.getAndIncrement(), itemButton);
            }
        });
        inventoryGUI.open(player);
    }

    @CommandHook("info")
    public void info(Player player) {
        Map<UUID, Mine> mines = mineStorage.getMines();
        Location playerLocation = player.getLocation();
        String needToBeInAMine = Messages.msg("youNeedToBeInAMine");

        mines.forEach((uuid, mine) -> {
            MineData mineData = mine.getMineData();
            String playerName = Bukkit.getOfflinePlayer(mineData.getMineOwner()).getName();

            if (mine.isInsideFullRegion(playerLocation) || mine.isInside(playerLocation)) {
                player.sendMessage(ChatColor.GRAY + "Owner: " + ChatColor.GREEN + playerName);
                if (mineData.isOpen()) {
                    player.sendMessage(ChatColor.GRAY + "Status: " + ChatColor.GREEN + "Open");
                } else {
                    player.sendMessage(ChatColor.GRAY + "Status: " + ChatColor.RED + "Closed");
                }
                player.sendMessage(ChatColor.GRAY + "Tax: " + ChatColor.GREEN + mine.getTax());
                player.sendMessage(ChatColor.GRAY + "Mine Percentage: " + ChatColor.GREEN + mine.getPercentage() + "%");
                if (!mine.getMaterials().isEmpty()) {
                    mine.getMaterials().forEach((material, aDouble) -> player.sendMessage("" + ChatColor.GRAY + "-" + Utils.prettify(material.name() + " " + ChatColor.GREEN + aDouble + "%")));
                } else {
                    MineType mineType = mine.getMineType();
                    Map<Material, Double> materials = mineType.getMaterials();
                    materials.forEach((material, aDouble) -> player.sendMessage("" + ChatColor.GRAY + "- " + Utils.prettify(material.name() + " " + ChatColor.GREEN + Utils.toPercentage(aDouble) + "%")));
                }
            } else {
                player.sendMessage(needToBeInAMine);
            }
        });
    }

    @CommandHook("debug")
    public void debug(Player player) {

        /*
            total mines
            total mine types
            average mine creation time
            world edit version
            world guard wrapper version
            compiled red lib version
            percentage of all players owning a mine
         */


        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();

        int totalMines = mineStorage.getMinesCount();
        int totalMineTypes = privateMines.getMineTypeManager().getTotalMineTypes();

        int onlinePlayersCount = onlinePlayers.size();
        int offlinePlayersCount = offlinePlayers.length;

        List<Long> times = timeStorage.getTimes();

        PlayerBorder playerBorder = new PlayerBorder();
        playerBorder.sendBorder(player, player.getLocation());
    }

    @CommandHook("reload")
    public void reload(CommandSender commandSender) {
        ConfigManager configManager = privateMines.getConfigManager();
        configManager.reload();
    }
}

