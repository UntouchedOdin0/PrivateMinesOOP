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
