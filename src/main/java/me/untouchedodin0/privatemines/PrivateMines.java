package me.untouchedodin0.privatemines;

import me.untouchedodin0.privatemines.mines.Mine;
import org.bukkit.plugin.java.JavaPlugin;

public class PrivateMines extends JavaPlugin {

    public static void main(String[] args) {

        /*
        Mine mine = new Mine();
        mine.name = "Mine";
        mine.material = "Stone";
        mine.resetTime = 1;
        mine.type = "Type";

        System.out.println("Mine Name: " + mine.getName());
        System.out.println("Mine Material: " + mine.getMaterial());
        System.out.println("Mine Reset Time: " + mine.getResetTime());
        System.out.println("Mine Type: " + mine.getType());
        System.out.println("Cuboid Region: " + mine.getCuboidRegion());
         */
    }

    @Override
    public void onEnable() {
        Mine mine = new Mine();
        mine.name = "Mine";
        mine.material = "Stone";
        mine.resetTime = 1;
        mine.type = "Type";

        System.out.println("Mine Name: " + mine.getName());
        System.out.println("Mine Material: " + mine.getMaterial());
        System.out.println("Mine Reset Time: " + mine.getResetTime());
        System.out.println("Mine Type: " + mine.getType());
        System.out.println("Cuboid Region: " + mine.getCuboidRegion());
    }
}
