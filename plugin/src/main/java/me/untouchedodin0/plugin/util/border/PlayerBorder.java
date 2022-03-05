package me.untouchedodin0.plugin.util.border;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerHeldItemChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWorldBorder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerBorder {

    public void sendBorder(Player player, Location location) {
        WrapperPlayServerHeldItemChange packet = new WrapperPlayServerHeldItemChange(1);
        WrapperPlayServerWorldBorder wrapperPlayServerWorldBorder = new WrapperPlayServerWorldBorder(5);
        WrapperPlayServerWorldBorder test = new WrapperPlayServerWorldBorder(location.getX(), location.getY());

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, test);
    }
}
