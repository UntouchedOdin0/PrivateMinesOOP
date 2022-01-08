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

package me.untouchedodin0.plugin.mines;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.mines.data.WorldEditMineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Utils;
import me.untouchedodin0.plugin.util.worldedit.Adapter;
import me.untouchedodin0.plugin.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import redempt.redlib.blockdata.DataBlock;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.configmanager.annotations.ConfigValue;
import redempt.redlib.misc.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static com.sk89q.worldedit.WorldEdit.getInstance;

public class WorldEditMine {

    public static final List<BlockVector3> EXPANSION_VECTORS = List.of(BlockVector3.UNIT_X, BlockVector3.UNIT_MINUS_X,
                                                                       BlockVector3.UNIT_Z, BlockVector3.UNIT_MINUS_Z);
    public static final BlockVector3 WALLS = BlockVector3.UNIT_Y;

    @ConfigValue("autoUpgrade.enabled")
    private static boolean autoUpgrade = false;
    @ConfigValue("autoUpgrade.startingSize")
    private static int startingSize = 48;
    @ConfigValue("autoUpgrade.everyXthExpansion")
    private static int expansionIncrement = 4;
    @ConfigValue("resetPercentage")
    private static double resetPercentage = 50;

    final Utils utils;
    private final PrivateMines privateMines;
    private WorldEditMineType worldEditMineType;
    private UUID mineOwner;
    private CuboidRegion cuboidRegion;
    private IWrappedRegion iWrappedRegion;
    private Region region;
    private Location spawnLocation;
    private World world;
    private Location location;
    private Material material;
    private Map<Material, Double> materials = new HashMap<>();
    private List<UUID> whitelistedPlayers = new ArrayList<>();
    private DataBlock dataBlock;
    private WorldEditMineData worldEditMineData;
    private MineFactory mineFactory;
    private Task task;
    private double tax = 5;

    public WorldEditMine(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.utils = new Utils(privateMines);
        this.mineFactory = privateMines.getMineFactory();
    }

    public WorldEditMineData getWorldEditMineData() {
        return worldEditMineData;
    }

    public void setWorldEditMineData(WorldEditMineData worldEditMineData) {
        this.worldEditMineData = worldEditMineData;
    }

    public UUID getMineOwner() {
        return mineOwner;
    }

    public void setMineOwner(UUID mineOwner) {
        this.mineOwner = mineOwner;
    }

    public WorldEditMineType getWorldEditMineType() {
        return worldEditMineType;
    }

    public void setWorldEditMineType(WorldEditMineType worldEditMineType) {
        this.worldEditMineType = worldEditMineType;
    }

    public CuboidRegion getCuboidRegion() {
        return cuboidRegion;
    }

    public void setCuboidRegion(CuboidRegion cuboidRegion) {
        this.cuboidRegion = cuboidRegion;
    }

    public IWrappedRegion getiWrappedRegion() {
        return iWrappedRegion;
    }

    public void setIWrappedRegion(IWrappedRegion iWrappedRegion) {
        this.iWrappedRegion = iWrappedRegion;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Map<Material, Double> getMaterials() {
        return materials;
    }

    public void setMaterials(Map<Material, Double> materials) {
        this.materials = materials;
    }

    public List<UUID> getWhitelistedPlayers() {
        return whitelistedPlayers;
    }

    public static double getResetPercentage() {
        return resetPercentage;
    }

    public void addToWhitelist(Player player, UUID uuid) {
        Player bukkitPlayer = Bukkit.getPlayer(uuid);
        Player mineOwner = Bukkit.getPlayer(getMineOwner());

        String playerAlreadyWhitelisted = Messages.msg("playerAlreadyWhitelisted");

        if (!whitelistedPlayers.contains(uuid)) {
            if (mineOwner != null && bukkitPlayer != null) {
                whitelistedPlayers.add(uuid);
            } else {
                if (bukkitPlayer != null && whitelistedPlayers.contains(uuid)) {
                    String replacedAlreadyWhitelisted = playerAlreadyWhitelisted.replace("%name%", bukkitPlayer.getName());
                    player.sendMessage(replacedAlreadyWhitelisted);
                }
            }
        }
        player.sendMessage(whitelistedPlayers.toString());
    }

    public void removeFromWhiteList(Player player, UUID uuid) {
        Player bukkitPlayer = Bukkit.getPlayer(uuid);
        String playerNotWhitelisted = Messages.msg("playerWasNotWhitelisted");

        if (!whitelistedPlayers.contains(uuid)) {
            if (bukkitPlayer != null) {
                player.sendMessage(playerNotWhitelisted.replace("%name%", bukkitPlayer.getName()));
            }
        } else {
            player.sendMessage("Removing " + bukkitPlayer + " from the whitelist!");
            whitelistedPlayers.remove(uuid);
        }
    }

    public DataBlock getDataBlock() {
        return dataBlock;
    }

    public void setDataBlock(DataBlock dataBlock) {
        this.dataBlock = dataBlock;
    }

//    public BlockState getFillState() {
//        final BlockType blockType = utils.bukkitToBlockType(getMaterial());
//        return utils.getBlockState(blockType);
//    }

    public void teleport(Player player) {
        UUID uuid = player.getUniqueId();
        boolean isOpen = worldEditMineData.isOpen();
        String notWhitelisted = Messages.msg("notWhitelisted");
        WorldEditMineData worldEditMineData = getWorldEditMineData();
        MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
        World world = mineWorldManager.getMinesWorld();

        int spawnX = worldEditMineData.getSpawnX();
        int spawnY = worldEditMineData.getSpawnY();
        int spawnZ = worldEditMineData.getSpawnZ();
        Location location = new Location(world, spawnX + 0.5, spawnY, spawnZ + 0.5);
        player.teleport(location);
    }

    public void setWorld(World world) {
        this.world = world;
    }

    //todo work out how to do this

    public boolean isInside(Location location) {
        CuboidRegion cuboidRegion = getCuboidRegion();
        return (location.getX() >= cuboidRegion.getMinimumPoint().getX() && location.getX() <= cuboidRegion.getMaximumPoint().getX() &&
                location.getY() >= cuboidRegion.getMinimumPoint().getY() && location.getY() <= cuboidRegion.getMaximumPoint().getY() &&
                location.getZ() >= cuboidRegion.getMinimumPoint().getZ() && location.getZ() <= cuboidRegion.getMaximumPoint().getZ());
    }

    public void fill(Map<Material, Double> blocks) {

        World world = privateMines.getMineWorldManager().getMinesWorld();
        CuboidRegion cuboidRegion = getCuboidRegion();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isInside(player.getLocation())) {
                teleport(player);
            }
        }

        try (final var session = getInstance()
                .newEditSession(BukkitAdapter.adapt(world))) {
            final RandomPattern randomPattern = new RandomPattern();

            blocks.forEach((material, aDouble) -> {
                Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
                randomPattern.add(pattern, 1.0);
            });

            session.setBlocks(cuboidRegion, randomPattern);
            // fix lighting ffs
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    // Ignore the Casting 'BukkitAdapter.adapt(...)' to 'Pattern' is redundant warning it makes the code work lol
    public void reset() {
        String mineType = worldEditMineData.getMineType();
        WorldEditMineType worldEditMineType = privateMines.getWorldEditMineType(mineType);
        fill(worldEditMineType.getMaterials());
    }

    public void reset(WorldEditMineType worldEditMineType) {

        final RandomPattern pattern = new RandomPattern();
        Map<Material, Double> mineTypeMaterials = worldEditMineType.getMaterials();

        mineTypeMaterials.forEach((material1, aDouble) -> {
            privateMines.getLogger().info("blockData: " + material1.createBlockData());
            Pattern blockPattern = (Pattern) BukkitAdapter.adapt(material1.createBlockData());
            pattern.add(blockPattern, aDouble);
        });

        try (final var session = getInstance()
                .newEditSession(BukkitAdapter.adapt(world))) {
            session.setBlocks(getCuboidRegion(), pattern);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    public void getMineSize() {
        this.world = privateMines.getMineWorldManager().getMinesWorld();

        if (world == null) {
            privateMines.getLogger().warning("Failed to delete the mine due to the world being null");
        }

        Player player = Bukkit.getPlayer(getMineOwner());
        CuboidRegion cuboidRegion = getCuboidRegion();

        cuboidRegion.asFlatRegion().iterator().forEachRemaining(blockVector2 -> {
            player.sendMessage(String.valueOf(blockVector2));
        });
    }

    public void delete() {

        this.world = privateMines.getMineWorldManager().getMinesWorld();
        final BlockType air = utils.bukkitToBlockType(Material.AIR);

        int minX = worldEditMineData.getRegionMinX();
        int minY = worldEditMineData.getRegionMinY();
        int minZ = worldEditMineData.getRegionMinZ();

        int maxX = worldEditMineData.getRegionMaxX();
        int maxY = worldEditMineData.getRegionMaxY();
        int maxZ = worldEditMineData.getRegionMaxZ();

        BlockVector3 min = BlockVector3.at(minX, minY, minZ);
        BlockVector3 max = BlockVector3.at(maxX, maxY, maxZ);

        CuboidRegion cuboidRegion = new CuboidRegion(min, max);

        MineStorage mineStorage = privateMines.getMineStorage();

        if (world == null) {
            privateMines.getLogger().warning("Failed to delete the mine due to the world being null");
        }

        try (final var session = getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            session.setBlocks(cuboidRegion, utils.getBlockState(air));
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
        mineStorage.removeWorldEditMine(getMineOwner());
    }

    public void upgrade() {
        TreeMap<String, WorldEditMineType> worldEditMineTypeTreeMap = privateMines.getWorldEditMineTypeTreeMap();
        WorldEditMineData worldEditMineData = getWorldEditMineData();
        Player player = Bukkit.getPlayer(getMineOwner());

        if (Objects.equals(privateMines.getWorldEditMineTypeTreeMap().lastEntry().getValue().getName(), worldEditMineData.getMineType())) {
            privateMines.getLogger().info("Mine was already maxed!");
        } else {
            WorldEditMineType higherEntry = worldEditMineTypeTreeMap.higherEntry(worldEditMineData.getMineType()).getValue();
            worldEditMineData.setMineType(higherEntry.getName());
            if (player != null) {
                this.expand(0);
                this.expand(1);
            }
        }
    }

    /*
      for here + 1; < amount
      if block is expected theme
      return -1;
      else
      return amount - expected theme

      expand (returned amount) -> update theme -> expand the rest

     return canExpand;
     return -1;
     */

    public void upgrade(Player player, WorldEditMineType worldEditMineType) {
        setWorldEditMineType(worldEditMineType);
        this.world = privateMines.getMineWorldManager().getMinesWorld();
        mineFactory.createMine(player, getLocation(), worldEditMineType, true);
    }

    private BlockVector3[] expansionVectors(final int amount) {
        return EXPANSION_VECTORS.stream().map(it -> it.multiply(amount)).toArray(BlockVector3[]::new);
    }

    public BlockVector3[] divideVectors(final int amount) {
        return EXPANSION_VECTORS.stream().map(it -> it.divide(amount)).toArray(BlockVector3[]::new);
    }

    // WORKING, DON'T FUCK WITH THIS ANYMORE!
    public boolean canExpand(final int amount) {
        if (amount <= 0) {
            return true;
        }
        this.world = privateMines.getMineWorldManager().getMinesWorld();
        final var mine = getCuboidRegion();
        final int toCheck = 3 + amount;

        BlockVector3 min = mine.getMinimumPoint();
        BlockVector3 max = mine.getMaximumPoint();
        CuboidRegion minMaxCuboid = new CuboidRegion(min, max);

        redempt.redlib.region.CuboidRegion cuboidRegion = utils.worldEditRegionToRedLibRegion(minMaxCuboid);
        cuboidRegion.expand(3, 0, 3, 0, 3, 0);

        return cuboidRegion.stream().noneMatch(block -> {
            return block.getType() == Material.OBSIDIAN;
        });
    }

    public void expand(final int amount) {

        // upgrade before expanding
        if (autoUpgrade) {
            // compare x blindly assuming mine is square
            int currentSize = getCuboidRegion().getMaximumPoint().getBlockX() - getCuboidRegion().getMinimumPoint().getBlockX();
            int newSize = currentSize + amount * 2;
            if ((newSize - startingSize) % (expansionIncrement * 2) == 0) {
                this.upgrade();
            }
        }

        this.world = privateMines.getMineWorldManager().getMinesWorld();
        boolean canExpand = canExpand(amount);
        MineStorage mineStorage = privateMines.getMineStorage();
        File minesDirectory = privateMines.getMinesDirectory();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String fileName = getMineOwner() + ".json";
        File oldFile = new File(minesDirectory, fileName);
        boolean deleted = oldFile.delete();
        if (deleted) {
            privateMines.getLogger().info("Deleted file to replace later!");
        }
        File jsonFile = new File(minesDirectory, fileName);

        if (world == null) {
            privateMines.getLogger().warning("Failed to expand the mine due to the world being null!");
        }

        if (!canExpand) {
            privateMines.getLogger().info("The private mine can't expand anymore!");
        } else {
            WorldEditMineType type = PrivateMines.getPrivateMines().getWorldEditMineType(worldEditMineData.getMineType());
            final BlockType fillType;
            if (type.getMaterials().keySet().stream().findFirst().isPresent()) {
                fillType = BlockTypes.get(type.getMaterials().keySet().stream().findFirst().get().getKey().toString());
                final var wallType = BlockTypes.BEDROCK;

                if (fillType == null || wallType == null) return;

                final var mine = getCuboidRegion();
                final var walls = getCuboidRegion();

                mine.expand(expansionVectors(amount));
                walls.expand(expansionVectors(amount));

                try (final var session = getInstance().newEditSession(BukkitAdapter.adapt(world))) {
                    session.setBlocks(mine, fillType.getDefaultState());
                    session.setBlocks(Adapter.walls(walls), wallType.getDefaultState());

                    mine.contract(expansionVectors(1));

                    BlockVector3 min = mine.getMinimumPoint();
                    BlockVector3 max = mine.getMaximumPoint();

                    worldEditMineData.setMinX(min.getBlockX());
                    worldEditMineData.setMinY(min.getBlockY());
                    worldEditMineData.setMinZ(min.getBlockZ());

                    worldEditMineData.setMaxX(max.getBlockX());
                    worldEditMineData.setMaxY(max.getBlockY());
                    worldEditMineData.setMaxZ(max.getBlockZ());
                } catch (MaxChangedBlocksException exception) {
                    exception.printStackTrace();
                }

                worldEditMineData.setMineOwner(getMineOwner());
                worldEditMineData.setSpawnX(spawnLocation.getBlockX());
                worldEditMineData.setSpawnY(spawnLocation.getBlockY());
                worldEditMineData.setSpawnZ(spawnLocation.getBlockZ());

                try {
                    Writer writer = new FileWriter(jsonFile);
                    writer.write(gson.toJson(worldEditMineData));
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                setCuboidRegion(null);
                setCuboidRegion(mine);
                setWorldEditMineData(worldEditMineData);
                privateMines.getMineStorage().replaceMine(getMineOwner(), this);
            }
        }
        mineStorage.replaceMine(getMineOwner(), this);
    }

    public double getPercentage() {
        double count = 0;
        long volume = getCuboidRegion().getVolume();
        World world = privateMines.getMineWorldManager().getMinesWorld();

        int minX = getCuboidRegion().getMinimumPoint().getBlockX();
        int minY = getCuboidRegion().getMinimumPoint().getBlockY();
        int minZ = getCuboidRegion().getMinimumPoint().getBlockZ();

        int maxX = getCuboidRegion().getMaximumPoint().getBlockX();
        int maxY = getCuboidRegion().getMaximumPoint().getBlockY();
        int maxZ = getCuboidRegion().getMaximumPoint().getBlockZ();

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (!block.isEmpty()) count++;
                }
            }
        }
        return count * 100 / volume;
    }

    public void startResetTask() {
        this.task = Task.syncRepeating(() -> {
            double percentage = getPercentage();
            double resetPercentage = getResetPercentage();
            if (percentage <= resetPercentage) {
                reset();
            }
        }, 0L, 20L);
    }

    public Task getTask() {
        return task;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }
}

// fuck sake idk if i should remove this or not, advice?

//    public void expand(final int amount) {
//        final var fillType = BlockTypes.DIAMOND_BLOCK;
//        final var wallType = BlockTypes.BEDROCK;
//        final var min = getCuboidRegion().getMinimumPoint();
//        final var max = getCuboidRegion().getMaximumPoint();
//
//        if (fillType == null || wallType == null) {
//            return;
//        }
//
//        final var mine = getCuboidRegion(); //Adapter.adapt(getCuboidRegion());
//        privateMines.getLogger().info("expand mine cuboid : " + mine);
//
//        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
//            mine.expand(expansionVectors(amount));
//            setCuboidRegion(null);
//            setCuboidRegion(mine);
////            this.cuboidRegion = mine;
//
//            session.setBlocks(mine, fillType.getDefaultState());
//            session.setBlocks(Adapter.walls(mine), wallType.getDefaultState());
//
//        } catch (MaxChangedBlocksException ex) {
//            ex.printStackTrace();
//        }
//
//        privateMines.getLogger().info("expand min: " + min);
//        privateMines.getLogger().info("expand max: " + max);
//
//        final var stupidWallCuboid = Adapter.walls(mine);
//
////        final var stupidWallCuboid = new CuboidRegion(BukkitAdapter.adapt(world, mine.getMinimumPoint()),
////                BukkitAdapter.adapt(world, mine.getMaximumPoint()));
//
//        privateMines.getLogger().info("expand mine min: " + mine.getMinimumPoint());
//        privateMines.getLogger().info("expand mine max: " + mine.getMaximumPoint());
//
////        privateMines.getLogger().info("expand stupidWallCuboid: " + stupidWallCuboid);
//
//        setBedrockCubeRegion(stupidWallCuboid);
//    }

//    public void expand(final int amount) {
//        final var fillType = BlockTypes.DIAMOND_BLOCK;
//        final var wallType = BlockTypes.BEDROCK;
//
//        if (fillType == null || wallType == null) {
//            return;
//        }
//
//        final var mine = getCuboidRegion();
//        final var fillCuboid = mine.clone();
//
//        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
//
//            mine.expand(expansionVectors(amount));
//
//            session.setBlocks(mine, fillType.getDefaultState());
//            session.setBlocks(Adapter.walls(mine), wallType.getDefaultState());
//        } catch (MaxChangedBlocksException ex) {
//            ex.printStackTrace();
//        }
//
//        final var stupidWallCuboid = new CuboidRegion(mine.getMinimumPoint(), mine.getMaximumPoint());
//        setCuboidRegion(stupidWallCuboid);
//        setBedrockCubeRegion(stupidWallCuboid);
//
////        setBedrockCubeRegion(stupidWallCuboid);
//    }


//    public void teleport(Player player) {
//        player.teleport(location);
//    }

