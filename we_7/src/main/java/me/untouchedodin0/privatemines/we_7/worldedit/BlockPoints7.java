package me.untouchedodin0.privatemines.we_7.worldedit;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.World;

import java.util.List;

public class BlockPoints7 {

    World world;
    BlockVector3 spawn;
    List<BlockVector3> corners;
    BlockVector3 corner1;
    BlockVector3 corner2;

    public BlockVector3 getSpawn() {
        return spawn;
    }

    public void setSpawn(BlockVector3 spawn) {
        this.spawn = spawn;
    }

    public List<BlockVector3> getCorners() {
        return corners;
    }

    public void setCorners(List<BlockVector3> corners) {
        this.corners = corners;
    }

    public BlockVector3 getCorner1() {
        return corner1;
    }

    public void setCorner1(BlockVector3 corner1) {
        this.corner1 = corner1;
    }

    public BlockVector3 getCorner2() {
        return corner2;
    }

    public void setCorner2(BlockVector3 corner2) {
        this.corner2 = corner2;
    }
}
