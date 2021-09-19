package me.untouchedodin0.privatemines;

import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.mines.Mine;
import me.untouchedodin0.privatemines.mines.MineData;
import me.untouchedodin0.privatemines.storage.MineStorage;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

public class PrivateMines extends JavaPlugin {

    EnumMap<Material, Double> mineBlocks = new EnumMap<>(Material.class);
    EnumMap<Material, Double> mineBlocks2 = new EnumMap<>(Material.class);

    File configFile;
    private ConfigManager configManager;

    @ConfigValue
    private Map<String, MineConfig> mineTypes = ConfigManager.map(MineConfig.class);

    @Override
    public void onEnable() {

        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        configManager = new ConfigManager(this).register(this).load();

        MineStorage mineStorage = new MineStorage();

        mineBlocks.put(Material.STONE, 0.5);
        mineBlocks.put(Material.EMERALD_ORE, 0.5);

        mineBlocks2.put(Material.COBBLESTONE, 0.5);
        mineBlocks2.put(Material.GOLD_ORE, 0.5);

        mineTypes.forEach((string, mineConfig) -> {
            Mine mine = new Mine();
            MineData mineData = new MineData();
            mineData.setName(string);
            mineData.setMineTier(mineConfig.getPriority());
            mineData.setResetTime(mineConfig.getResetTime());
            mineData.setMaterials(mineConfig.getMaterials());
            mine.setMineData(mineData);

//            System.out.println("mineData Name: " + mineData.getName());
//            System.out.println("mineData Tier: " + mineData.getMineTier());
//            System.out.println("mineData Materials: " + mineData.getMaterials());
//            System.out.println("mineData Reset Time: " + mineData.getResetTime());

            mineStorage.addMineData(string, mineData);
        });

        mineStorage.getMineDataMap().forEach((string, mineData) -> {
            System.out.println("mineData Name: " + mineData.getName());
            System.out.println("mineData Tier: " + mineData.getMineTier());
            System.out.println("mineData Materials: " + mineData.getMaterials());
            System.out.println("mineData Reset Time: " + mineData.getResetTime());
        });

        /*
        Mine mine = new Mine();
        MineData mineData = new MineData();
        mineData.setName("Default");
        mineData.setMineTier(1);
        mineData.setResetTime(5);
        mineData.setMaterials(mineBlocks);
        mine.setMineData(mineData);

        Mine mine2 = new Mine();
        MineData mineData2 = new MineData();
        mineData2.setName("Type 2");
        mineData2.setMineTier(2);
        mineData2.setResetTime(6);
        mineData2.setMaterials(mineBlocks2);
        mine2.setMineData(mineData2);

        System.out.println("mineData Name: " + mineData.getName());
        System.out.println("mineData Tier: " + mineData.getMineTier());
        System.out.println("mineData Materials: " + mineData.getMaterials());
        System.out.println("mineData Reset Time: " + mineData.getResetTime());

        System.out.println("mineData2 Name: " + mineData2.getName());
        System.out.println("mineData2 Tier: " + mineData2.getMineTier());
        System.out.println("mineData2 Materials: " + mineData2.getMaterials());
        System.out.println("mineData2 Reset Time: " + mineData2.getResetTime());

        mineStorage.addMine("staticName1", mineData);
        mineStorage.addMine("staticName2", mineData2);

        mineStorage.getMines().forEach((name, data) -> {
            System.out.println("For Each Name: " + name);
            System.out.println("For Each Data: " + data);
            System.out.println("For Each Tier: " + data.getMineTier());
            System.out.println("For Each Reset Time: " + data.getResetTime());
            System.out.println("For Each Materials: " + data.getMaterials());
        });
        */
    }
}

