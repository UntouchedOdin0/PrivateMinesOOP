/*
MIT License

Copyright (c) 2021 - 2022 Kyle Hicks

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

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.google.gson.Gson;
import me.untouchedodin0.plugin.commands.PrivateMinesCommand;
import me.untouchedodin0.plugin.config.Config;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.mines.MineTypeManager;
import me.untouchedodin0.plugin.mines.data.MineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Exceptions;
import me.untouchedodin0.plugin.util.Metrics;
import me.untouchedodin0.plugin.util.Utils;
import me.untouchedodin0.plugin.util.placeholderapi.PrivateMinesExpansion;
import me.untouchedodin0.plugin.world.MineWorldManager;
import me.untouchedodin0.privatemines.compat.WorldEditAdapter;
import me.untouchedodin0.privatemines.compat.WorldEditCompatibility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import redempt.redlib.RedLib;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.region.CuboidRegion;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PrivateMines extends JavaPlugin {

    private static final int PLUGIN_ID = 11413;
    private static final int SPIGOT_PLUGIN_ID = 90890;
    private static PrivateMines privateMines;
    private static PrivateMinesAPI privateMinesAPI;

    private final Path minesDirectory = getDataFolder().toPath().resolve("mines");
    private final Path schematicsDirectory = getDataFolder().toPath().resolve("schematics");
    private final File addonsDirectory = new File("plugins/PrivateMines/addons");
    private final Pattern jarPattern = Pattern.compile("(.*?)\\.(jar)");
    IWrappedRegion globalRegion;
    private MineTypeManager mineTypeManager;
    private MineFactory mineFactory;
    private MineWorldManager mineWorldManager;
    private MineStorage mineStorage;
    private Utils utils;
    private ConfigManager configManager;
    private Gson gson;
    private WorldEditAdapter worldEditAdapter;
    private WorldBorderApi worldBorderApi;

    public WorldEditAdapter getWorldEditAdapter() {
        return worldEditAdapter;
    }

//    @ConfigValue
//    private Map<String, MineConfig> mineTypes = ConfigManager.map(MineConfig.class);
//
//    @ConfigValue
//    private Map<String, MenuConfig> inventory = ConfigManager.map(MenuConfig.class);

    public static PrivateMines getPrivateMines() {
        return privateMines;
    }

    public static PrivateMinesAPI getAPI() {
        return privateMinesAPI;
    }

    @Override
    public void onEnable() {
        privateMines = this;
        privateMinesAPI = new PrivateMinesAPI(privateMines);
        gson = new Gson();

        saveDefaultConfig();

        try {
            Files.createDirectories(minesDirectory);
            Files.createDirectories(schematicsDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (RedLib.MID_VERSION < 13) {
            // Save the schematic file, this format is used pre-1.13
            saveResource("schematics/mine.schematic", false);
        } else {
            // Save the schem file this format is used in 1.13 and beyond.
            saveResource("schematics/mine.schem", false);
        }

        mineWorldManager = new MineWorldManager(this);
        utils = new Utils(this);

        this.worldEditAdapter = WorldEditCompatibility.loadWorldEdit();
        if (worldEditAdapter == null) {
            getLogger().warning("WorldEdit is not enabled! Disabling PrivateMines!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        mineStorage = new MineStorage();
        mineFactory = new MineFactory(this);
        mineTypeManager = new MineTypeManager(this);
        //configManager = new ConfigManager(this).register(this, Mine.class).load();
        //configManager = ConfigManager.create(this).target(MineConfig.class).saveDefaults().load();
        configManager = ConfigManager.create(this).target(Config.class).saveDefaults().load();

        RegisteredServiceProvider<WorldBorderApi> worldBorderApiRegisteredServiceProvider = Bukkit.getServer().getServicesManager().getRegistration(WorldBorderApi.class);

        this.worldBorderApi = BorderAPI.getApi(); // this keeps returning null.

        ArgType<MineType> mineTypes = new ArgType<>("mineType", mineTypeManager::getMineType);

        try {
            final List<Path> files = Files.list(getDataFolder().toPath())
                    .collect(Collectors.toList());
            utils.moveSchematicFiles(files);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            loadMines();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load mines!", e);
        }

        /*
            Does these things in order

            Sets up the private mines command
            Loads the plugins messages
         */

        new CommandParser(this.getResource("command.rdcml"))
                .setArgTypes(ArgType.of("material", Material.class),
                             ArgType.of("mineType", mineTypeManager.getMineTypes()))
                .parse()
                .register("privatemines",
                        new PrivateMinesCommand(this));

        Messages.load(this);

        Metrics metrics = new Metrics(this, PLUGIN_ID);
        metrics.addCustomChart(new Metrics.SingleLineChart("mines", mineStorage::getLoadedMinesSize));

        //TODO FIX THIS
        File[] addons = addonsDirectory.listFiles();
        if (addons != null) {
            Arrays.stream(addons).forEach(file -> {
                if (file.getName().matches(String.valueOf(jarPattern))) {
                    privateMines.getLogger().info("found addon file: " + file);
                }
            });
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("Connecting to placeholder api and registering the placeholders");
            new PrivateMinesExpansion().register();
        } else {
            getLogger().info("PlaceholderAPI was not present, not able to establish a hook!");
        }

        World world = getMineWorldManager().getMinesWorld();
        final Optional<IWrappedRegion> global = WorldGuardWrapper.getInstance().getRegion(world, "__global__");
        if (global.isPresent()) {
            globalRegion = global.get();
            utils.setGlobalFlags(globalRegion);
        } else {
            privateMines.getLogger().severe("The global region was somehow null. This should be " +
                                            "impossible.");
        }

//        if (notifyForUpdates) {
//            UpdateChecker.init(this, SPIGOT_PLUGIN_ID).checkEveryXHours(1).setDownloadLink(SPIGOT_PLUGIN_ID).checkNow();
//        }
    }

    private void loadMines() throws IOException {
        final PathMatcher jsonMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.json");
        AtomicInteger loadedMineCount = new AtomicInteger();
        Files.list(minesDirectory)
                .filter(jsonMatcher::matches)
                .map(Exceptions.throwing(Files::readAllLines))
                .map(lines -> String.join("\n", lines))
                .forEach(file -> {
                    Mine mine = new Mine(this);

                    MineData mineData = gson.fromJson(file, MineData.class);
                    int minX = mineData.getMinX();
                    int minY = mineData.getMinY();
                    int minZ = mineData.getMinZ();
                    int maxX = mineData.getMaxX();
                    int maxY = mineData.getMaxY();
                    int maxZ = mineData.getMaxZ();

                    final World world = Bukkit.getWorld(mineData.getWorldName());
                    if (world == null) {
                        throw new IllegalStateException("World " + mineData.getWorldName() + " does not exist!");
                    }
                    Location spawn = new Location(world,
                            mineData.getSpawnX(), mineData.getSpawnY(), mineData.getSpawnZ());

                    Location min =
                            new Location(world, minX, minY, minZ);
                    Location max =
                            new Location(world, maxX, maxY, maxZ);

                    CuboidRegion cuboidRegion = new CuboidRegion(min, max);

                    mine.setSpawnLocation(spawn);
                    mine.setRegion(mineData.getFullRegion());
                    mine.setMiningRegion(cuboidRegion);
                    mine.setMineData(mineData);
                    mine.setMineTypes(mineData.getMaterials());
                    mine.setMineType(mineTypeManager.getMineType(mineData.getMineType()));
                    mine.setMineOwner(mineData.getMineOwner());
                    mine.startResetTask();
                    mineStorage.addMine(mineData.getMineOwner(), mine);
                    mineWorldManager.getNextFreeLocation();
                    loadedMineCount.incrementAndGet();
                });

        getLogger().info(() -> "Loaded " + loadedMineCount.get() + " mines");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling Private Mines...");
        mineTypeManager.clear();
        getLogger().info("Saving and closing the BlockDataManager...");
    }

    public MineTypeManager getMineTypeManager() {
        return mineTypeManager;
    }

//    public Material getSpawnMaterial() {
//        return spawnPoint;
//    }
//
//    public Material getCornerMaterial() {
//        return mineCorner;
//    }
//
//    public String getMainMenuTitle() {
//        return mainMenuTitle;
//    }

    /*
        Gets the mine storage
     */

    public MineFactory getMineFactory() {
        return mineFactory;
    }

    /*
        Gets the block data manager
     */

    public MineStorage getMineStorage() {
        return mineStorage;
    }


    public int getMineDistance() {
        return 150; // need to get from new config (somehow)
    }

    public MineWorldManager getMineWorldManager() {
        return mineWorldManager;
    }

    public Utils getUtils() {
        return utils;
    }

    public Path getMinesDirectory() {
        return minesDirectory;
    }

    public Path getSchematicsDirectory() {
        return schematicsDirectory;
    }

//    public Map<String, MenuConfig> getInventory() {
//        return inventory;
//    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public WorldBorderApi getWorldBorderAPI() {
        return worldBorderApi;
    }
}
