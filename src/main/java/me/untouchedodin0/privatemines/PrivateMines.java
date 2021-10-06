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
import me.untouchedodin0.privatemines.mines.Mine;
import me.untouchedodin0.privatemines.mines.MineData;
import me.untouchedodin0.privatemines.storage.MineStorage;
import me.untouchedodin0.privatemines.util.Metrics;
import me.untouchedodin0.privatemines.util.Utils;
import me.untouchedodin0.privatemines.world.MineWorldManager;
import me.untouchedodin0.privatemines.world.utils.MineLoopUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigValue;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.region.CuboidRegion;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PrivateMines extends JavaPlugin {

    private final Map<String, MineData> mineDataMap = new HashMap<>();
    private final TreeMap<String, MineData> mineDataTreeMap = new TreeMap<>();
    private static PrivateMines privateMines;

    EnumMap<Material, Double> mineBlocks = new EnumMap<>(Material.class);
    EnumMap<Material, Double> mineBlocks2 = new EnumMap<>(Material.class);
    File configFile;
    File minesFile;

    MineLoopUtil mineLoopUtil;
    MineFactory mineFactory;
    MineWorldManager mineWorldManager;
    MineStorage mineStorage;
    BlockDataManager blockDataManager;
    Utils utils;
    ConfigManager minesConfig;

    @ConfigValue
    private String spawnPoint;

    @ConfigValue
    private String mineCorner;

    @ConfigValue
    private String sellNpc;

    @ConfigValue
    private boolean debugMode = false;

    @ConfigValue
    private Map<String, MineConfig> mineTypes = ConfigManager.map(MineConfig.class);

//    @ConfigValue
//    private Map<String, StorageConfig> mines = ConfigManager.map(StorageConfig.class);

    @Override
    public void onEnable() {
        privateMines = this;
        configFile = new File(getDataFolder(), "config.yml");
        minesFile = new File(getDataFolder(), "mines.yml");

        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        if (!minesFile.exists()) {
            boolean createdFile = false;
            try {
                createdFile = minesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (createdFile) {
                Bukkit.getLogger().info("Created the mines.yml file!");
            }
        }

        ConfigManager configManager = new ConfigManager(this).register(this).load();
        this.minesConfig = new ConfigManager(minesFile).register(this).load();
        Bukkit.getLogger().info("minesFile: " + minesFile);
        Bukkit.getLogger().info("minesConfig: " + minesConfig);

        blockDataManager = new BlockDataManager(
                getDataFolder()
                        .toPath()
                        .resolve("mines.db"));

        mineStorage = new MineStorage();
        mineFactory = new MineFactory(this, blockDataManager);
        mineWorldManager = new MineWorldManager();
        mineLoopUtil = new MineLoopUtil();
        utils = new Utils(this);
        int pluginId = 11413;

        mineTypes.forEach((string, mineConfig) -> {
            getLogger().info("Loading mine type... " + string);
        });

        int loaded = mineTypes.size();
        getLogger().info("Loaded a total of {loaded} mine types!"
                .replace("{loaded}",
                        String.valueOf(loaded)));

//        mines.forEach((string, storageConfig) -> {
//            Bukkit.getLogger().info("Loading mine " + string +
//                    " from storage config " + storageConfig);
//        });

        mineBlocks.put(Material.STONE, 0.5);
        mineBlocks.put(Material.EMERALD_ORE, 0.5);

        mineBlocks2.put(Material.COBBLESTONE, 0.5);
        mineBlocks2.put(Material.GOLD_ORE, 0.5);

        Bukkit.getLogger().info("mines BEFORE: " + mineStorage.getMines());
        blockDataManager.getAll().forEach(dataBlock -> {
//            Mine mine = new Mine(this, utils);
//            MineData mineData = mineDataMap.get(dataBlock.getString("mineData"));
//            UUID uuid = UUID.fromString(dataBlock.getString("owner"));
//            mine.setMineOwner(uuid);
//            mine.setMineData(mineData);
            getLogger().info("DataBlock: " + dataBlock);
            getLogger().info("DataBlock Owner: " + dataBlock.get("owner"));
            getLogger().info("DataBlock mine type name: " + dataBlock.get("type"));
            getLogger().info("DataBlock mine location: " + dataBlock.get("location"));
            getLogger().info("DataBlock spawnLocation location: " + dataBlock.get("spawnLocation"));
            getLogger().info("DataBlock npcLocation location: " + dataBlock.get("npcLocation"));
            getLogger().info("DataBlock corner1 location: " + dataBlock.get("corner1"));
            getLogger().info("DataBlock corner2 location: " + dataBlock.get("corner2"));

            UUID playerUUID = UUID.fromString(dataBlock.getString("owner"));
            String typeName = String.valueOf(dataBlock.getString("type"));
            String locationName = String.valueOf(dataBlock.getString("location"));
            String spawnLocationName = String.valueOf(dataBlock.getString("spawnLocation"));
            String npcLocationName = String.valueOf(dataBlock.getString("npcLocation"));
            String corner1Name = String.valueOf(dataBlock.getString("corner1"));
            String corner2Name = String.valueOf(dataBlock.getString("corner2"));

            Location location = LocationUtils.fromString(locationName);
            Location spawnLocation = LocationUtils.fromString(spawnLocationName);
            Location npcLocation = LocationUtils.fromString(npcLocationName);
            Location corner1Location = LocationUtils.fromString(corner1Name);
            Location corner2Location = LocationUtils.fromString(corner2Name);

            CuboidRegion cuboidRegion = new CuboidRegion
                    (corner1Location, corner2Location);

            Mine mine = new Mine(this, utils);
            MineData mineData = getMineDataMap().get(typeName);
            mine.setMineOwner(playerUUID);
            mine.setMineData(mineData);
            mine.setMineLocation(location);
            mine.setSpawnLocation(spawnLocation);
            mine.setNpcLocation(npcLocation);
            mine.setCuboidRegion(cuboidRegion);
            mine.reset();

            mineStorage.addMine(playerUUID, mine);

            getLogger().info("playerUUID: " + playerUUID);
            getLogger().info("typeName: " + typeName);
            getLogger().info("mine: " + mine);
            getLogger().info("mineData: " + mineData);
            getLogger().info("mineLocation: " + mine.getMineLocation());
            getLogger().info("spawnLocation: " + mine.getSpawnLocation());
            getLogger().info("npcLocation: " + mine.getNpcLocation());
            getLogger().info("cuboid region Start: " + mine.getCuboidRegion().getStart());
            getLogger().info("cuboid region End: " + mine.getCuboidRegion().getEnd());
            getLogger().info("cuboidRegion: " + mine.getCuboidRegion());

//            getLogger().info("mineData: " + mineData);
//            getLogger().info("mineLocation: " + location);
//            getLogger().info("spawnLocation: " + spawnLocation);
//            getLogger().info("npcLocation: " + npcLocation);
//            getLogger().info("corner1Location: " + corner1Location);
//            getLogger().info("corner2Location: " + corner2Location);
//            getLogger().info("cuboidRegion: " + cuboidRegion);

            Bukkit.getLogger().info("mines AFTER: " + mineStorage.getMines());
        });

        // Loads the mines back after each reboot (fixes vanishing mines)

//        mines.forEach((string, storageConfig) -> {
//            Bukkit.getLogger().info("Loading mine " + string + " back!");
//        });

//        if (!minesConfig.configExists()) {
//            return;
//        } else {
//            if (minesConfig.getConfig().getConfigurationSection("mines") == null) {
//                getLogger().info("No mines to load!");
//            } else {
//                for (String mine : Objects.requireNonNull(minesConfig
//                        .getConfig()
//                        .getConfigurationSection("mines"))
//                        .getKeys(true)) {
//                    Bukkit.getLogger().info("config mines for loop : " + mine);
//                }
//            }
//        }

        /*
            Does these things in order

            Sets up the private mines command
            Loads the plugins messages
         */

        new CommandParser(this.getResource("command.rdcml"))
                .parse()
                .register("privatemines",
                        new PrivateMinesCommand(this));
        Messages.load(this);
        Metrics metrics = new Metrics(this, pluginId);
    }

    /*
        Disables the plugin, clears the map and saves the block data manager
     */

    @Override
    public void onDisable() {
        getLogger().info("Disabling Private Mines...");
        getLogger().info("Mine data map before: " + mineDataMap);
        mineDataMap.clear();
        getLogger().info("Mine data map after: " + mineDataMap);
        getLogger().info("Saving and closing the BlockDataManager...");
        blockDataManager.getAll().forEach(dataBlock -> {
            Bukkit.getLogger().info("Saving data block: " + dataBlock);
        });
        blockDataManager.saveAndClose();
    }

    public static PrivateMines getPrivateMines() {
        return privateMines;
    }

    /*
        Adds a MineData to the maps
     */

    public void addMineData(String name, MineData mineData) {
        mineDataMap.putIfAbsent(name, mineData);
        mineDataTreeMap.put(name, mineData);
    }

    /*
        Gets a map of all the MineData types
     */

    public Map<String, MineData> getMineDataMap() {
        return mineDataMap;
    }

    /*
        Gets the default mine data
     */

    public MineData getDefaultMineData() {
        if (mineDataTreeMap.isEmpty()) {
            Bukkit.getLogger().info("No default mine data was found!");
            Bukkit.getLogger().info("Create a mine type in the mineTypes");
            Bukkit.getLogger().info("section of the config.yml");
            Bukkit.getLogger().info("Please ask in the discord server" +
                    " if you need help");
            return null;
        }
        return mineDataTreeMap.firstEntry().getValue();
    }

    /*
        Gets the spawn material
     */

    public String getSpawnMaterial() {
        return spawnPoint;
    }

    /*
        Gets the corner material
     */

    public String getCornerMaterial() {
        return mineCorner;
    }

    /*
        Gets the sell npc material
     */

    public String getSellNpcMaterial() {
        return sellNpc;
    }

    /*
        Gets the next MineData from the TreeMap using String
     */

    public MineData getNextMineData(String mineData) {
        return mineDataTreeMap.higherEntry(mineData).getValue();
    }


    /*
        Gets the next MineData from the TreeMap using MineData
     */

    public MineData getNextMineData(MineData mineData) {
        MineData lastValue = mineDataTreeMap.lastEntry().getValue();
        if (mineDataTreeMap.higherEntry(mineData.getName()) == null) {
            return lastValue;
        }
        return mineDataTreeMap.higherEntry(mineData.getName()).getValue();
    }

    /*
        Checks is the mine is currently fully maxed out
     */

    public boolean isAtLastMineData(MineData mineData) {
        MineData lastValue = mineDataTreeMap.lastEntry().getValue();
        return mineData.equals(lastValue);
    }

    /*
        Gets the Mine Factory.
     */

    public MineFactory getMineFactory() {
        return mineFactory;
    }

    /*
        Gets the mine storage
     */

    public MineStorage getMineStorage() {
        return mineStorage;
    }

    /*
        Gets the block data manager
     */

    public BlockDataManager getBlockDataManager() {
        return blockDataManager;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public MineWorldManager getMineWorldManager() {
        return mineWorldManager;
    }

    public Utils getUtils() {
        return utils;
    }

    public ConfigManager getMinesConfig() {
        return minesConfig;
    }
}