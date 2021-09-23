package me.untouchedodin0.privatemines;

import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mines.MineData;
import me.untouchedodin0.privatemines.storage.MineStorage;
import me.untouchedodin0.privatemines.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class PrivateMines extends JavaPlugin {

    EnumMap<Material, Double> mineBlocks = new EnumMap<>(Material.class);
    EnumMap<Material, Double> mineBlocks2 = new EnumMap<>(Material.class);

    File configFile;

    private final Map<String, MineData> mineDataMap = new HashMap<>();

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

        mineTypes.forEach((string, mineConfig) -> {
            Bukkit.getLogger().info("Loading mine type... " + string);
        });

        mineBlocks.put(Material.STONE, 0.5);
        mineBlocks.put(Material.EMERALD_ORE, 0.5);

        mineBlocks2.put(Material.COBBLESTONE, 0.5);
        mineBlocks2.put(Material.GOLD_ORE, 0.5);

        mineDataMap.forEach((string, mineData) -> {
            System.out.println("mineData Name: " + mineData.getName());
            System.out.println("mineData Tier: " + mineData.getMineTier());
            System.out.println("mineData Materials: " + mineData.getMaterials());
            System.out.println("mineData Reset Time: " + mineData.getResetTime());
        });
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Disabling Private Mines...");
        Bukkit.getLogger().info("Mine data map before: " + mineDataMap);
        mineDataMap.clear();
        Bukkit.getLogger().info("Mine data map after: " + mineDataMap);
    }

    public void addMineData(String name, MineData mineData) {
        mineDataMap.putIfAbsent(name, mineData);
    }

    public Map<String, MineData> getMineDataMap() {
        return mineDataMap;
    }
}
