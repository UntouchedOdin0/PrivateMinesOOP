package me.untouchedodin0.privatemines.commands;

import com.cryptomorin.xseries.XMaterial;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mines.Mine;
import me.untouchedodin0.privatemines.mines.MineType;
import me.untouchedodin0.privatemines.storage.MineStorage;
import me.untouchedodin0.privatemines.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.region.CuboidRegion;

public class PrivateMinesCommand {

    private final MineFactory mineFactory;
    private final MineStorage mineStorage;
    private final MineWorldManager mineWorldManager;
    private final PrivateMines privateMines;

    public PrivateMinesCommand(PrivateMines privateMine) {
        this.privateMines = privateMine;
        this.mineFactory = privateMine.getMineFactory();
        this.mineStorage = privateMine.getMineStorage();
        this.mineWorldManager = privateMine.getMineWorldManager();
    }

    @CommandHook("give")
    public void give(CommandSender commandSender, Player target) {
        if (mineStorage.hasMine(target.getUniqueId())) {
            Messages.msg("targetAlreadyOwnsAMine");
            return;
        }
        commandSender.sendMessage(ChatColor.GREEN + "Giving " + target.getName() + " a private mine!");
        Mine mine = mineFactory.createMine(target, mineWorldManager.getNextFreeLocation());
        Bukkit.getLogger().info("mine: " + mine);
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
        privateMines.getLogger().info("mineStorage: " + mineStorage);
        privateMines.getLogger().info("mineStorage mines: " + mineStorage.getMines());
        boolean hasMine = mineStorage.hasMine(player.getUniqueId());
        privateMines.getLogger().info("has mine: " + hasMine);
        Mine mine = mineStorage.getMine(player.getUniqueId());
        MineType mineType = mine.getMineType();
        CuboidRegion cuboidRegion = mine.getCuboidRegion();

        if (mineType.getWeightedRandom().getWeights().isEmpty()) {
            privateMines.getLogger().warning("There were no materials in the weighted random!");
            return;
        }

        privateMines.getLogger().info("mine: " + mine);

//        cuboidRegion.forEachBlock(block -> {
//            Material material = XMaterial.matchXMaterial(mineType.getWeightedRandom().roll()).parseMaterial();
//            if (material == null) {
//                privateMines.getLogger().warning("no material null bla bla bla");
//            }
//            if (material != null) {
//                block.setType(material);
//            }
//        });

//        if (!mineStorage.hasMine(player.getUniqueId())) {
//            Messages.msg("doNotOwnMine");
//            return;
//        }
        Messages.msg("mineReset");
        mine.reset();
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
}
