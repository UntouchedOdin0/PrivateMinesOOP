package me.untouchedodin0.privatemines;

import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.storage.MineStorage;
import me.untouchedodin0.privatemines.world.MineWorldManager;
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

    @ConfigValue
    private Map<String, MineConfig> mineTypes = ConfigManager.map(MineConfig.class);

    @Override
    public void onEnable() {

        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        ConfigManager configManager = new ConfigManager(this).register(this).load();
        MineWorldManager mineWorldManager = new MineWorldManager();

        MineStorage mineStorage = new MineStorage();
        MineFactory mineFactory = new MineFactory(this, mineStorage);

        System.out.println("config manager: " + configManager);
        System.out.println("mine world manager: " + mineWorldManager);
        System.out.println("Mine Storage: " + mineStorage);
        System.out.println("Mine factory: " + mineFactory);

        mineBlocks.put(Material.STONE, 0.5);
        mineBlocks.put(Material.EMERALD_ORE, 0.5);

        mineBlocks2.put(Material.COBBLESTONE, 0.5);
        mineBlocks2.put(Material.GOLD_ORE, 0.5);

//        mineTypes.forEach((string, mineConfig) -> {
//            Bukkit.getLogger().info(string);
////            Bukkit.getLogger().info(mineConfig.getFile());
//
//            Mine mine = new Mine();
//            MineData mineData = new MineData();
//            mineData.setName(string);
//            mineData.setMineTier(mineConfig.getPriority());
//            mineData.setResetTime(mineConfig.getResetTime());
//            mineData.setMaterials(mineConfig.getMaterials());
//            mine.setMineData(mineData);
//        });





        /*
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
         */

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
