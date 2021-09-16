package me.untouchedodin0.privatemines;

import me.untouchedodin0.privatemines.mines.Mine;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;

public class PrivateMines extends JavaPlugin {

    EnumMap<Material, Double> mineBlocks = new EnumMap<>(Material.class);

    @Override
    public void onEnable() {
        mineBlocks.put(Material.STONE, 0.5);
        mineBlocks.put(Material.EMERALD_ORE, 0.5);

        Mine mine = new Mine();
        mine.setName("Mine");
        mine.setMineBlocks(mineBlocks);
        mine.setTier(1);
        mine.setResetTime(5);

        Mine mine2 = new Mine();
        mine2.setName("Mine2");
        mine2.setMineBlocks(mineBlocks);
        mine2.setTier(2);
        mine2.setResetTime(5);

        System.out.println("Mine Name: " + mine.getName());
        System.out.println("Mine Tier: " + mine.getMineTier());
        System.out.println("Mine Blocks: " + mine.getMineBlocks());
        System.out.println("Mine Reset Time: " + mine.getResetTime());

        System.out.println("Mine2 Name: " + mine2.getName());
        System.out.println("Mine2 Tier: " + mine2.getMineTier());
        System.out.println("Mine2 Blocks: " + mine2.getMineBlocks());
        System.out.println("Mine2 Reset Time: " + mine2.getResetTime());
    }
}
