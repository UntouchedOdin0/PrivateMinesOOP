/*
MIT License

Copyright (c) 2021 Kyle Hicks

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package me.untouchedodin0.privatemines;

import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mines.MineData;
import me.untouchedodin0.privatemines.storage.MineStorage;
import me.untouchedodin0.privatemines.world.MineWorldManager;
import me.untouchedodin0.privatemines.world.utils.MineLoopUtil;
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
    MineLoopUtil mineLoopUtil;

    private final Map<String, MineData> mineDataMap = new HashMap<>();

    @ConfigValue
    private String spawnPoint;

    @ConfigValue
    private String mineCorner;

    @ConfigValue
    private String sellNpc;

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
        mineLoopUtil = new MineLoopUtil();

        getLogger().info("config manager: " + configManager);
        getLogger().info("mine world manager: " + mineWorldManager);
        getLogger().info("Mine Storage: " + mineStorage);
        getLogger().info("Mine factory: " + mineFactory);

        mineTypes.forEach((string, mineConfig) -> {
            getLogger().info("Loading mine type... " + string);
        });

        int loaded = mineTypes.size();
        getLogger().info("Loaded a total of {loaded} mine types!"
                .replace("{loaded}",
                        String.valueOf(loaded)));

        mineBlocks.put(Material.STONE, 0.5);
        mineBlocks.put(Material.EMERALD_ORE, 0.5);

        mineBlocks2.put(Material.COBBLESTONE, 0.5);
        mineBlocks2.put(Material.GOLD_ORE, 0.5);

        mineDataMap.forEach((string, mineData) -> {
            getLogger().info("mineData Name: " + mineData.getName());
            getLogger().info("mineData Tier: " + mineData.getMineTier());
            getLogger().info("mineData Materials: " + mineData.getMaterials());
            getLogger().info("mineData Reset Time: " + mineData.getResetTime());
        });
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling Private Mines...");
        getLogger().info("Mine data map before: " + mineDataMap);
        mineDataMap.clear();
        getLogger().info("Mine data map after: " + mineDataMap);
    }

    public void addMineData(String name, MineData mineData) {
        mineDataMap.putIfAbsent(name, mineData);
    }

    public Map<String, MineData> getMineDataMap() {
        return mineDataMap;
    }

    public MineLoopUtil getMineLoopUtil() {
        return mineLoopUtil;
    }

    public String getSpawnMaterial() {
        return spawnPoint;
    }

    public String getCornerMaterial() {
        return mineCorner;
    }

    public String getSellNpcMaterial() {
        return sellNpc;
    }
}
