package me.untouchedodin0.plugin.mines.data;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
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
    String mineType;

    boolean isOpen;
}
