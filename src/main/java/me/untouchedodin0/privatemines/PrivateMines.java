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

import me.untouchedodin0.privatemines.commands.PrivateMinesCommand;
import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mines.MineData;
import me.untouchedodin0.privatemines.storage.MineStorage;
import me.untouchedodin0.privatemines.world.MineWorldManager;
import me.untouchedodin0.privatemines.world.utils.MineLoopUtil;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PrivateMines extends JavaPlugin {

    EnumMap<Material, Double> mineBlocks = new EnumMap<>(Material.class);
    EnumMap<Material, Double> mineBlocks2 = new EnumMap<>(Material.class);

    File configFile;
    MineLoopUtil mineLoopUtil;
    MineFactory mineFactory;
    MineStorage mineStorage;
    BlockDataManager blockDataManager;

    private final Map<String, MineData> mineDataMap = new HashMap<>();
    private final TreeMap<String, MineData> mineDataTreeMap = new TreeMap<>();

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

        blockDataManager = new BlockDataManager(
                getDataFolder()
                        .toPath()
                        .resolve("mines.db"));
        mineStorage = new MineStorage();
        mineFactory = new MineFactory(this, blockDataManager);
        mineLoopUtil = new MineLoopUtil();


        getLogger().info("config manager: " + configManager);
        getLogger().info("mine world manager: " + mineWorldManager);
        getLogger().info("Mine Storage: " + getMineStorage());
        getLogger().info("Mine factory: " + mineFactory);
        getLogger().info("BlockDataManager: " + blockDataManager);

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

        for (Map.Entry<String, MineData> entry : mineDataTreeMap.entrySet()) {
            getLogger().info("Tree Map Name: " + entry.getValue().getName());
            getLogger().info("Tree Map Tier: " + entry.getValue().getMineTier());
            getLogger().info("Tree Map Materials: " + entry.getValue().getMaterials());
            getLogger().info("Tree Map Reset Time: " + entry.getValue().getResetTime());
            getLogger().info(" ");
            if (mineDataTreeMap.lastKey().equals(entry.getKey())) {
                getLogger().info("You've reached the last entry!");
            } else {
                getLogger().info("the next entry after "
                        + entry.getValue().getName() + " is: "
                        + mineDataTreeMap.higherEntry(entry.getKey()));
            }
        }

        new CommandParser(this.getResource("command.rdcml"))
                .parse()
                .register("privatemines",
                        new PrivateMinesCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling Private Mines...");
        getLogger().info("Mine data map before: " + mineDataMap);
        mineDataMap.clear();
        getLogger().info("Mine data map after: " + mineDataMap);
        getLogger().info("Saving and closing the BlockDataManager...");
        blockDataManager.saveAndClose();
    }

    public void addMineData(String name, MineData mineData) {
        mineDataMap.putIfAbsent(name, mineData);
        mineDataTreeMap.put(name, mineData);
    }

    public Map<String, MineData> getMineDataMap() {
        return mineDataMap;
    }

    public MineData getDefaultMineData() {
        return mineDataTreeMap.firstEntry().getValue();
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

    public MineData getNextMineData(String mineData) {
        return mineDataTreeMap.higherEntry(mineData).getValue();
    }

    public MineFactory getMineFactory() {
        return mineFactory;
    }

    public MineStorage getMineStorage() { return mineStorage; }

    public BlockDataManager getBlockDataManager() { return blockDataManager; }
}
