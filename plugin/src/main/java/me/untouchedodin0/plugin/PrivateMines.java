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

import com.google.gson.Gson;
import de.jeff_media.updatechecker.UpdateChecker;
import me.untouchedodin0.plugin.commands.PrivateMinesCommand;
import me.untouchedodin0.plugin.config.Config;
import me.untouchedodin0.plugin.config.MineConfig;
import me.untouchedodin0.plugin.config.menu.MenuItemType;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.listener.AutoSellListener;
import me.untouchedodin0.plugin.listener.MineCreationTest;
import me.untouchedodin0.plugin.listener.RevAutoSellListener;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.mines.MineTypeManager;
import me.untouchedodin0.plugin.mines.data.MineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.storage.TimeStorage;
import me.untouchedodin0.plugin.util.Exceptions;
import me.untouchedodin0.plugin.util.Utils;
import me.untouchedodin0.plugin.util.placeholderapi.PrivateMinesExpansion;
import me.untouchedodin0.plugin.world.MineWorldManager;
import me.untouchedodin0.privatemines.compat.WorldEditAdapter;
import me.untouchedodin0.privatemines.compat.WorldEditCompatibility;
import me.untouchedodin0.privatemines.we_6.worldedit.BlockPoints6;
import me.untouchedodin0.privatemines.we_7.worldedit.WE7Adapter;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PrivateMines extends JavaPlugin {

    private static final int PLUGIN_ID = 11413;
    private static final int SPIGOT_PLUGIN_ID = 90890;
    private static PrivateMines privateMines;
    private static PrivateMinesAPI privateMinesAPI;
    private Economy economy;

    private final Path minesDirectory = getDataFolder().toPath().resolve("mines");
    private final Path schematicsDirectory = getDataFolder().toPath().resolve("schematics");
    private final File addonsDirectory = new File("plugins/PrivateMines/addons");
    private final Pattern jarPattern = Pattern.compile("(.*?)\\.(jar)");
    IWrappedRegion globalRegion;
    private MineTypeManager mineTypeManager;
    private MineFactory mineFactory;
    private MineWorldManager mineWorldManager;
    private TimeStorage timeStorage;
    private MineStorage mineStorage;

    private Utils utils;
    private ConfigManager configManager;
    private ConfigManager menuConfigManager;
    private WE7Adapter we7Adapter; // = new WE7Adapter();

    private Gson gson;
    private WorldEditAdapter worldEditAdapter;

    public WorldEditAdapter getWorldEditAdapter() {
        return worldEditAdapter;
    }

    public static PrivateMines getPrivateMines() {
        return privateMines;
    }

    public static PrivateMinesAPI getAPI() {
        return privateMinesAPI;
    }


//    @Override
//    public void onLoad() {
//        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
//        PacketEvents.getAPI().load();
//    }

    @Override
    public void onEnable() {
        privateMines = this;
        privateMinesAPI = new PrivateMinesAPI(privateMines);
        gson = new Gson();

        saveDefaultConfig();
        setupEconomy();

        try {
            Files.createDirectories(minesDirectory);
            Files.createDirectories(schematicsDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mineWorldManager = new MineWorldManager();
        utils = new Utils(this);

        this.worldEditAdapter = WorldEditCompatibility.loadWorldEdit();
        if (worldEditAdapter == null) {
            getLogger().warning("WorldEdit is not enabled! Disabling PrivateMines!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        timeStorage = new TimeStorage();
        mineStorage = new MineStorage();

        mineFactory = new MineFactory(this);
        mineTypeManager = new MineTypeManager(this);

        configManager = ConfigManager.create(this).target(Config.class).saveDefaults().load();
        menuConfigManager = ConfigManager.create(this).target(MenuItemType.class).saveDefaults().load();

        @SuppressWarnings("unused")
        ConfigManager mineConfig = ConfigManager.create(this)
                .addConverter(Material.class, Material::valueOf, Material::toString)
                .target(MineConfig.class)
                .saveDefaults()
                .load();

        if (RedLib.MID_VERSION < 13) {
            // Save the schematic file, this format is used pre-1.13
            saveResource("schematics/mine.schematic", false);

            BlockPoints6 blockPoints6 = new BlockPoints6();
        } else {
            // Save the schem file this format is used in 1.13 and beyond.
            saveResource("schematics/mine.schem", false);
            me.untouchedodin0.privatemines.we_7.worldedit.Utils we7Utils = new me.untouchedodin0.privatemines.we_7.worldedit.Utils();
            we7Adapter = new WE7Adapter();

            MineConfig.mineTypes.forEach((s, mineType) -> {
                mineTypeManager.registerMineType(mineType);
//                File file = new File("plugins/PrivateMines/schematics/" + mineType.getFile());
//                we7Adapter.searchFile(file);
            });
        }
        getLogger().info("Loaded " + mineTypeManager.getTotalMineTypes() + " mine types!");

        try {
            final List<Path> files = Files.list(getDataFolder().toPath())
                    .collect(Collectors.toList());
            utils.moveSchematicFiles(files);
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadMines();

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

        //metrics.addCustomChart(new Metrics.("mines", mineStorage::getLoadedMinesSize));

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

        if (Bukkit.getPluginManager().getPlugin("AutoSell") != null) {
            // AutoSell was enabled, lets set up the hook!
            getLogger().info("Found AutoSell, registering a hook!");
            getServer().getPluginManager().registerEvents(new AutoSellListener(this), this);
        } else if (Bukkit.getPluginManager().getPlugin("UltraPrisonCore") != null) {
            // UltraPrisonCore was enabled, lets set up the hook!
            getLogger().info("Found UltraPrisonCore, registering a hook!");
            getLogger().info("Due to drawethree not giving a maven repo we can't do this yet");
            getLogger().info("please ask him in his discord to add a maven repo then I can");
            getLogger().info("add this, It's out of my control at this point, sorry!");
            getLogger().info("~ UntouchedOdin0");
            //todo Drawethree please api?
        } else if (Bukkit.getPluginManager().getPlugin("RevAutoSell") != null) {
            // RevAutoSell was enabled, lets set up the hook!
            getLogger().info("Found RevAutoSell, registering a hook!");
            getServer().getPluginManager().registerEvents(new RevAutoSellListener(this), this);
        }

        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            // Citizens was found, lets set up the hook!
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
        getServer().getPluginManager().registerEvents(new MineCreationTest(), this);

//        PacketEvents.getAPI().getSettings().debug(false).bStats(true);
//        PacketEvents.getAPI().init();

//        PacketEvents.getAPI().getSettings().bStats(true).checkForUpdates(false).debug(true);
//        PacketEvents.getAPI().init();

        UpdateChecker.init(this, SPIGOT_PLUGIN_ID).checkEveryXHours(6).setDownloadLink(SPIGOT_PLUGIN_ID).checkNow();

        //Metrics metrics = new Metrics(this, PLUGIN_ID);
        Metrics metrics1 = new Metrics(this, PLUGIN_ID);
        //metrics.addCustomChart(new SingleLineChart("mines", loadedMineCount::get));
    }

    /**
     *
     * @deprecated
     * This is the old system for loading the mines, it was non-async, so it wasn't thread friendly.
     * We're using {@link PrivateMines#loadMines()} instead now as it's Async and threaded correctly.
     */

    private void loadMinesOld() throws IOException {
        final PathMatcher jsonMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.json");
        AtomicInteger loadedMineCount = new AtomicInteger();
        String worldName = mineWorldManager.getMinesWorld().getName();

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

                    final World world = Bukkit.getWorld(worldName);
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
                    mine.setMineType(mineTypeManager.getMineType(mineData.getMineType()));
                    mine.setMineOwner(mineData.getMineOwner());
                    mine.startResetTask();
                    mineStorage.addMine(mineData.getMineOwner(), mine);
                    mineWorldManager.getNextFreeLocation();
                    loadedMineCount.incrementAndGet();
                });

        getLogger().info(() -> "Loaded " + loadedMineCount.get() + " mines");
    }

    private void loadMines() {
        final PathMatcher jsonMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.json");
        AtomicInteger loadedMinesCount = new AtomicInteger();
        String worldName = mineWorldManager.getMinesWorld().getName();
        final World world = Bukkit.getWorld(worldName);

        Runnable runnable = () -> {
            try {
                Files.list(minesDirectory)
                        .filter(jsonMatcher::matches)
                        .map(Exceptions.throwing(Files::readAllLines))
                        .map(lines -> String.join("\n", lines))
                        .forEach(file -> {
                            Mine mine = new Mine(this);
                            MineData mineData = gson.fromJson(file, MineData.class);
                            MineType mineType = mineTypeManager.getMineType(mineData.getMineType());
                            UUID owner = mineData.getMineOwner();

                            int minX = mineData.getMinX();
                            int minY = mineData.getMinY();
                            int minZ = mineData.getMinZ();
                            int maxX = mineData.getMaxX();
                            int maxY = mineData.getMaxY();
                            int maxZ = mineData.getMaxZ();

                            int spawnX = mineData.getSpawnX();
                            int spawnY = mineData.getSpawnY();
                            int spawnZ = mineData.getSpawnZ();

                            if (world == null) {
                                throw new IllegalStateException("World " + worldName + " does not exist!");
                            }
                            Location spawn = new Location(world, spawnX, spawnY, spawnZ);
                            Location minimumLocation = new Location(world, minX, minY, minZ);
                            Location maximumLocation = new Location(world, maxX, maxY, maxZ);
                            CuboidRegion miningRegion = new CuboidRegion(minimumLocation, maximumLocation);

                            mine.setSpawnLocation(spawn);
                            mine.setRegion(mineData.getFullRegion());
                            mine.setMiningRegion(miningRegion);
                            mine.setMineData(mineData);
                            mine.setMineType(mineType);
                            mine.setMineOwner(owner);
                            mine.startResetTask();
                            mineStorage.addMine(owner, mine);
                            mineWorldManager.getNextFreeLocation();
                            loadedMinesCount.incrementAndGet();
                        });
                if (loadedMinesCount.get() == 1) {
                    getLogger().info("Loaded " + loadedMinesCount.get() + " mine!");
                } else {
                    getLogger().info("Loaded " + loadedMinesCount.get() + " mines!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(runnable);
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

    /*
        Gets the time storage
     */

    public TimeStorage getTimeStorage() {
        return timeStorage;
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

    public ConfigManager getMenuConfigManager() {
        return menuConfigManager;
    }

    public Economy getEconomy() {
        return economy;
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return;
        RegisteredServiceProvider<Economy> registeredServiceProvider =
                getServer().getServicesManager().getRegistration(Economy.class);
        if (registeredServiceProvider == null) {
            privateMines.getLogger().info("Failed to find a economy provider!");
            return;
        }
        economy = registeredServiceProvider.getProvider();
        privateMines.getLogger().info("Successfully setup economy!");
    }
}
