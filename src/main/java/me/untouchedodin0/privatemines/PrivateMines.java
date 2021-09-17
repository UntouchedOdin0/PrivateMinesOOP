package me.untouchedodin0.privatemines;

import me.untouchedodin0.privatemines.mines.Mine;
import me.untouchedodin0.privatemines.mines.MineType;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;

public class PrivateMines extends JavaPlugin {

    EnumMap<Material, Double> mineBlocks = new EnumMap<>(Material.class);
    EnumMap<Material, Double> mineBlocks2 = new EnumMap<>(Material.class);

    @Override
    public void onEnable() {
        mineBlocks.put(Material.STONE, 0.5);
        mineBlocks.put(Material.EMERALD_ORE, 0.5);

        mineBlocks2.put(Material.COBBLESTONE, 0.5);
        mineBlocks2.put(Material.GOLD_ORE, 0.5);

        Mine mine = new Mine();
        MineType mineType = new MineType();
        mineType.setName("Default");
        mineType.setMineTier(1);
        mineType.setResetTime(5);
        mineType.setMaterials(mineBlocks);
        mine.setMineType(mineType);

        Mine mine2 = new Mine();
        MineType mineType2 = new MineType();
        mineType2.setName("Type 2");
        mineType2.setMineTier(2);
        mineType2.setResetTime(6);
        mineType2.setMaterials(mineBlocks2);
        mine2.setMineType(mineType2);

        System.out.println("mineType Name: " + mineType.getName());
        System.out.println("mineType Tier: " + mineType.getMineTier());
        System.out.println("mineType Materials: " + mineType.getMaterials());
        System.out.println("mineType Reset Time: " + mineType.getResetTime());

        System.out.println("mineType2 Name: " + mineType2.getName());
        System.out.println("mineType2 Tier: " + mineType2.getMineTier());
        System.out.println("mineType2 Materials: " + mineType2.getMaterials());
        System.out.println("mineType2 Reset Time: " + mineType2.getResetTime());
    }
}
