package me.untouchedodin0.plugin.listener;

import me.untouchedodin0.plugin.events.PrivateMineCreationEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MineCreationTest implements Listener {

    @EventHandler
    public void onCreate(PrivateMineCreationEvent event) {
        Bukkit.broadcastMessage("Mine: " + event.getMine() + " has been created!");
    }
}
