package me.untouchedodin0.plugin.mines.data;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.UUID;

public class WorldEditMineData {

    UUID mineOwner;

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

    Region region;
    CuboidRegion cuboidRegion;
    String worldName;
    String material;

    public UUID getMineOwner() {
        return mineOwner;
    }

    public void setMineOwner(UUID mineOwner) {
        this.mineOwner = mineOwner;
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

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public CuboidRegion getCuboidRegion() {
        return cuboidRegion;
    }

    public void setCuboidRegion(CuboidRegion cuboidRegion) {
        this.cuboidRegion = cuboidRegion;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }
}
