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

package me.untouchedodin0.plugin.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.data.MineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import org.jetbrains.annotations.NotNull;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.region.CuboidRegion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class MineFactory {
    private final Gson gson = new GsonBuilder().create();
    PrivateMines privateMines;
    Utils utils;
    MineStorage mineStorage;

    public MineFactory(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.utils = privateMines.getUtils();
        this.mineStorage = privateMines.getMineStorage();
    }

    private MineBlocks findMineBlocks(CuboidRegion mineRegion, Material spawnMaterial, Material cornerMaterial) {
        MineBlocks mineBlocks = new MineBlocks();
        mineBlocks.corners = new Location[2];
        mineRegion.forEachBlock(mineBlock -> {
            Material bukkitMaterial = mineBlock.getType();
            if (bukkitMaterial == spawnMaterial) {
                mineBlocks.spawnLocation = mineBlock.getLocation();
            } else if (bukkitMaterial == (cornerMaterial)) {
                if (mineBlocks.corners[0] == null) {
                    mineBlocks.corners[0] = mineBlock.getLocation();
                } else if (mineBlocks.corners[1] == null) {
                    mineBlocks.corners[1] = mineBlock.getLocation();
                } else {
                    throw new IllegalArgumentException("Too many corners in mine!");
                }
            }
        });
        if (mineBlocks.corners[0] == null || mineBlocks.corners[1] == null) {
            throw new IllegalArgumentException("Mine does not have 2 corners set");
        }
        if (mineBlocks.spawnLocation == null) {
            throw new IllegalArgumentException("Mine does not have a spawn location set");
        }
        return mineBlocks;
    }

    public void createMine(Player player, Location location, @NotNull MineType mineType, boolean replaceOld) {
        UUID uuid = player.getUniqueId();
        Material spawnMaterial = privateMines.getSpawnMaterial();
        Material mineCornerMaterial = privateMines.getCornerMaterial();

        Path file = mineType.getSchematicFile();

        CuboidRegion region = privateMines.getWorldEditAdapter().pasteSchematic(location, file);

        MineBlocks mineBlocks = findMineBlocks(region, spawnMaterial, mineCornerMaterial);
        Location spawnLocation = mineBlocks.spawnLocation;
        final Location corner1 = mineBlocks.corners[0];
        final Location corner2 = mineBlocks.corners[1];
        spawnLocation.getBlock().setType(Material.AIR, false);

        final CuboidRegion miningRegion = new CuboidRegion(corner1, corner2);
        Mine mine = new Mine(privateMines);
        MineData mineData = new MineData();
        mine.setMiningRegion(miningRegion);
        mine.setRegion(region);
        mine.setSpawnLocation(spawnLocation);
        mine.setMineTypes(mineType.getMaterials());
        mine.setMineType(mineType);
        mine.setMineOwner(player.getUniqueId());
        mine.setWorldEditMineData(mineData);
        mineData.setMineOwner(uuid);
        mineData.setSpawnX(spawnLocation.getBlockX());
        mineData.setSpawnY(spawnLocation.getBlockY());
        mineData.setSpawnZ(spawnLocation.getBlockZ());
        mineData.setMiningRegion(miningRegion);
        mineData.setFullRegion(region);
        mineData.setMaterials(mineType.getMaterials());
        mineData.setMineType(mineType.getName());
        saveMineData(uuid, mineData);

        mine.reset();
        mine.startResetTask();
        if (replaceOld) {
            this.privateMines.getMineStorage().replaceMine(uuid, mine);
            player.teleport(spawnLocation);
        } else {
            this.privateMines.getMineStorage().addMine(uuid, mine);
            player.sendMessage(Messages.msg("recievedMine"));
            player.teleport(spawnLocation);
        }
        IWrappedRegion worldGuardRegion = utils.createWorldGuardRegion(player, miningRegion);
        mine.setIWrappedRegion(worldGuardRegion);
        utils.setMineFlags(mine);
    }

    private void saveMineData(UUID uuid, MineData mineData) {
        Path minesDirectory = privateMines.getMinesDirectory();
        Path playerDataFile = minesDirectory.resolve(uuid + ".json");
        // TODO does this file structure work with having multiple mines?
        try {
            Files.write(playerDataFile, gson.toJson(mineData).getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Could not save mine data", e);
        }
    }

    private static class MineBlocks {
        Location spawnLocation;
        Location[] corners;
    }
}
