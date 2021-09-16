package me.untouchedodin0.privatemines;

import me.untouchedodin0.privatemines.mines.Mine;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class PrivateMines extends JavaPlugin {

    @Override
    public void onEnable() {
        Mine mine = new Mine();
        mine.name = "Mine";
        mine.material = Material.STONE;
        mine.resetTime = 1;
        mine.type = "Type";
        mine.mineLocation = null;

        System.out.println("Mine Name: " + mine.getName());
        System.out.println("Mine Material: " + mine.getMaterial());
        System.out.println("Mine Reset Time: " + mine.getResetTime());
        System.out.println("Mine Type: " + mine.getType());
        System.out.println("Cuboid Region: " + mine.getCuboidRegion());
        System.out.println("Mine Location: " + mine.getMineLocation());
    }
}
