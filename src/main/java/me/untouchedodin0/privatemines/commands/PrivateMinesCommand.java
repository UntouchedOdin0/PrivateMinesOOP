package me.untouchedodin0.privatemines.commands;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mines.Mine;
import me.untouchedodin0.privatemines.storage.MineStorage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;

public class PrivateMinesCommand {

    private final MineFactory mineFactory;
    private final MineStorage mineStorage;

    public PrivateMinesCommand(PrivateMines privateMine) {
        this.mineFactory = privateMine.getMineFactory();
        this.mineStorage = privateMine.getMineStorage();
    }

    @CommandHook("give")
    public void give(CommandSender commandSender, Player target) {
        commandSender.sendMessage("Giving " + target.getName() + " a private mine!");
        Mine mine = mineFactory.createMine(target, target.getLocation());
        mine.teleportPlayer(target);
    }

    @CommandHook("delete")
    public void delete(CommandSender commandSender, Player target) {
        commandSender.sendMessage("Deleting " + target.getName() + "'s Private Mine");
        Mine mine = mineStorage.getMine(target.getUniqueId());
        mine.delete();
    }
}
