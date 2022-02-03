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

package me.untouchedodin0.plugin.mines.data;

import org.bukkit.Material;
import redempt.redlib.region.CuboidRegion;

import java.util.*;

public class WorldEditMineData {

    UUID mineOwner;
    UUID coOwner;

    int spawnX;
    int spawnY;
    int spawnZ;

    int minX;
    int minY;
    int minZ;

    int maxX;
    int maxY;
    int maxZ;

    int regionMinX;
    int regionMinY;
    int regionMinZ;

    int regionMaxX;
    int regionMaxY;
    int regionMaxZ;

    String worldName;
    String mineType;

    boolean isOpen;

    Map<Material, Double> materials = new EnumMap<>(Material.class);
    List<UUID> whitelistedPlayers = new ArrayList<>();
    List<UUID> bannedPlayers = new ArrayList<>();
    List<UUID> priorityPlayers = new ArrayList<>();

    public UUID getMineOwner() {
        return mineOwner;
    }

    public void setMineOwner(UUID mineOwner) {
        this.mineOwner = mineOwner;
    }

    public UUID getCoOwner() {
        return coOwner;
    }

    public void setCoOwner(UUID coOwner) {
        this.coOwner = coOwner;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public void setSpawnX(int spawnX) {
        this.spawnX = spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public void setSpawnY(int spawnY) {
        this.spawnY = spawnY;
    }

    public int getSpawnZ() {
        return spawnZ;
    }

    public void setSpawnZ(int spawnZ) {
        this.spawnZ = spawnZ;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public void setMinZ(int minZ) {
        this.minZ = minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(int maxZ) {
        this.maxZ = maxZ;
    }

    public int getRegionMinX() {
        return regionMinX;
    }

    public void setRegionMinX(int regionMinX) {
        this.regionMinX = regionMinX;
    }

    public void setMiningRegion(CuboidRegion region) {
        setMinX(region.getStart().getBlockX());
        setMinY(region.getStart().getBlockY());
        setMinZ(region.getStart().getBlockZ());
        setMaxX(region.getEnd().getBlockX());
        setMaxY(region.getEnd().getBlockY());
        setMaxZ(region.getEnd().getBlockZ());
    }

    public void setFullRegion(CuboidRegion region) {
        setRegionMinX(region.getStart().getBlockX());
        setRegionMinY(region.getStart().getBlockY());
        setRegionMinZ(region.getStart().getBlockZ());
        setRegionMaxX(region.getEnd().getBlockX());
        setRegionMaxY(region.getEnd().getBlockY());
        setRegionMaxZ(region.getEnd().getBlockZ());
        setWorldName(region.getWorld().getName());
    }

    public int getRegionMinY() {
        return regionMinY;
    }

    public void setRegionMinY(int regionMinY) {
        this.regionMinY = regionMinY;
    }

    public int getRegionMinZ() {
        return regionMinZ;
    }

    public void setRegionMinZ(int regionMinZ) {
        this.regionMinZ = regionMinZ;
    }

    public int getRegionMaxX() {
        return regionMaxX;
    }

    public void setRegionMaxX(int regionMaxX) {
        this.regionMaxX = regionMaxX;
    }

    public int getRegionMaxY() {
        return regionMaxY;
    }

    public void setRegionMaxY(int regionMaxY) {
        this.regionMaxY = regionMaxY;
    }

    public int getRegionMaxZ() {
        return regionMaxZ;
    }

    public void setRegionMaxZ(int regionMaxZ) {
        this.regionMaxZ = regionMaxZ;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getMineType() {
        return mineType;
    }

    public void setMineType(String mineType) {
        this.mineType = mineType;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public Map<Material, Double> getMaterials() {
        return materials;
    }

    public void setMaterials(Map<Material, Double> materials) {
        this.materials = materials;
    }

    public void addWhitelistedPlayer(UUID uuid) {
        if (whitelistedPlayers.contains(uuid)) return;
        whitelistedPlayers.add(uuid);
    }

    public void removeWhitelistedPlayer(UUID uuid) {
        whitelistedPlayers.remove(uuid);
    }

    public List<UUID> getWhitelistedPlayers() {
        return whitelistedPlayers;
    }

    public void addBannedPlayer(UUID uuid) {
        if (bannedPlayers.contains(uuid)) return;
        bannedPlayers.add(uuid);
    }

    public void removeBannedPlayer(UUID uuid) {
        bannedPlayers.remove(uuid);
    }

    public List<UUID> getBannedPlayers() {
        return bannedPlayers;
    }

    public void addPriorityPlayer(UUID uuid) {
        if (priorityPlayers.contains(uuid)) return;
        priorityPlayers.add(uuid);
    }

    public void removePriorityPlayer(UUID uuid) {
        priorityPlayers.remove(uuid);
    }

    public List<UUID> getPriorityPlayers() {
        return priorityPlayers;
    }
}
