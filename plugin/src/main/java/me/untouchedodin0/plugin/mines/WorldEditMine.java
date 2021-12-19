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
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
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
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import redempt.redlib.blockdata.DataBlock;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class WorldEditMine {

    public static final List<BlockVector3> EXPANSION_VECTORS = List.of(BlockVector3.UNIT_X, BlockVector3.UNIT_MINUS_X,
                                                                       BlockVector3.UNIT_Z, BlockVector3.UNIT_MINUS_Z);
    public static final BlockVector3 positiveY = BlockVector3.UNIT_Y;

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
    private IWrappedRegion mineRegion;

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

    public Location getSpawnLocation() {
        return spawnLocation;
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

    public BlockState getFillState() {
        final BlockType blockType = utils.bukkitToBlockType(getMaterial());
        return utils.getBlockState(blockType);
    }

//    public List<BlockState> getMultipleFillState() {
//        Material[] materials = getMaterials();
//        List<BlockState> blockStates = new ArrayList<>();
//
//        for (Material material : materials) {
//            BlockType blockType = utils.bukkitToBlockType(material);
//            BlockState blockState = utils.getBlockState(blockType);
//            blockStates.add(blockState);
//        }
//        return blockStates;
//    }

    public File getSchematicFile() {
        return worldEditMineType.getSchematicFile();
    }

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
        Location location = new Location(world, spawnX+0.5, spawnY, spawnZ+0.5);
        player.teleport(location);
    }


    public void setWorld(World world) {
        this.world = world;
    }

// Resets the mine

    /*
    public void reset() {

        final var fillType = utils.bukkitToBlockType(material);

        final RandomPattern randomPattern = new RandomPattern();
        final Map<Material, Double> materials = worldEditMineData.getMaterials();

        privateMines.getLogger().info("reset debug");
        privateMines.getLogger().info("materials: " + materials);
        privateMines.getLogger().info("randomPattern: " + randomPattern);

//        materials.forEach((material, percentage) -> {
//            Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
//            randomPattern.add(pattern, percentage);
//        });

        this.world = privateMines.getMineWorldManager().getMinesWorld();

        if (world == null) {
            privateMines.getLogger().warning("Failed to reset due to the mine world being null");
        }

        // Makes sure everything isn't null
        if (cuboidRegion != null && fillType != null) {

            // Creates edit session, sets the blocks and flushes it!
            try (final var session = WorldEdit.getInstance()
                    .newEditSession(BukkitAdapter.adapt(world))) {
                session.setBlocks(getCuboidRegion(), (Pattern) fillType);
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            }
        }
    }
     */


//    public void reset() {
//
//        final var fillType = utils.bukkitToBlockType(material);
//        Map<Material, Double> map = new HashMap<>();
//        final RandomPattern pattern = new RandomPattern();
//
//        worldEditMineData.getMaterials().forEach((material1, aDouble) -> {
//            privateMines.getLogger().info("adding material " + material1 + " with percentage " + aDouble);
//            map.put(material1, aDouble);
//        });
//
//        privateMines.getLogger().info("reset map: " + map);
//
//        privateMines.getLogger().info("pattern: " + pattern);
//        try (final var session = WorldEdit.getInstance()
//                .newEditSession(BukkitAdapter.adapt(world))) {
//            map.forEach((itemStack, aDouble) -> {
//                Pattern pat = BukkitAdapter.adapt(itemStack.createBlockData());
//                privateMines.getLogger().info("pat: " + pat);
//                pattern.add(pat, aDouble);
//            });
//
//            privateMines.getLogger().info(pattern.toString());
//            session.setBlocks(getCuboidRegion(), (Pattern) fillType);
//        } catch (MaxChangedBlocksException e) {
//            e.printStackTrace();
//        }
//    }

    public void fill(Map<Material, Double> blocks) {

        World world = privateMines.getMineWorldManager().getMinesWorld();

        try (final var session = WorldEdit.getInstance()
                .newEditSession(BukkitAdapter.adapt(world))) {
            final RandomPattern randomPattern = new RandomPattern();

            blocks.forEach((material1, aDouble) -> {
                Pattern pattern = BukkitAdapter.adapt(material1.createBlockData());
                randomPattern.add(pattern, 1.0);
            });

            session.setBlocks(getCuboidRegion(), randomPattern);
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

        Map<Material, Double> materials = new HashMap<>();

        privateMines.getLogger().info("reset mineTypeMaterials: " + mineTypeMaterials);

        mineTypeMaterials.forEach((material1, aDouble) -> {
            privateMines.getLogger().info("blockData: " + material1.createBlockData());
            Pattern blockPattern = (Pattern) BukkitAdapter.adapt(material1.createBlockData());
            pattern.add(blockPattern, aDouble);
        });

        privateMines.getLogger().info("set blocks pattern: " + pattern);

        try (final var session = WorldEdit.getInstance()
                .newEditSession(BukkitAdapter.adapt(world))) {
            session.setBlocks(getCuboidRegion(), pattern);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    //todo work out how to do this

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

//        IWrappedRegion iWrappedRegion = getiWrappedRegion();

        if (world == null) {
            privateMines.getLogger().warning("Failed to delete the mine due to the world being null");
        }

        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            session.setBlocks(cuboidRegion, utils.getBlockState(air));
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }

//        privateMines.getLogger().info("delete region: " + iWrappedRegion);
        mineStorage.removeWorldEditMine(getMineOwner());
//        utils.deleteWorldGuardRegion(iWrappedRegion);
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

    public void upgrade(Player player, WorldEditMineType worldEditMineType) {
        setWorldEditMineType(worldEditMineType);
        this.world = privateMines.getMineWorldManager().getMinesWorld();
        mineFactory.createMine(player, getLocation(), worldEditMineType, true);
    }

//    public void upgrade() {
//
//        TreeMap<String, WorldEditMineType> worldEditMineTypeTreeMap = privateMines.getWorldEditMineTypeTreeMap();
//        privateMines.getLogger().info(worldEditMineTypeTreeMap.toString());
//
//        WorldEditMineData worldEditMineData = getWorldEditMineData();
//        if (worldEditMineTypeTreeMap.higherEntry(worldEditMineData.getMineType()) == null) {
//            privateMines.getLogger().info("Mine already maxed!");
//        } else {
//            String currentType = worldEditMineData.getMineType();
//            String nextType = worldEditMineTypeTreeMap.higherKey(currentType);
//            worldEditMineData.setMineType(nextType);
//            setWorldEditMineData(worldEditMineData);
//            privateMines.getMineStorage().replaceMine(getMineOwner(), this);
//            reset(getWorldEditMineType());
//        }
//    }

    /*
    public void upgrade() {

        this.world = privateMines.getMineWorldManager().getMinesWorld();
        final BlockType air = utils.bukkitToBlockType(Material.AIR);
        final WorldEditMineType currentWorldEditMineType = getWorldEditMineType();
        final PasteFactory pasteFactory = new PasteFactory(privateMines);
        final Location location = getLocation();

        int minX = worldEditMineData.getRegionMinX();
        int minY = worldEditMineData.getRegionMinY();
        int minZ = worldEditMineData.getRegionMinZ();

        int maxX = worldEditMineData.getRegionMaxX();
        int maxY = worldEditMineData.getRegionMaxY();
        int maxZ = worldEditMineData.getRegionMaxZ();

        BlockVector3 min = BlockVector3.at(minX, minY, minZ);
        BlockVector3 max = BlockVector3.at(maxX, maxY, maxZ);

        CuboidRegion cuboidRegion = new CuboidRegion(min, max);
        Clipboard clipboard;
        List<Location> corners = new ArrayList<>();

        if (world == null) {
            privateMines.getLogger().warning("Failed to upgrade the mine due to the world being null");
        }

        try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            session.setBlocks(cuboidRegion, utils.getBlockState(air));
        } catch (MaxChangedBlocksException exception) {
            exception.printStackTrace();
        }

        WorldEditMineType worldEditMineType = getWorldEditMineType();
        File schematicFile = worldEditMineType.getSchematicFile();
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        Player player = Bukkit.getPlayer(getMineOwner());

        if (clipboardFormat != null) {
            try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {
                clipboard = clipboardReader.read();
                Region region = pasteFactory.paste(clipboard, location);

                region.iterator().forEachRemaining(blockVector3 -> {
                    if (world != null) {
                        Location bukkitLocation = utils.blockVector3toBukkit(world, blockVector3);
                        Material bukkitMaterial = bukkitLocation.getBlock().getType();

                        if (bukkitMaterial == Material.CHEST) {
                            this.spawnLocation = utils.blockVector3toBukkit(world, blockVector3);
                        } else if (bukkitMaterial == Material.POWERED_RAIL) {
                            corners.add(bukkitLocation);
                        }
                    }
                });

                // Gets the corner locations
                Location corner1 = corners.get(0);
                Location corner2 = corners.get(1);

                // Makes the block vector 3 corner locations

                BlockVector3 blockVectorCorner1 = BlockVector3.at(
                        corner1.getBlockX(),
                        corner1.getBlockY(),
                        corner1.getBlockZ()
                );

                BlockVector3 blockVectorCorner2 = BlockVector3.at(
                        corner2.getBlockX(),
                        corner2.getBlockY(),
                        corner2.getBlockZ()
                );

                // Makes the cuboid region to fill with blocks

                CuboidRegion fillCuboidRegion = new CuboidRegion(blockVectorCorner1, blockVectorCorner2);
                setCuboidRegion(fillCuboidRegion);

                spawnLocation.getBlock().setType(Material.AIR);
                reset();
                if (player != null) {
                    teleport(player);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
     */

    private BlockVector3[] expansionVectors(final int amount) {
        return EXPANSION_VECTORS.stream().map(it -> it.multiply(amount)).toArray(BlockVector3[]::new);
    }

    public BlockVector3[] divideVectors(final int amount) {
        return EXPANSION_VECTORS.stream().map(it -> it.divide(amount)).toArray(BlockVector3[]::new);
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

    @ConfigValue ("autoUpgrade.enabled")
    private static boolean autoUpgrade = false;

    @ConfigValue ("autoUpgrade.startingSize")
    private static int startingSize = 48;

    @ConfigValue ("autoUpgrade.everyXthExpansion")
    private static int expansionIncrement = 4;

    public void expand(final int amount) {

        privateMines.getLogger().info(Boolean.toString(autoUpgrade));

        // upgrade before expanding
        if (autoUpgrade) {
            // compare x blindly assuming mine is square
            int currentSize = getCuboidRegion().getMaximumPoint().getBlockX() - getCuboidRegion().getMinimumPoint().getBlockX();
            int newSize = currentSize + amount*2;

            Bukkit.broadcastMessage("currentSize: " + currentSize);
            Bukkit.broadcastMessage("newSize: " + newSize);

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
        File jsonFile = new File(minesDirectory, fileName);

        if (world == null) {
            privateMines.getLogger().warning("Failed to expand the mine due to the world being null!");
        }

        if (!canExpand) {
            privateMines.getLogger().info("The private mine can't expand anymore!");
        } else {
            WorldEditMineType type = PrivateMines.getPrivateMines().getWorldEditMineType(worldEditMineData.getMineType());
            final var fillType = BlockTypes.get(type.getMaterials().keySet().stream().findFirst().get().getKey().toString());
            final var wallType = BlockTypes.BEDROCK;

            if (fillType == null || wallType == null) return;

            final var mine = getCuboidRegion();
            final var walls = getCuboidRegion();

            mine.expand(expansionVectors(amount));
            walls.expand(expansionVectors(amount));

            privateMines.getLogger().info(fillType.toString());

            try (final var session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
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
            worldEditMineData.setCuboidRegion(mine);
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
        mineStorage.replaceMine(getMineOwner(), this);
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
}

//    public void teleport(Player player) {
//        player.teleport(location);
//    }

