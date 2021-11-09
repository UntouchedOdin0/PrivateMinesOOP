package me.untouchedodin0.plugin.util.worldedit;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionIntersection;
import org.bukkit.Location;

public enum Adapter {
    ;

    public static CuboidRegion adapt(redempt.redlib.region.CuboidRegion region) {
        Location min = region.getStart();
        Location max = region.getEnd();

        return new CuboidRegion(BlockVector3.at(min.getBlockX(), min.getBlockY(), min.getBlockZ()),
                BlockVector3.at(max.getBlockX(), max.getBlockY(), max.getBlockZ()));
    }

    public static Region walls(CuboidRegion region) {
        BlockVector3 pos1 = region.getPos1();
        BlockVector3 pos2 = region.getPos2();

        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        return new RegionIntersection(
                // Project to Z-Y plane
                new CuboidRegion(pos1.withX(min.getX()), pos2.withX(min.getX())),
                new CuboidRegion(pos1.withX(max.getX()), pos2.withX(max.getX())),

                // Project to X-Y plane
                new CuboidRegion(pos1.withZ(min.getZ()), pos2.withZ(min.getZ())),
                new CuboidRegion(pos1.withZ(max.getZ()), pos2.withZ(max.getZ())),

                // Project to the X-Z plane
                new CuboidRegion(pos1.withY(min.getY()), pos2.withY(min.getY())));
    }

}
