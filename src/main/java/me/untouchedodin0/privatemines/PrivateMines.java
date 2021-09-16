package me.untouchedodin0.privatemines;

import me.untouchedodin0.privatemines.mines.Mine;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class PrivateMines extends JavaPlugin {

    @Override
    public void onEnable() {
        Mine mine = new Mine();
        mine.name = "Mine";
        mine.mineBlocks.put(Material.STONE, 0.1);
        mine.mineBlocks.put(Material.EMERALD_BLOCK, 0.9);
        mine.resetTime = 1;
        mine.type = "Type";
        mine.mineLocation = null;

        System.out.println("Mine Name: " + mine.getName());
        System.out.println("Mine Blocks: " + mine.getMineBlocks());
        System.out.println("Mine Reset Time: " + mine.getResetTime());
        System.out.println("Mine Type: " + mine.getType());
        System.out.println("Cuboid Region: " + mine.getCuboidRegion());
        System.out.println("Mine Location: " + mine.getMineLocation());
    }
}
