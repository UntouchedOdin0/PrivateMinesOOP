package me.untouchedodin0.api;

import lombok.Getter;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.PrivateMinesAPI;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.MineType;
import me.untouchedodin0.plugin.mines.data.MineData;
import me.untouchedodin0.plugin.storage.MineStorage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import redempt.redlib.region.CuboidRegion;

import java.util.Map;
import java.util.UUID;

@Getter
public class API {

    API api = new API();

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    PrivateMinesAPI privateMinesAPI = PrivateMines.getAPI();

    MineFactory mineFactory = privateMinesAPI.getMineFactory();
    MineStorage mineStorage = privateMinesAPI.getMineStorage();

    public Mine getMine(UUID uuid) {
        return mineStorage.getMine(uuid);
    }

    public boolean hasMine(UUID uuid) {
        return mineStorage.hasMine(uuid);
    }

    public int getTotalMines() {
        return mineStorage.getMinesCount();
    }

    public double getResetPercentage(Mine mine) {
        return mine.getPercentage();
    }

    public MineData getMineData(Mine mine) {
        return mine.getMineData();
    }

    public UUID getMineOwner(Mine mine) {
        return mine.getMineOwner();
    }

    public MineType getMineType(Mine mine) {
        return mine.getMineType();
    }

    public CuboidRegion getMiningRegion(Mine mine) {
        return mine.getMiningRegion();
    }

    public CuboidRegion getRegion(Mine mine) {
        return mine.getRegion();
    }

    public IWrappedRegion getIWrappedRegion(Mine mine) {
        return mine.getIWrappedRegion();
    }

    public Location getSpawnLocation(Mine mine) {
        return mine.getSpawnLocation();
    }

    public Map<Material, Double> getMaterials(Mine mine) {
        return mine.getMaterials();
    }

    public boolean isInsideMiningRegion(Mine mine, Location location) {
        return mine.isInside(location);
    }

    public boolean isInsideFullRegion(Mine mine, Location location) {
        return mine.isInsideFullRegion(location);
    }

    public double getPercentage(Mine mine) {
        return mine.getPercentage();
    }
}
