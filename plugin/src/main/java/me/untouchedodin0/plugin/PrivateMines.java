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

package me.untouchedodin0.plugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.untouchedodin0.plugin.commands.PrivateMinesCommand;
import me.untouchedodin0.plugin.config.MineConfig;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Metrics;
import me.untouchedodin0.plugin.util.Utils;
import me.untouchedodin0.plugin.util.placeholderapi.PrivateMinesExpansion;
import me.untouchedodin0.plugin.world.MineWorldManager;
import me.untouchedodin0.plugin.world.utils.MineLoopUtil;
import me.untouchedodin0.privatemines.compat.WorldEditUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigValue;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.multiblock.MultiBlockStructure;
import redempt.redlib.multiblock.Structure;
import redempt.redlib.region.CuboidRegion;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;


public class PrivateMines extends JavaPlugin {

    private static PrivateMines privateMines;
    private final Map<String, MineType> mineDataMap = new HashMap<>();
    private final TreeMap<String, MineType> mineTypeTreeMap = new TreeMap<>();

    private MineFactory mineFactory;
    private MineWorldManager mineWorldManager;
    private MineStorage mineStorage;
    private BlockDataManager blockDataManager;
    private Utils utils;
    private Structure structure;
    private WorldEditUtilities worldEditUtils;
    private boolean isWorldEditEnabled = false;

    @ConfigValue
    private String spawnPoint;

    @ConfigValue
    private String mineCorner;

    @ConfigValue
    private String sellNpc;

    @ConfigValue
    private String upgradeMaterial;

    @ConfigValue
    private boolean debugMode = false;

    @ConfigValue
    private boolean useWorldEdit = false;

    @ConfigValue
    private Map<String, MineConfig> mineTypes = ConfigManager.map(MineConfig.class);

    public static PrivateMines getPrivateMines() {
        return privateMines;
    }

    /*
        Disables the plugin, clears the map and saves the block data manager
     */

    @Override
    public void onEnable() {
        privateMines = this;
        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        ConfigManager configManager = new ConfigManager(this).register(this).load();

        blockDataManager = new BlockDataManager(
                getDataFolder()
                        .toPath()
                        .resolve("mines.db"));

        mineStorage = new MineStorage();
        mineFactory = new MineFactory(this, blockDataManager);
        mineWorldManager = new MineWorldManager();
        MineLoopUtil mineLoopUtil = new MineLoopUtil();
        utils = new Utils(this);
        Plugin worldEditPlugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        int pluginId = 11413;

        int loaded = mineTypes.size();
        getLogger().info("Loaded a total of {loaded} mine types!"
                .replace("{loaded}",
                        String.valueOf(loaded)));

        if (useWorldEdit) {
            getLogger().info("using w/e instead!");
        } else {

            // Loops all the data blocks
            blockDataManager.getAll().forEach(dataBlock -> {

                // Gets the mine type from the data block

                MineType mineType = getMineDataMap().get(dataBlock.getString("type"));

                // Creates a new mine object
                Mine mine = new Mine(this);

                // Gets the player uuid string from the data block then converts it to a UUID
                UUID playerUUID = UUID.fromString(dataBlock.getString("owner"));

                // Gets the mine location string from the datablock then converts it to a bukkit Location
                Location location = LocationUtils.fromString(dataBlock.getString("location"));

                // The multi block structure for the mine initialized further on
                MultiBlockStructure multiBlockStructure;

                // Sets the mine owner and the mine type
                mine.setMineOwner(playerUUID);
                mine.setMineType(mineType);

                // Initialize the multi block structure from the mine type
                multiBlockStructure = mineType.getMultiBlockStructure();

                // Initialize the structure by using the multi block structure to assume the structure is at a location

                this.structure = multiBlockStructure.assumeAt(location);

                // Get the relative locations
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

                mine.setSpawnLocation(spawnLocation);
                mine.setNpcLocation(npcLocation);
                mine.setCuboidRegion(cuboidRegion);
                mine.setMineOwner(playerUUID);
                mine.setStructure(structure);
                mine.reset();
                mine.startAutoResetTask();

                mineStorage.addMine(playerUUID, mine);
            });
        }

        mineStorage.getMines().forEach(((uuid, mine) -> {
            String username = Bukkit.getOfflinePlayer(mine.getMineOwner()).getName();
            String loadingMessage = String.format("Loading %s's Mine!", username);
            getLogger().info(loadingMessage);
        }));

        /*
            Does these things in order

            Sets up the private mines command
            Loads the plugins messages
         */

        new CommandParser(this.getResource("command.rdcml"))
                .setArgTypes(ArgType.of("material", Material.class))
                .parse()
                .register("privatemines",
                        new PrivateMinesCommand(this));
        Messages.load(this);
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SingleLineChart("mines", MineStorage::getLoadedMineSize));

        if (worldEditPlugin != null) {
            worldEditUtils = WorldEditUtilities.getInstance();
            getLogger().info("Loading worldedit v" + WorldEditPlugin.getPlugin(WorldEditPlugin.class)
                    .getDescription().getVersion());
            if (useWorldEdit) {
                isWorldEditEnabled = true;
            }
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("Connecting to placeholder api and registering the placeholders");
            new PrivateMinesExpansion().register();
        } else {
            getLogger().info("PlaceholderAPI was not present, not able to establish a hook!");
        }
        getLogger().info("upgradeMaterial: " + getUpgradeMaterial());
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling Private Mines...");
        mineDataMap.clear();
        getLogger().info("Saving and closing the BlockDataManager...");
        blockDataManager.getAll().forEach(dataBlock -> Bukkit.getLogger().info("Saving data block: " + dataBlock));
        blockDataManager.saveAndClose();
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
        Gets the upgrade material
     */

    public String getUpgradeMaterial() {
        return upgradeMaterial;
    }


    public MineType getMineType(String mineType) {
        MineType newType = mineTypeTreeMap.get(mineType);
        Bukkit.broadcastMessage("new type: " + newType);
        return newType;
    }

    /*
        Gets the next MineData from the TreeMap using String
     */

    public MineType getNextMineType(String mineType) {

        MineType lastValue = mineTypeTreeMap.lastEntry().getValue();
        if (mineTypeTreeMap.higherEntry(mineType) == null) {
            return lastValue;
        }
        return mineTypeTreeMap.higherEntry(mineType).getValue();
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

    public boolean isWorldEditEnabled() {
        return isWorldEditEnabled;
    }

    public boolean useWorldEdit() {
        return useWorldEdit;
    }

    public MineWorldManager getMineWorldManager() {
        return mineWorldManager;
    }

    public Utils getUtils() {
        return utils;
    }

    public WorldEditUtilities getWorldEditUtils() {
        return worldEditUtils;
    }
}