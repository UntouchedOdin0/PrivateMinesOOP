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

package me.untouchedodin0.plugin.util;

import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.mines.WorldEditMine;
import me.untouchedodin0.plugin.mines.data.WorldEditMineData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import org.jetbrains.annotations.NotNull;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.region.CuboidRegion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

    private final PrivateMines privateMines;

    public Utils(PrivateMines privateMines) {
        this.privateMines = privateMines;
    }

    public IWrappedRegion createWorldGuardRegion(Player player, CuboidRegion cuboidRegion) {
        UUID uuid = player.getUniqueId();
        String regionName = String.format("mine-%s", uuid);
        return WorldGuardWrapper.getInstance().addCuboidRegion(
                regionName,
                cuboidRegion.getStart(),
                cuboidRegion.getEnd()
        ).orElseThrow(() -> new RuntimeException("Could not create worldguard region named " + regionName));
    }


    public void setMineFlags(WorldEditMine worldEditMine) {
        final WorldGuardWrapper worldGuardWrapper = WorldGuardWrapper.getInstance();
        IWrappedRegion iWrappedRegion = worldEditMine.getIWrappedRegion();

        Stream.of(
                        worldGuardWrapper.getFlag("block-place", WrappedState.class),
                        worldGuardWrapper.getFlag("block-break", WrappedState.class)
                ).filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(flag -> iWrappedRegion.setFlag(flag, WrappedState.ALLOW));

        Stream.of(
                        worldGuardWrapper.getFlag("mob-spawning", WrappedState.class)
                ).filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(flag -> iWrappedRegion.setFlag(flag, WrappedState.DENY));
    }

    public void setGlobalFlags(IWrappedRegion iWrappedRegion) {
        final WorldGuardWrapper worldGuardWrapper = WorldGuardWrapper.getInstance();

        Stream.of(
                        worldGuardWrapper.getFlag("build", WrappedState.class),
                        worldGuardWrapper.getFlag("interact", WrappedState.class),
                        worldGuardWrapper.getFlag("use", WrappedState.class)
                ).filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(flag -> iWrappedRegion.setFlag(flag, WrappedState.DENY));
    }


    public void sendMessage(CommandSender commandSender, String message) {
        String toSend = Messages.msg(message);
        if (commandSender != null && toSend != null) {
            commandSender.sendMessage(toSend);
        }
    }

    public void sendMessage(Player player, String message) {
        String toSend = Messages.msg(message);
        if (player != null && toSend != null) {
            player.sendMessage(toSend);
        }
    }

    public void moveSchematicFiles(@NotNull Collection<Path> files) {
        Path directory = privateMines.getSchematicsDirectory();
        files.stream()
                .filter(path -> path.endsWith(".schem"))
                .forEach(path -> {
                    Path inDirectory = directory.resolve(path.getFileName());
                    if (Files.exists(inDirectory)) {
                        privateMines.getLogger()
                                .info("The file " + path + " already exists so not moving!");
                        return;
                    }
                    privateMines.getLogger().info("Moving file " + path);
                    try {
                        Files.move(path, inDirectory);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void doAction(Player player, @NotNull WorldEditMine worldEditMine, String action) {

        WorldEditMineData worldEditMineData = worldEditMine.getWorldEditMineData();
        List<UUID> whitelistedPlayers = worldEditMineData.getWhitelistedPlayers();
        List<UUID> bannedPlayers = worldEditMineData.getBannedPlayers();
        List<UUID> priorityPlayers = worldEditMineData.getPriorityPlayers();
        UUID coowner = worldEditMineData.getCoOwner();
        String notSetCoOwner = Messages.msg("youHaveNotSetACoOwner");
        String nextUpdate = "Next Update! <3";

        switch (action.toLowerCase()) {
            case "teleport": {
                worldEditMine.teleport(player);
                break;
            }
            case "status":
            case "minesize":
            case "settax":
                player.sendMessage(nextUpdate);
                break;
            case "reset":
                worldEditMine.reset();
                break;
            case "whitelistedplayers":
                player.sendMessage(ChatColor.GOLD + "Whitelisted Players:");
                whitelistedPlayers.forEach(uuid -> player.sendMessage(ChatColor.YELLOW + "- " + Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName()));
                break;
            case "bannedplayers": {
                player.sendMessage(ChatColor.GOLD + "Banned Players:");
                bannedPlayers.forEach(uuid -> player.sendMessage(ChatColor.YELLOW + "- " + Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName()));
                break;
            }
            case "priorityplayers": {
                player.sendMessage(ChatColor.GOLD + "Priority Players:");
                priorityPlayers.forEach(uuid -> player.sendMessage(ChatColor.YELLOW + "- " + Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName()));
                break;
            }
            case "coowner": {
                if (coowner == null) {
                    player.sendMessage(notSetCoOwner);
                } else {
                    player.sendMessage(ChatColor.GREEN + "Your co-owner is set to " +
                                       Objects.requireNonNull(Bukkit.getPlayer(worldEditMineData.getCoOwner())).getName());
                }
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + action.toLowerCase());
        }
    }

    public String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    // Credits to CapOfCave#5962 for this
    public List<String> color(@NotNull Collection<String> toConvert) {
        return toConvert.stream().map(this::color).collect(Collectors.toList());
    }
}
