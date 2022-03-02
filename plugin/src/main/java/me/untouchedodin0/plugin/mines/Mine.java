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
import me.untouchedodin0.plugin.events.PrivateMineDeletionEvent;
import me.untouchedodin0.plugin.events.PrivateMineResetEvent;
import me.untouchedodin0.plugin.mines.data.MineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Utils;
import me.untouchedodin0.plugin.util.exceptions.MineAlreadyMaxedException;
import me.untouchedodin0.plugin.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
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
    private CuboidRegion fullRegion;
    private CuboidRegion miningRegion;
    private IWrappedRegion iWrappedMiningRegion;
    private IWrappedRegion iWrappedFullRegion;
    private CuboidRegion region;
    private Location spawnLocation;
    private MineData mineData;
    private Task task;
    private double tax = 5;

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

    public CuboidRegion getFullRegion() {
        return fullRegion;
    }

    public void setFullRegion(CuboidRegion fullRegion) {
        this.fullRegion = fullRegion;
    }

    public CuboidRegion getRegion() {
        return region;
    }

    public void setRegion(CuboidRegion region) {
        this.region = region;
    }

    public IWrappedRegion getIWrappedMiningRegion() {
        return iWrappedMiningRegion;
    }

    public void setIWrappedMiningRegion(IWrappedRegion iWrappedRegion) {
        this.iWrappedMiningRegion = iWrappedRegion;
    }

    public IWrappedRegion getIWrappedFullRegion() {
        return iWrappedFullRegion;
    }

    public void setIWrappedFullRegion(IWrappedRegion iWrappedFullRegion) {
        this.iWrappedFullRegion = iWrappedFullRegion;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public void teleport(Player player) {
        MineData mineData = getMineData();
        MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
        World world = mineWorldManager.getMinesWorld();

        int spawnX = mineData.getSpawnX();
        int spawnY = mineData.getSpawnY();
        int spawnZ = mineData.getSpawnZ();
        Location location = new Location(world, spawnX + 0.5, spawnY, spawnZ + 0.5);
        if (!location.getBlock().isEmpty()) {
            location.getBlock().setType(Material.AIR);
        }
        Task.syncDelayed(() -> player.teleport(location), 10L);
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

    public void teleport(Player player, boolean whitelisted) {
        MineData mineData = getMineData();
        UUID uuid = player.getUniqueId();
        List<UUID> whiteListedUsers = mineData.getWhitelistedPlayers();

        if (!whitelisted) { // If mine isn't whitelisted
            teleport(player);
        } else { // If mine is whitelisted
            if (!whiteListedUsers.contains(uuid)) return;
            teleport(player);
        }
    }

    public Map<Material, Double> getMaterials() {
        if (!mineData.getMaterials().isEmpty()) return mineData.getMaterials();
        return getMineType().getMaterials();
    }

    public boolean isInside(Location location) {
        return getMiningRegion().contains(location);
    }

    public boolean isInsideFullRegion(Location location) {
        return getRegion().contains(location);
    }

    public void emptyMine() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isInside(player.getLocation())) {
                teleport(player);
            }
        }
    }

    public void fill(Map<Material, Double> blocks) {
        CuboidRegion cuboidRegion = getMiningRegion();
        Task task = Task.asyncDelayed(() -> privateMines.getWorldEditAdapter().fillRegion(cuboidRegion, blocks));
        task.cancel();
    }

    public void fillAir() {
        CuboidRegion miningRegion = getMiningRegion();
        CuboidRegion fullRegion = getRegion();
        Task task = Task.asyncDelayed(() -> {
            privateMines.getWorldEditAdapter().fillRegion(miningRegion, Material.AIR);
            privateMines.getWorldEditAdapter().fillRegion(fullRegion, Material.AIR);
        });
        task.cancel();
    }

    public void reset() {
        if (!mineData.getMaterials().isEmpty()) {
            emptyMine();
            fill(mineData.getMaterials());
        } else {
            emptyMine();
            fill(mineType.getMaterials());
        }

        Task task = Task.syncDelayed(() -> {
            PrivateMineResetEvent privateMineResetEvent = new PrivateMineResetEvent(this, privateMines);
            Bukkit.getPluginManager().callEvent(privateMineResetEvent);
        }, 20L);
        task.cancel();
    }

    public void delete() {
        PrivateMineDeletionEvent privateMineDeletionEvent = new PrivateMineDeletionEvent(this);
        Bukkit.getPluginManager().callEvent(privateMineDeletionEvent);
        if (privateMineDeletionEvent.isCancelled()) return;
        fillAir();
        MineStorage mineStorage = privateMines.getMineStorage();
        mineStorage.removeMine(getMineOwner());
    }

    public void upgrade() throws MineAlreadyMaxedException {
        final MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
        final String mineTypeName = getMineData().getMineType();
        final MineType mineType = mineTypeManager.getMineType(mineTypeName);
        Objects.requireNonNull(mineType, "Invalid Mine type " + mineTypeName);

        if (mineTypeManager.isLastMineType(mineType)) {
            throw new MineAlreadyMaxedException("Mine was already maxed!");
        }
        final MineType next = mineTypeManager.getNextMineType(mineType);
        mineData.setMineType(next.getName());
        mineData.setMaterials(next.getMaterials());
        utils.saveMineData(getMineOwner(), mineData);
        setMineData(mineData);
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
                try {
                    this.upgrade();
                } catch (MineAlreadyMaxedException e) {
                    privateMines.getLogger().info("The mine was already maxed out!");
                }
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
        IWrappedRegion miningRegion = getIWrappedMiningRegion();
        String regionName = miningRegion.getId();

        if (!canExpand) {
            privateMines.getLogger().info("The private mine can't expand anymore!");
        } else {

            final CuboidRegion mine = getMiningRegion().clone();
            final CuboidRegion walls = getMiningRegion().clone();

            expandXAndZ(mine, amount);
            expandXAndZ(walls, amount);

            privateMines.getWorldEditAdapter()
                    .fillRegion(walls, Material.BEDROCK);

            // TODO make this configurable

            expandXAndZ(mine, -1);

            mineData.setSpawnX(spawnLocation.getBlockX());
            mineData.setSpawnY(spawnLocation.getBlockY());
            mineData.setSpawnZ(spawnLocation.getBlockZ());

            try {
                Files.write(jsonFile, gson.toJson(mineData).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Optional<IWrappedRegion> iWrappedRegion =
                    WorldGuardWrapper.getInstance().getRegion(world, regionName);
            WorldGuardWrapper.getInstance().removeRegion(world, regionName);

            Player player = Bukkit.getOfflinePlayer(getMineOwner()).getPlayer();
            IWrappedRegion expandedMiningRegion = utils.createWorldGuardRegion(player, mine);
            mineData.setMiningRegion(mine);
            setIWrappedMiningRegion(expandedMiningRegion);
            setMineData(mineData);
            privateMines.getMineStorage().replaceMine(getMineOwner(), this);
            utils.setMineFlags(this);
            reset();
        }
    }

    public double getPercentage() {
        int volume = getMiningRegion().getBlockVolume();
        long minedCount = miningRegion.stream().filter(block -> !block.isEmpty()).count();

        return minedCount * 100d / volume;
    }

    public void startResetTask() {
        int resetTime = mineType.getResetTime();

        this.task = Task.syncRepeating(() -> {
            double percentage = getPercentage();
            if (percentage <= getResetPercentage()) {
                reset();
            }
        }, 0L, resetTime * 20L);
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

    public void sendBarrier(Player player, Location location) {
        utils.sendBorder(player, location);
    }
}
