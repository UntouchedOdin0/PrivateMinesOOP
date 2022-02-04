package me.untouchedodin0.plugin.debug;

import me.untouchedodin0.plugin.events.PrivateMineCreationEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TestEventThing implements Listener {

    @EventHandler
    public void onPrivateMineCreation(PrivateMineCreationEvent event) {
        Bukkit.getLogger().info("Mine: " + event.getMine());
        Bukkit.getLogger().info("Mine Type" + event.getMineType());
        event.setCancelled(true);
    }
}
