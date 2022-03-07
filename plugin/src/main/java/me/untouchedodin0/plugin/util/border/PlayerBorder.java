package me.untouchedodin0.plugin.util.border;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerBorder {

    int size = 0;
    public void sendBorder(Player player, Location location) {

        int centerX = location.getBlockX();
        int centerY = location.getBlockZ();

//        WrapperPlayServerHeldItemChange test = new WrapperPlayServerHeldItemChange(1);
//        PacketEvents.getAPI().getPlayerManager().sendPacket(player, test);
//        int originalSize = size--;
//
//        Task task = Task.syncRepeating(() -> {
//            size++;
//            WrapperPlayServerWorldBorder wrapperPlayServerWorldBorder = new WrapperPlayServerWorldBorder(originalSize, size, 1);
//            PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapperPlayServerWorldBorder);
//        }, 0L, 20L);

//        WrapperPlayServerHeldItemChange packet = new WrapperPlayServerHeldItemChange(1);
//        WrapperPlayServerWorldBorder wrapperPlayServerWorldBorder = new WrapperPlayServerWorldBorder(5);
//        WrapperPlayServerWorldBorder test = new WrapperPlayServerWorldBorder(location.getX(), location.getY());
//
//        PacketEvents.getAPI().getPlayerManager().sendPacket(player, test);
    }
}
