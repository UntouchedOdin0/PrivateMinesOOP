package me.untouchedodin0.privatemines.compat;

import org.bukkit.Location;
import org.bukkit.Material;
import redempt.redlib.region.CuboidRegion;
import redempt.redlib.region.Region;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

public interface WorldEditAdapter {

    CuboidRegion pasteSchematic(Location location, Path file);

    void fillRegion(CuboidRegion region, Map<Material, Double> materials);

    default void fillRegion(CuboidRegion region, Material material) {
        Map<Material, Double> map = new EnumMap<>(Material.class);
        map.put(material, 1.0);
        fillRegion(region, map);
    }
}
