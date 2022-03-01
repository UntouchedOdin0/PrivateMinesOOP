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

import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.config.Config;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.mines.data.MineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import me.untouchedodin0.plugin.storage.TimeStorage;
import me.untouchedodin0.plugin.util.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import org.jetbrains.annotations.NotNull;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.region.CuboidRegion;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class MineFactory {

    PrivateMines privateMines;
    Utils utils;
    MineStorage mineStorage;
    TimeStorage timeStorage;
    MineBlocks mineBlocks;
    Location spawnLocation;
    Location corner1;
    Location corner2;

    public MineFactory(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.utils = privateMines.getUtils();
        this.mineStorage = privateMines.getMineStorage();
        this.timeStorage = privateMines.getTimeStorage();
    }

    @Deprecated
    @SuppressWarnings("unused")
    private MineBlocks findMineBlocksLegacy(CuboidRegion mineRegion, Material spawnMaterial, Material cornerMaterial) {
        MineBlocks mineBlocks = new MineBlocks();
        mineBlocks.corners = new Location[2];

        Instant forEachStart = Instant.now();

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

        Instant forEachEnd = Instant.now();
        Duration timeElapsedStream = Duration.between(forEachStart, forEachEnd);

        if (mineBlocks.corners[0] == null || mineBlocks.corners[1] == null) {
            throw new IllegalArgumentException("Mine does not have 2 corners set");
        }
        if (mineBlocks.spawnLocation == null) {
            throw new IllegalArgumentException("Mine does not have a spawn location set");
        }
        return mineBlocks;
    }

    /*
        Handle this method Async somehow
     */

    private MineBlocks findMineBlocks(CuboidRegion mineRegion, Material spawnMaterial, Material cornerMaterial) {
        MineBlocks mineBlocks = new MineBlocks();
        mineBlocks.corners = new Location[2];
        Instant start = Instant.now();

        mineRegion.stream().iterator().forEachRemaining(block -> {
            Material bukkitMaterial = block.getType();
            if (bukkitMaterial == spawnMaterial) {
                mineBlocks.spawnLocation = block.getLocation();
            } else if (bukkitMaterial == (cornerMaterial)) {
                if (mineBlocks.corners[0] == null) {
                    mineBlocks.corners[0] = block.getLocation();
                } else if (mineBlocks.corners[1] == null) {
                    mineBlocks.corners[1] = block.getLocation();
                } else {
                    throw new IllegalArgumentException("Too many corners in mine!");
                }
            }
        });

        Instant end = Instant.now();

        Duration timeElapsedStream = Duration.between(start, end);
        privateMines.getLogger().info("Time elapsed: " + timeElapsedStream.toMillis() + "ms");
        timeStorage.addTime(timeElapsedStream.toMillis());

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
        Material spawnMaterial = Config.spawnPoint;
        Material mineCornerMaterial = Config.mineCorner;

        File schematicFile = new File("plugins/PrivateMines/schematics/" + mineType.getFile());
        Path path = schematicFile.toPath();

        Mine mine = new Mine(privateMines);
        MineData mineData = new MineData();

        CuboidRegion region = privateMines.getWorldEditAdapter().pasteSchematic(location, path);
        mineBlocks = findMineBlocks(region, spawnMaterial, mineCornerMaterial);
        spawnLocation = mineBlocks.spawnLocation;
        corner1 = mineBlocks.corners[0];
        corner2 = mineBlocks.corners[1];

        final Location fullCorner1 = region.getStart();
        final Location fullCorner2 = region.getEnd();

        final CuboidRegion miningRegion = new CuboidRegion(corner1, corner2);
        final CuboidRegion fullRegion = new CuboidRegion(fullCorner1, fullCorner2);

        mine.setMiningRegion(miningRegion);
        mine.setFullRegion(fullRegion);
        mine.setRegion(region);
        mine.setSpawnLocation(spawnLocation);
        mine.setMaterials(mineType.getMaterials());
        mine.setMineType(mineType);
        mine.setMineOwner(player.getUniqueId());
        mineData.setMineOwner(uuid);
        mineData.setSpawnX(spawnLocation.getBlockX());
        mineData.setSpawnY(spawnLocation.getBlockY());
        mineData.setSpawnZ(spawnLocation.getBlockZ());
        mineData.setMiningRegion(miningRegion);
        mineData.setFullRegion(fullRegion);
        mineData.setMineType(mineType.getName());
        mine.setMineData(mineData);
        utils.saveMineData(uuid, mineData);

        mine.reset();
        mine.startResetTask();

        if (replaceOld) {
            privateMines.getMineStorage().replaceMine(uuid, mine);
            player.sendMessage(Messages.msg("recievedMine"));
            String commandToSuggest = "/privatemines teleport %name%".replace("%name%", player.getName());

            // Make a new component using the Bungee API.
            TextComponent textComponent = new TextComponent("Click me to go to your mine!");
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandToSuggest));
            player.spigot().sendMessage(textComponent);
        } else {
            privateMines.getMineStorage().addMine(uuid, mine);
            player.sendMessage(Messages.msg("recievedMine"));

            String commandToSuggest = "/privatemines teleport";

            // Make a new component using the Bungee API.
            TextComponent textComponent = new TextComponent(ChatColor.GREEN + "Click me to go to your mine!");
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandToSuggest));
            player.spigot().sendMessage(textComponent);
        }
        IWrappedRegion iWrappedMiningRegion = utils.createWorldGuardRegion(player, miningRegion);
        IWrappedRegion iWrappedFullRegion = utils.createFullWorldGuardRegion(player, fullRegion);

        mine.setIWrappedMiningRegion(iWrappedMiningRegion);
        mine.setIWrappedFullRegion(iWrappedFullRegion);
        utils.setMineFlags(mine);
        //worldBorderApi.setBorder(player, 10, location);
    }

    private static class MineBlocks {
        Location spawnLocation;
        Location[] corners;
    }
}
