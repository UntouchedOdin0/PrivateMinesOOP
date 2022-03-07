package me.untouchedodin0.plugin.listener;

import me.revils.autosell.CustomEvent.RevAutoSellSellEvent;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import redempt.redlib.commandmanager.Messages;

import java.util.Map;
import java.util.UUID;

public class RevAutoSellListener implements Listener {

    private final MineStorage mineStorage;
    private final Economy economy;

    public RevAutoSellListener(PrivateMines privateMines) {
        this.mineStorage = privateMines.getMineStorage();
        this.economy = privateMines.getEconomy();
    }

    @EventHandler
    public void onRevAutoSellAll(RevAutoSellSellEvent revAutoSellSellEvent) {
        double totalSold = revAutoSellSellEvent.getTotalSold();
        Map<UUID, Mine> mines = mineStorage.getMines();
        Player player = revAutoSellSellEvent.getPlayer();
        Location location = player.getLocation();

        String afterTaxString = "afterTax";
        String taxTakenString = "taxTaken";

        String afterTaxMessage = Messages.msg(afterTaxString);
        String taxTakenMessage = Messages.msg(taxTakenString);

        mines.forEach((uuid, mine) -> {
            if (mine.getMineOwner().equals(player.getUniqueId())) return; // We no tax owner! ;)
            if (mine.isInsideFullRegion(location) || mine.isInside(location)) {
                // BristerMitten code credit starts
                double tax = totalSold / 100.0 * mine.getTax();
                double totalCost = revAutoSellSellEvent.getTotalSold();
                double afterTax = totalCost - tax;

                String afterTaxAmount = String.valueOf(afterTax);

                revAutoSellSellEvent.setTotalSold(afterTax);
                economy.depositPlayer(Bukkit.getOfflinePlayer(mine.getMineOwner()), tax);
                // BristerMitten code credit ends
                player.sendMessage(Utils.sendColorMessage(afterTaxMessage.replace("%amount%", afterTaxAmount)));
                player.sendMessage(Utils.sendColorMessage(taxTakenMessage
                                                                  .replace("%amount%", String.valueOf(tax))
                                                                  .replace("%tax%", String.valueOf(mine.getTax()))));
            }
        });
    }
}
