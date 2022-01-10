package me.untouchedodin0.privatemines.we_6.worldedit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MineFactory6 {

    public void sayHi() {
        Bukkit.getLogger().info("hi from 6");
    }

    public void createMine(Player player, Location location, WorldEdit6MineType worldEdit6MineType, boolean replaceOld) {
        Utils utils = new Utils();
        UUID uuid = player.getUniqueId();
        List<Location> corners = new ArrayList<>();


        player.sendMessage("player: " + player);
        player.sendMessage("location: " + location);
        player.sendMessage("worldEdit6MineType: " + worldEdit6MineType);
        player.sendMessage("replaceOld: " + replaceOld);
        player.sendMessage("utils: " + utils);
        player.sendMessage("uuid: " + uuid);
    }
}
