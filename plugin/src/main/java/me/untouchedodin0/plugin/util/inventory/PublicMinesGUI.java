package me.untouchedodin0.plugin.util.inventory;

import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.mines.data.MineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PublicMinesGUI {

    MineStorage mineStorage;

    public PublicMinesGUI(MineStorage mineStorage) {
        this.mineStorage = mineStorage;
    }

    public void open(Player player, int size, String inventoryTitle) {
        InventoryGUI inventoryGUI = new InventoryGUI(size, inventoryTitle);
        AtomicInteger slot = new AtomicInteger();

        mineStorage.getMines().forEach((uuid, mine) -> {
            Player owner = Bukkit.getOfflinePlayer(uuid).getPlayer();
            String playerName = owner.getName();
            MineData mineData = mine.getMineData();
            MineType mineType = mine.getMineType();
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
                                .setName(ChatColor.GREEN + playerName + "'s Mine")
                                .addLore(lore),
                        clickEvent -> {
                            player.sendMessage("Teleporting you to the mine");
                            mine.teleport(player);
                        }
                );
                inventoryGUI.addButton(slot.getAndIncrement(), itemButton);
            }
        });

        if (slot.get() == 0) {
            InventoryGUI noMinesOpen = new InventoryGUI(9, "No mines are open");
            ItemButton itemButton = ItemButton.create(
                    new ItemBuilder(Material.BARRIER)
                            .setName(ChatColor.RED + "No mines are open"), clickEvent -> {
                    });
            noMinesOpen.fill(0, 9, itemButton.getItem());
            noMinesOpen.open(player);
        } else {
            inventoryGUI.open(player);
        }
    }
}
