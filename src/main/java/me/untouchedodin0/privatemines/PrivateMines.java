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
import me.untouchedodin0.privatemines.mines.MineType;
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
import redempt.redlib.multiblock.MultiBlockStructure;
import redempt.redlib.multiblock.Structure;
import redempt.redlib.region.CuboidRegion;

import java.io.File;
import java.util.*;

public class PrivateMines extends JavaPlugin {

    private final Map<String, MineType> mineDataMap = new HashMap<>();
    private final TreeMap<String, MineType> mineTypeTreeMap = new TreeMap<>();
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
    Structure structure;

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

        if (!configFile.exists()) {
            saveDefaultConfig();
        }
//        if (!minesFile.exists()) {
//            boolean createdFile = false;
//            try {
//                createdFile = minesFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (createdFile) {
//                Bukkit.getLogger().info("Created the mines.yml file!");
//            }
//        }

        ConfigManager configManager = new ConfigManager(this).register(this).load();
        Bukkit.getLogger().info("minesFile: " + minesFile);

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

        // Loops all the data blocks
        blockDataManager.getAll().forEach(dataBlock -> {

            // Gets the mine type from the data block

            MineType mineType = getMineDataMap().get(dataBlock.getString("type"));

//            Mine mine = new Mine(this, utils);
//            MineData mineData = mineDataMap.get(dataBlock.getString("mineData"));
//            UUID uuid = UUID.fromString(dataBlock.getString("owner"));
//            mine.setMineOwner(uuid);
//            mine.setMineData(mineData);
//            getLogger().info("DataBlock: " + dataBlock);
//            getLogger().info("DataBlock Owner: " + dataBlock.get("owner"));
//            getLogger().info("DataBlock mine type name: " + dataBlock.get("type"));
//            getLogger().info("DataBlock mine location: " + dataBlock.get("location"));
//            getLogger().info("DataBlock spawnLocation location: " + dataBlock.get("spawnLocation"));
//            getLogger().info("DataBlock npcLocation location: " + dataBlock.get("npcLocation"));
//            getLogger().info("DataBlock corner1 location: " + dataBlock.get("corner1"));
//            getLogger().info("DataBlock corner2 location: " + dataBlock.get("corner2"));
//            getLogger().info("DataBlock structure structure: " + dataBlock.get("structure"));
//
//            String typeName = String.valueOf(dataBlock.getString("type"));
//            String locationName = String.valueOf(dataBlock.getString("location"));
//            String spawnLocationName = String.valueOf(dataBlock.getString("spawnLocation"));
//            String npcLocationName = String.valueOf(dataBlock.getString("npcLocation"));


//            Location location = LocationUtils.fromString(locationName);
//            Location spawnLocation = LocationUtils.fromString(spawnLocationName);
//            Location npcLocation = LocationUtils.fromString(npcLocationName);

            // Creates a new mine object
            Mine mine = new Mine(this);

            // Gets the player uuid string from the data block then converts it to a UUID
            UUID playerUUID = UUID.fromString(dataBlock.getString("owner"));

            // Gets the mine location string from the datablock then converts it to a bukkit Location
            Location location = LocationUtils.fromString(dataBlock.getString("location"));

            // The multi block structure for the mine initialized further on
            MultiBlockStructure multiBlockStructure;

//            this.structure = mine.getStructure();
//            this.structure = mineData.getStructure();
//            this.spawnLocation = mine.getRelative(relativeSpawn);
//            this.npcLocation = mine.getRelative(relativeNpc);
//            this.corner1 = mine.getRelative(relativeCorner1);
//            this.corner2 = mine.getRelative(relativeCorner2);


//            mine.setMineLocation(location);

//            this.spawnLocation = mineType.getSpawnLocation();
//            this.npcLocation = mineType.getNpcLocation();
//            this.corner1 = mineType.getCorner1();
//            this.corner2 = mineType.getCorner2();

//            CuboidRegion cuboidRegion = new CuboidRegion(mine.getRelative(corner1), mine.getRelative(corner2));

//            mine.setSpawnLocation(mine.getRelative(spawnLocation));
//            mine.setNpcLocation(mine.getRelative(npcLocation));
//            mine.setCuboidRegion(cuboidRegion);

            // Sets the mine owner and the mine type
            mine.setMineOwner(playerUUID);
            mine.setMineType(mineType);

            // Initialize the multi block structure from the mine type
            multiBlockStructure = mineType.getMultiBlockStructure();

            if (location == null) {
                getLogger().warning("Mine location was null, couldn't find the structure!");
                return;
            }

            // Initialize the structure by using the multi block structure to assume the structure is at a location

            this.structure = multiBlockStructure.assumeAt(location);

            privateMines.getLogger().info("Searching at location... " + location);
            privateMines.getLogger().info("multiblockstructure: " + multiBlockStructure);
            privateMines.getLogger().info("structure: " + structure);

            int[] relativeSpawn = mineType.getSpawnLocation();
            int[] relativeNpc = mineType.getNpcLocation();
            int[] relativeCorner1 = mineType.getCorner1();
            int[] relativeCorner2 = mineType.getCorner2();

            Location spawnLocation = utils.getRelative(structure, relativeSpawn);
            Location npcLocation = utils.getRelative(structure, relativeNpc);
            Location corner1Location = utils.getRelative(structure, relativeCorner1);
            Location corner2Location = utils.getRelative(structure, relativeCorner2);

            CuboidRegion cuboidRegion = new CuboidRegion(corner1Location, corner2Location);
            cuboidRegion.expand(1, 0, 1, 0, 1, 0);

            getLogger().info("spawnLocation: " + spawnLocation);
            getLogger().info("npcLocation: " + npcLocation);
            getLogger().info("corner1: " + corner1Location);
            getLogger().info("corner2: " + corner2Location);
            getLogger().info("cuboidRegion: " + cuboidRegion);

            mine.setSpawnLocation(spawnLocation);
            mine.setNpcLocation(npcLocation);
            mine.setCuboidRegion(cuboidRegion);
            mine.setMineOwner(playerUUID);

            getLogger().info("mine spawnLocation: " + mine.getSpawnLocation());
            getLogger().info("mine npcLocation: " + mine.getNpcLocation());
            getLogger().info("mine cuboidregion: " + mine.getCuboidRegion());
            getLogger().info("mine corner 1: " + mine.getCuboidRegion().getStart());
            getLogger().info("mine corner 2: " + mine.getCuboidRegion().getEnd());

            getLogger().info("mine owner: " + mine.getMineOwner());

            mine.reset();

            mineStorage.addMine(playerUUID, mine);

//            getLogger().info("playerUUID: " + playerUUID);
//            getLogger().info("mine: " + mine);
//            getLogger().info("mineData: " + mineType);
//            getLogger().info("mineLocation: " + mine.getMineLocation());
//            getLogger().info("spawnLocation: " + mine.getSpawnLocation());
//            getLogger().info("npcLocation: " + mine.getNpcLocation());
//            getLogger().info("spawnLocation: " + spawnLocation);
//            getLogger().info("npcLocation: " + npcLocation);
//            getLogger().info("cuboid region Start: " + mine.getCuboidRegion().getStart());
//            getLogger().info("cuboid region End: " + mine.getCuboidRegion().getEnd());
//            getLogger().info("cuboidRegion: " + mine.getCuboidRegion());

//            getLogger().info("mineData: " + mineData);
//            getLogger().info("mineLocation: " + location);
//            getLogger().info("spawnLocation: " + spawnLocation);
//            getLogger().info("npcLocation: " + npcLocation);
//            getLogger().info("corner1Location: " + corner1Location);
//            getLogger().info("corner2Location: " + corner2Location);
//            getLogger().info("cuboidRegion: " + cuboidRegion);

            Bukkit.getLogger().info("mines AFTER: " + mineStorage.getMines());
        });

        mineStorage.getMines().forEach(((uuid, mine) -> {
            getLogger().info("loading mine... " + mine);
            getLogger().info("mine owner: " + mine.getMineOwner());
            getLogger().info("mine type: " + mine.getMineType());

        }));

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

    public void addMineData(String name, MineType mineType) {
        mineDataMap.putIfAbsent(name, mineType);
        mineTypeTreeMap.put(name, mineType);
    }

    /*
        Gets a map of all the MineData types
     */

    public Map<String, MineType> getMineDataMap() {
        return mineDataMap;
    }

    /*
        Gets the default mine data
     */

    public MineType getDefaultMineType() {
        if (mineTypeTreeMap.isEmpty()) {
            Bukkit.getLogger().info("No default mine type was found!");
            Bukkit.getLogger().info("Create a mine type in the mineTypes");
            Bukkit.getLogger().info("section of the config.yml");
            Bukkit.getLogger().info("Please ask in the discord server" +
                    " if you need help");
            return null;
        }
        return mineTypeTreeMap.firstEntry().getValue();
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

    public MineType getNextMineType(String mineData) {
        return mineTypeTreeMap.higherEntry(mineData).getValue();
    }


    /*
        Gets the next MineData from the TreeMap using MineData
     */

    public MineType getNextMineType(MineType mineType) {
        MineType lastValue = mineTypeTreeMap.lastEntry().getValue();
        if (mineTypeTreeMap.higherEntry(mineType.getName()) == null) {
            return lastValue;
        }
        return mineTypeTreeMap.higherEntry(mineType.getName()).getValue();
    }

    /*
        Checks is the mine is currently fully maxed out
     */

    public boolean isAtLastMineType(MineType mineType) {
        MineType lastValue = mineTypeTreeMap.lastEntry().getValue();
        return mineType.equals(lastValue);
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
}