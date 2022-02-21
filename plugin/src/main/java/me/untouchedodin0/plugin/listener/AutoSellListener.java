package me.untouchedodin0.plugin.listener;

import me.clip.autosell.events.AutoSellEvent;
import me.clip.autosell.events.SellAllEvent;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.storage.MineStorage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;

public class AutoSellListener implements Listener {

    private final PrivateMines privateMines;

    public AutoSellListener(PrivateMines privateMines) {
        this.privateMines = privateMines;
    }

    @EventHandler
    public void onSellAllEvent(SellAllEvent sellAllEvent) {
        Player player = sellAllEvent.getPlayer();
        MineStorage mineStorage = privateMines.getMineStorage();
        Map<UUID, Mine> mines = mineStorage.getMines();
        Location location = player.getLocation();

        mines.forEach((uuid, mine) -> {
            if (mine.getMineOwner().equals(player.getUniqueId())) return; // We no tax owner! ;)
            if (mine.isInsideFullRegion(location) || mine.isInside(location)) {
                // BristerMitten code credit starts
                double tax = sellAllEvent.getTotalCost() / 100.0 * mine.getTax();
                double totalCost = sellAllEvent.getTotalCost();
                double afterTax = totalCost - tax;

                sellAllEvent.setTotalCost(afterTax);
                privateMines.getLogger().info("After Tax: " + afterTax);
                // BristerMitten code credit ends
            }
        });
    }

    @EventHandler
    public void onAutoSellEvent(AutoSellEvent autoSellEvent) {
        Player player = autoSellEvent.getPlayer();
        MineStorage mineStorage = privateMines.getMineStorage();
        Map<UUID, Mine> mines = mineStorage.getMines();
        Location location = player.getLocation();

        mines.forEach((uuid, mine) -> {
            if (mine.getMineOwner().equals(player.getUniqueId())) return; // We no tax owner! ;)
            if (mine.isInsideFullRegion(location) || mine.isInside(location)) {
                // BristerMitten code credit starts
                double tax = autoSellEvent.getPrice() / 100.0 * mine.getTax();
                autoSellEvent.setMultiplier(1.0D - tax / 100.0D);
                // BristerMitten code credit ends
            }
        });
    }
}
