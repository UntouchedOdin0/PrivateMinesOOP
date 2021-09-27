package me.untouchedodin0.privatemines.commands;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mines.Mine;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;

public class PrivateMinesCommand {

    private final MineFactory mineFactory;

    public PrivateMinesCommand(PrivateMines privateMine) {
        this.mineFactory = privateMine.getMineFactory();
    }

    @CommandHook("give")
    public void give(CommandSender commandSender, Player target) {
        commandSender.sendMessage("Giving " + target.getName() + " a private mine!");
        Mine mine = mineFactory.createMine(target, target.getLocation());
        mine.teleportPlayer(target);
    }
}
