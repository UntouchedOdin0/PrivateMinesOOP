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

package me.untouchedodin0.plugin.mines;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.mines.data.MineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Utils;
import me.untouchedodin0.plugin.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import redempt.redlib.config.annotations.ConfigName;
import redempt.redlib.misc.Task;
import redempt.redlib.region.CuboidRegion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Mine {

    @ConfigName("autoUpgrade.enabled")
    private static boolean autoUpgrade = false;
    @ConfigName("autoUpgrade.startingSize")
    private static int startingSize = 48;
    @ConfigName("autoUpgrade.everyXthExpansion")
    private static int expansionIncrement = 4;
    @ConfigName("resetPercentage")
    private static double resetPercentage = 50;

    final Utils utils;
    private final PrivateMines privateMines;
    private MineType mineType;
    private UUID mineOwner;
    private CuboidRegion miningRegion;
    private IWrappedRegion iWrappedRegion;
    private CuboidRegion region;
    private Location spawnLocation;
    private MineData mineData;
    private Task task;
    private double tax = 5;
    private Map<Material, Double> materials = new EnumMap<>(Material.class);

    public Mine(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.utils = new Utils(privateMines);
    }

    public static double getResetPercentage() {
        return resetPercentage;
    }

    public MineData getMineData() {
        return mineData;
    }

    public void setMineData(MineData mineData) {
        this.mineData = mineData;
    }

    public UUID getMineOwner() {
        return mineOwner;
    }

    public void setMineOwner(UUID mineOwner) {
        this.mineOwner = mineOwner;
    }

    public MineType getMineType() {
        return mineType;
    }

    public void setMineType(MineType mineType) {
        this.mineType = mineType;
    }

    public CuboidRegion getMiningRegion() {
        return miningRegion;
    }

    public void setMiningRegion(CuboidRegion miningRegion) {
        this.miningRegion = miningRegion;
    }

    public IWrappedRegion getIWrappedRegion() {
        return iWrappedRegion;
    }

    public void setIWrappedRegion(IWrappedRegion iWrappedRegion) {
        this.iWrappedRegion = iWrappedRegion;
    }

    public CuboidRegion getRegion() {
        return region;
    }

    public void setRegion(CuboidRegion region) {
        this.region = region;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }


    public void teleport(Player player) {
        MineData mineData = getMineData();
        MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
        World world = mineWorldManager.getMinesWorld();

        int spawnX = mineData.getSpawnX();
        int spawnY = mineData.getSpawnY();
        int spawnZ = mineData.getSpawnZ();
        Location location = new Location(world, spawnX + 0.5, spawnY, spawnZ + 0.5);
        player.teleport(location);
    }

    public void teleport(Player player, boolean whitelisted, boolean coowner) {
        MineData mineData = getMineData();
        UUID uuid = player.getUniqueId();
        List<UUID> whiteListedUsers = mineData.getWhitelistedPlayers();

        if (!whitelisted) { // If mine isn't whitelisted
            teleport(player);
        } else { // If mine is whitelisted
            if (!whiteListedUsers.contains(uuid) || coowner)  return;
            teleport(player);
        }
    }

    public Map<Material, Double> getMaterials() {
        return getMineType().getMaterials();
    }

    public void setMaterials(Map<Material, Double> materials) {
        this.materials = materials;
    }

    public boolean isInside(Location location) {
        return getMiningRegion().contains(location);
    }

    public void fill(Map<Material, Double> blocks) {
        CuboidRegion cuboidRegion = getMiningRegion();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isInside(player.getLocation())) {
                teleport(player);
            }
        }
        privateMines.getWorldEditAdapter().fillRegion(cuboidRegion, blocks);
    }

    public void reset() {
        if (!mineData.getMaterials().isEmpty()) {
            fill(mineData.getMaterials());
        } else {
            fill(mineType.getMaterials());
        }
    }

    public void delete() {
        privateMines.getWorldEditAdapter().fillRegion(region, Material.AIR);

        MineStorage mineStorage = privateMines.getMineStorage();

        mineStorage.removeMine(getMineOwner());
    }

    public void upgrade() {
        final MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
        final String mineTypeName = getMineData().getMineType();
        final MineType mineType = mineTypeManager.getMineType(mineTypeName);
        Objects.requireNonNull(mineType, "Invalid Mine type " + mineTypeName);

        if (mineTypeManager.isLastMineType(mineType)) {
            privateMines.getLogger().info("Mine was already maxed!");
            //TODO This should probably be an exception
            return;
        }
        final MineType next = mineTypeManager.getNextMineType(mineType);
        mineData.setMineType(next.getName());
        mineData.setMaterials(next.getMaterials());
//        Player owner = Bukkit.getPlayer(getMineOwner());
//        if (owner != null) {
//            // TODO why is this necessary? does the player really need to be online to upgrade?
//            this.expand(0);
//            this.expand(1);
//        }
        utils.saveMineData(getMineOwner(), mineData);
        setMineData(mineData);
        Bukkit.broadcastMessage("" + mineData.getMaterials());
        reset();
    }

    private void expandXAndZ(CuboidRegion region, final int amount) {
        region.expand(amount, amount, 0, 0, amount, amount);
    }

    // WORKING, DON'T FUCK WITH THIS ANYMORE!
    public boolean canExpand(int amount) {
        if (amount <= 0) {
            return true;
        }

        amount += 3; // TODO why is this here?? pls no magic numbers

        return miningRegion.clone().expand(amount, 0, amount, 0, amount, 0)
                .stream().noneMatch(block -> block.getType() == Material.OBSIDIAN);
    }

    public void expand(final int amount) {

        // upgrade before expanding
        if (autoUpgrade) {
            // compare x blindly assuming mine is square
            int currentSize =
                    getMiningRegion().getEnd().getBlockX() - getMiningRegion().getStart().getBlockX();
            int newSize = currentSize + amount * 2;
            if ((newSize - startingSize) % (expansionIncrement * 2) == 0) {
                this.upgrade();
            }
        }

        World world = privateMines.getMineWorldManager().getMinesWorld();
        if (world == null) {
            throw new IllegalStateException("Mines world is null");
        }

        boolean canExpand = canExpand(amount);
        MineStorage mineStorage = privateMines.getMineStorage();
        Path minesDirectory = privateMines.getMinesDirectory();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String fileName = getMineOwner() + ".json";
        Path jsonFile = minesDirectory.resolve(fileName);


        if (!canExpand) {
            privateMines.getLogger().info("The private mine can't expand anymore!");
        } else {
//            final Map<Material, Double> materials = getMineTypes();
//            if (materials.isEmpty()) {
//                throw new IllegalStateException("Mine type " + mineData.getMineType() +
//                                                " has no materials!");
//            }

            final CuboidRegion mine = getMiningRegion().clone();
            final CuboidRegion walls = getMiningRegion().clone();

            expandXAndZ(mine, amount);
            expandXAndZ(walls, amount);

//            privateMines.getWorldEditAdapter()
//                    .fillRegion(mine, materials);
            privateMines.getWorldEditAdapter()
                    .fillRegion(walls, Material.BEDROCK);
            // TODO make this configurable

            expandXAndZ(mine, -1);
            mineData.setMiningRegion(mine);

            mineData.setSpawnX(spawnLocation.getBlockX());
            mineData.setSpawnY(spawnLocation.getBlockY());
            mineData.setSpawnZ(spawnLocation.getBlockZ());

            try {
                Files.write(jsonFile, gson.toJson(mineData).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            setMiningRegion(mine);
            setMineData(mineData);
            privateMines.getMineStorage().replaceMine(getMineOwner(), this);
        }
        mineStorage.replaceMine(getMineOwner(), this);
//        reset();
    }

    public double getPercentage() {
        int volume = getMiningRegion().getBlockVolume();
        long minedCount = miningRegion.stream().filter(block -> !block.isEmpty()).count();

        return minedCount * 100d / volume;
    }

    public void startResetTask() {
        this.task = Task.syncRepeating(() -> {
            double percentage = getPercentage();
            if (percentage <= getResetPercentage()) {
                reset();
            }
        }, 0L, 20L);
    }

    public void cancelResetTask() {
        if (this.task != null) {
            this.task.cancel();
        }
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
