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

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.Gson;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.untouchedodin0.plugin.commands.PrivateMinesCommand;
import me.untouchedodin0.plugin.config.MineConfig;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.mines.WorldEditMine;
import me.untouchedodin0.plugin.mines.WorldEditMineType;
import me.untouchedodin0.plugin.mines.data.WorldEditMineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Metrics;
import me.untouchedodin0.plugin.util.Utils;
import me.untouchedodin0.plugin.util.addons.AddonLoader;
import me.untouchedodin0.plugin.util.placeholderapi.PrivateMinesExpansion;
import me.untouchedodin0.plugin.world.MineWorldManager;
import me.untouchedodin0.privatemines.compat.WorldEditUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class PrivateMines extends JavaPlugin {

    private static PrivateMines privateMines;
    private final Map<String, MineType> mineDataMap = new HashMap<>();
    private final TreeMap<String, MineType> mineTypeTreeMap = new TreeMap<>();
    private final TreeMap<String, WorldEditMineType> worldEditMineTypeTreeMap = new TreeMap<>();

    private MineFactory mineFactory;
    private MineWorldManager mineWorldManager;
    private MineStorage mineStorage;
    private BlockDataManager blockDataManager;
    private Utils utils;
    private PluginManager pluginManager;
    private AddonLoader addonLoader;
    private WorldEditUtilities worldEditUtils;
    private boolean isWorldEditEnabled = false;
    private final File minesDirectory = new File("plugins/PrivateMines/mines");
    private final File schematicsDirectory = new File("plugins/PrivateMines/schematics");
    private final File addonsDirectory = new File("plugins/PrivateMines/addons");

    File[] files;

    private final Pattern filePattern = Pattern.compile("(.*?)\\.(json)");
    private final Pattern jarPattern = Pattern.compile("(.*?)\\.(jar)");

    private Gson gson;
    private Material material;

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
        gson = new Gson();

        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        if (!minesDirectory.exists()) {
            boolean created = minesDirectory.mkdir();
            if (created) {
                getLogger().info("Created the mines directory successfully!");
            }
        }

        if (!schematicsDirectory.exists()) {
            boolean created = schematicsDirectory.mkdir();
            if (created) {
                getLogger().info("Created the schematics directory successfully!");
            }
        }

        if (!addonsDirectory.exists()) {
            boolean created = addonsDirectory.mkdir();
            if (created) {
                getLogger().info("Created addons directory successfully!");
            }
        }

        @SuppressWarnings("unused")
        ConfigManager configManager = new ConfigManager(this).register(this).load();

        blockDataManager = new BlockDataManager(
                getDataFolder()
                        .toPath()
                        .resolve("mines.db"));

        mineStorage = new MineStorage();
        mineFactory = new MineFactory(this, blockDataManager);
        mineWorldManager = new MineWorldManager();
        utils = new Utils(this);
        pluginManager = Bukkit.getServer().getPluginManager();
        addonLoader = new AddonLoader(this, pluginManager);

        String pluginFolder = getDataFolder().getPath();
        File folder = new File(pluginFolder);
        File[] files = folder.listFiles();
        privateMines.getLogger().info("files: " + Arrays.toString(Arrays.stream(files).toArray()));
        utils.moveSchematicFiles(files);

        File[] addons = addonsDirectory.listFiles();

        Plugin worldEditPlugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        int pluginId = 11413;

        int loaded = mineTypes.size();
        getLogger().info("Loaded a total of {loaded} mine types!"
                   .replace("{loaded}", String.valueOf(loaded)));

        if (useWorldEdit) {
            files = minesDirectory.listFiles();
            if (files != null) {
                Arrays.stream(files).forEach(file -> {
                    BufferedReader bufferedReader;
                    if (file.getName().matches(String.valueOf(filePattern))) {
                        getLogger().info("Found file: " + file + " using regex pattern: " + filePattern);
                        try {
                            bufferedReader = Files.newBufferedReader(file.toPath());
                            WorldEditMine worldEditMine = new WorldEditMine(this);

                            WorldEditMineData worldEditMineData = gson.fromJson(bufferedReader, WorldEditMineData.class);
                            if (XMaterial.matchXMaterial(worldEditMineData.getMaterial()).isPresent()) {
                                material = XMaterial.matchXMaterial(worldEditMineData.getMaterial()).get().parseMaterial();
                            }

                            int minX = worldEditMineData.getMinX();
                            int minY = worldEditMineData.getMinY();
                            int minZ = worldEditMineData.getMinZ();

                            int maxX = worldEditMineData.getMaxX();
                            int maxY = worldEditMineData.getMaxY();
                            int maxZ = worldEditMineData.getMaxZ();

                            privateMines.getLogger().info("worldEditMineData: " + worldEditMineData);
                            privateMines.getLogger().info("mineOwner: " + worldEditMineData.getMineOwner());
                            privateMines.getLogger().info("spawn x: " + worldEditMineData.getSpawnX());
                            privateMines.getLogger().info("spawn y: " + worldEditMineData.getSpawnY());
                            privateMines.getLogger().info("spawn z: " + worldEditMineData.getSpawnZ());

                            privateMines.getLogger().info("min x: " + worldEditMineData.getMinX());
                            privateMines.getLogger().info("min y: " + worldEditMineData.getMinY());
                            privateMines.getLogger().info("min z: " + worldEditMineData.getMinZ());

                            privateMines.getLogger().info("max x: " + worldEditMineData.getMaxX());
                            privateMines.getLogger().info("max y: " + worldEditMineData.getMaxY());
                            privateMines.getLogger().info("max z: " + worldEditMineData.getMaxZ());

                            privateMines.getLogger().info("region: " + worldEditMineData.getRegion());

                            privateMines.getLogger().info("region min x: " + worldEditMineData.getRegionMinX());
                            privateMines.getLogger().info("region min y: " + worldEditMineData.getRegionMinY());
                            privateMines.getLogger().info("region min z: " + worldEditMineData.getRegionMinZ());

                            privateMines.getLogger().info("region max x: " + worldEditMineData.getRegionMaxX());
                            privateMines.getLogger().info("region max y: " + worldEditMineData.getRegionMaxY());
                            privateMines.getLogger().info("region max z: " + worldEditMineData.getRegionMaxZ());

                            privateMines.getLogger().info("world Name: " + worldEditMineData.getWorldName());


    //                        privateMines.getLogger().info("material: " + worldEditMineData.getMaterial());

                            privateMines.getLogger().info("material: " + material.name());

                            Location spawn = new Location(Bukkit.getWorld(worldEditMineData.getWorldName()),
                                                          worldEditMineData.getSpawnX(), worldEditMineData.getSpawnY(), worldEditMineData.getSpawnZ());

                            BlockVector3 min = BlockVector3.at(minX, minY, minZ);
                            BlockVector3 max = BlockVector3.at(maxX, maxY, maxZ);

                            privateMines.getLogger().info("min: " + min);
                            privateMines.getLogger().info("max: " + max);

                            CuboidRegion cuboidRegion = new CuboidRegion(min, max);
                            privateMines.getLogger().info("spawn: " + spawn);
                            privateMines.getLogger().info("cuboidRegion: " + cuboidRegion);

                            worldEditMine.setSpawnLocation(spawn);
                            worldEditMine.setCuboidRegion(cuboidRegion);
                            worldEditMine.setWorldEditMineData(worldEditMineData);
                            worldEditMine.setMaterial(material);

                            mineStorage.addWorldEditMine(worldEditMineData.getMineOwner(), worldEditMine);

                            privateMines.getLogger().info("worldEditMine get data: " + worldEditMine.getWorldEditMineData());
                            privateMines.getLogger().info("mineStorage: " + mineStorage.getWorldEditMines());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


//            blockDataManager.getAll().forEach(dataBlock -> {
//                UUID uuid = UUID.fromString(dataBlock.getString("owner"));
//                Location location; //= LocationUtils.fromString(dataBlock.getString("location"));
//                Location spawnLocation = LocationUtils.fromString(dataBlock.getString("spawnLocation"));
//                String worldEditMineTypeName = dataBlock.getString("type");
//                WorldEditMineType worldEditMineType = worldEditMineTypeTreeMap.get(worldEditMineTypeName);
//
//                int corner1X = Integer.parseInt(dataBlock.getString("corner1X"));
//                int corner1Y = Integer.parseInt(dataBlock.getString("corner1Y"));
//                int corner1Z = Integer.parseInt(dataBlock.getString("corner1Z"));
//
//                int corner2X = Integer.parseInt(dataBlock.getString("corner2X"));
//                int corner2Y = Integer.parseInt(dataBlock.getString("corner2Y"));
//                int corner2Z = Integer.parseInt(dataBlock.getString("corner2Z"));
//
//                BlockVector3 corner1Vector = BlockVector3.at(corner1X, corner1Y, corner1Z);
//                BlockVector3 corner2Vector = BlockVector3.at(corner2X, corner2Y, corner2Z);
//
//                CuboidRegion cuboidRegion = new CuboidRegion(corner1Vector, corner2Vector);
//
//                WorldEditMine worldEditMine = new WorldEditMine(this);
//                worldEditMine.setMineOwner(uuid);
//                worldEditMine.setCuboidRegion(cuboidRegion);
//                worldEditMine.setLocation(location);
//                worldEditMine.setSpawnLocation(spawnLocation);
//                worldEditMine.setMaterial(Material.STONE);
//                worldEditMine.setWorld(minesWorld);
//                worldEditMine.setWorldEditMineType(worldEditMineType);
//                worldEditMine.setDataBlock(dataBlock);
//
//                mineStorage.addWorldEditMine(uuid, worldEditMine);
//            });
        } else {

            privateMines.getLogger().info("using redlib");
            //todo re add this back

            /*
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
             */
        }

        mineStorage.getMines().forEach(((uuid, mine) -> {
            String username = Bukkit.getOfflinePlayer(mine.getMineOwner()).getName();
            String loadingMessage = String.format("Loading %s's Mine!", username);
            getLogger().info(loadingMessage);
            mine.reset();
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

        //TODO FIX THIS
        if (addons != null) {
            Arrays.stream(addons).forEach(file -> {
                if (file.getName().matches(String.valueOf(jarPattern))) {
                    privateMines.getLogger().info("found addon file: " + file);
                    addonLoader.load(file);
                }
            });
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

    public void addType(String name, WorldEditMineType worldEditMineType) {
        worldEditMineTypeTreeMap.put(name, worldEditMineType);
    }

    /*
        Gets a map of all the MineData types
     */

    @SuppressWarnings("unused")
    public Map<String, MineType> getMineDataMap() {
        return mineDataMap;
    }

    /*
        Gets the default mine data
     */

    public MineType getDefaultMineType() {
        if (mineTypeTreeMap.isEmpty()) {
            Bukkit.getLogger().info(""" No default mine type was found! \n
                                        Create a mine type in the mine types section of the config.yml. \n
                                        Please ask in the discord server if you need help!"""
                                    );
            return null;
        }
        return mineTypeTreeMap.firstEntry().getValue();
    }

    /*
        Get default world edit type
     */

    public WorldEditMineType getDefaultWorldEditMineType() {
        if (worldEditMineTypeTreeMap.isEmpty()) {
            Bukkit.getLogger().info(""" No default world edit mine type was found!  \n
                                        Create a mine type in the mineTypes         \n
                                        section of the config.yml                   \n
                                        Please ask in the discord server if you need help!"""
                                    );
            return null;
        }
        return worldEditMineTypeTreeMap.firstEntry().getValue();
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

    @SuppressWarnings("unused")
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
        Gets the next MineData from the TreeMap using MineData
     */

    public WorldEditMineType getNextMineType(WorldEditMineType worldEditMineType) {
        WorldEditMineType lastValue = worldEditMineTypeTreeMap.lastEntry().getValue();
        if (mineTypeTreeMap.higherEntry(worldEditMineType.getName()) == null) {
            return lastValue;
        }
        return worldEditMineTypeTreeMap.higherEntry(worldEditMineType.getName()).getValue();
    }

    /*
        Checks is the mine is currently fully maxed out
     */

    public boolean isAtLastMineType(MineType mineType) {
        MineType lastValue = mineTypeTreeMap.lastEntry().getValue();
        return mineType.equals(lastValue);
    }

    /*
        Checks is the mine is currently fully maxed out
     */

    public boolean isAtLastMineType(WorldEditMineType worldEditMineType) {
        WorldEditMineType lastValue = worldEditMineTypeTreeMap.lastEntry().getValue();
        return worldEditMineType.equals(lastValue);
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

    public File getMinesDirectory() {
        return minesDirectory;
    }

    public File getSchematicsDirectory() {
        return schematicsDirectory;
    }

    public TreeMap<String, WorldEditMineType> getWorldEditMineTypeTreeMap() {
        return worldEditMineTypeTreeMap;
    }
}