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

import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.config.MenuConfig;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.data.MineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Utils;
import me.untouchedodin0.plugin.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.misc.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PrivateMinesCommand {

    private final MineFactory mineFactory;
    private final MineStorage mineStorage;
    private final MineWorldManager mineWorldManager;
    private final PrivateMines privateMines;


    Utils utils;

    public PrivateMinesCommand(PrivateMines privateMine) {
        this.privateMines = privateMine;
        this.mineFactory = privateMine.getMineFactory();
        this.mineStorage = privateMine.getMineStorage();
        this.mineWorldManager = privateMine.getMineWorldManager();
        this.utils = privateMine.getUtils();
    }

    @CommandHook("main")
    public void mainHook(Player player) {
        Map<String, MenuConfig> menuConfig = privateMines.getInventory();
        String inventoryTitle = privateMines.getMainMenuTitle();
        String inventoryTitleColored = utils.color(inventoryTitle);

        InventoryGUI gui = new InventoryGUI(Bukkit.createInventory(null, 27, inventoryTitleColored));

        Mine mine = mineStorage.getMine(player.getUniqueId());
        if (mine == null) {
            player.sendMessage(Messages.msg("doNotOwnMine"));
            return;
        }

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
                    utils.doAction(player, mine, action);
                    player.closeInventory();
                }
            });
            gui.addButton(slot, itemButton);
        });

        gui.open(player);
    }

    @CommandHook("give")
    public void give(CommandSender commandSender, Player target) {
        if (mineStorage.hasMine(target.getUniqueId())) {
            commandSender.sendMessage(ChatColor.RED + "User already has a mine!");
            return;
        }
        commandSender.sendMessage(ChatColor.GREEN + "Giving " + target.getName() + " a private mine!");
        Location location = mineWorldManager.getNextFreeLocation();
        final MineType defaultMineType = privateMines.getMineTypeManager().getDefaultMineType();
        mineFactory.createMine(target, location, defaultMineType, false);
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
        Mine worldEditMine = privateMines.getMineStorage().getMine(uuid);
        Task task = worldEditMine.getTask();
        task.cancel();
        worldEditMine.delete();
        privateMines.getMineStorage().removeMine(uuid);

        try {
            Files.deleteIfExists(jsonFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        utils.sendMessage(commandSender, deletedPlayersMine);
        utils.sendMessage(target, yourMineHasBeenDeleted);
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
        MineData mineData = mine.getWorldEditMineData();

        UUID coowner = mineData.getCoOwner();
        boolean isCoOwner = coowner.equals(player.getUniqueId());

        List<UUID> whitelistedPlayers = mineData.getWhitelistedPlayers();
        boolean isOpen = mineData.isOpen();

        if (!isOpen) {
            boolean isWhitelisted = whitelistedPlayers.contains(player.getUniqueId());

            // If they're whitelisted, or they're co-owner they can enter.

            if (isWhitelisted || isCoOwner) {
                mine.teleport(player);
            } else {
                player.sendMessage(notWhitelisted);
            }
        } else {
            mine.teleport(player);
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
        mine.upgrade();
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
        Map<Material, Double> types = new EnumMap<>(Material.class);
        for (Material material : materials) {
            types.put(material, 1.0);
        }
        mine.setMineTypes(types);
        mine.reset();
    }

    @CommandHook("settype")
    public void setType(CommandSender commandSender, Player target, String type) {
        String invalidMineType = Messages.msg("invalidMineType");
        if (!mineStorage.hasMine(target.getUniqueId())) {
            utils.sendMessage(commandSender, Messages.msg("targetDoesNotOwnMine"));
            return;
        }

        Mine mine = mineStorage.getMine(target.getUniqueId());
        MineType newType = privateMines.getMineTypeManager().getMineType(type);
        if (newType == null) {
            utils.sendMessage(commandSender, invalidMineType);
            return;
        }

        mine.cancelResetTask();
        mine.setMineType(newType);
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
        mineData = mine.getWorldEditMineData();
        boolean isOpen = mineData.isOpen();

        if (isOpen) {
            player.sendMessage(mineAlreadyOpen);
        } else {
            mineData.setOpen(true);
            mine.setWorldEditMineData(mineData);
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
        mineData = mine.getWorldEditMineData();
        boolean isOpen = mineData.isOpen();

        if (!isOpen) {
            player.sendMessage(mineAlreadyClosed);
        } else {
            mineData.setOpen(false);
            mine.setWorldEditMineData(mineData);
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
        mineData = mine.getWorldEditMineData();
        mineData.addWhitelistedPlayer(targetUUID);
        player.sendMessage(addedPlayerReplaced);
        target.sendMessage(addedReplaced);

        mine.setWorldEditMineData(mineData);
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
        mineData = mine.getWorldEditMineData();
        mineData.removeWhitelistedPlayer(targetUUID);
        player.sendMessage(youHaveRemovedPlayerReplaced);
        target.sendMessage(youHaveBeenUnwhitelistedPlayerReplaced);
        mine.setWorldEditMineData(mineData);
        privateMines.getMineStorage().replaceMine(uuid, mine);
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
        mineData = mine.getWorldEditMineData();
        mineData.setCoOwner(targetUUID);
        player.sendMessage(replacedYouHaveSetUser);
        target.sendMessage(replacedYouHaveBeenSet);
        mine.setWorldEditMineData(mineData);
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
        player.sendMessage("" + mineStorage);
        player.sendMessage("" + mineStorage.getLoadedMinesSize());
        InventoryGUI inventoryGUI = new InventoryGUI(Bukkit.createInventory(null, 27, "Public Mines"));

        mineStorage.getMines().forEach((uuid, worldEditMine) -> {
            ItemButton itemButton = ItemButton.create(new ItemBuilder(Material.EMERALD_BLOCK).setName("Click me"), inventoryClickEvent -> {
                Player clickPlayer = (Player) inventoryClickEvent.getWhoClicked();
                clickPlayer.sendMessage("howdy " + this);
            });
            player.sendMessage(String.valueOf(worldEditMine.getTax()));
        });
        inventoryGUI.open(player);
    }

    @CommandHook("reload")
    public void reload(Player player) {
        ConfigManager configManager = privateMines.getConfigManager();
        configManager.load();
    }
}

