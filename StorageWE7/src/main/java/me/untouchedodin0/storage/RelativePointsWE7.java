package me.untouchedodin0.storage;

import com.sk89q.worldedit.math.BlockVector3;

public class RelativePointsWE7 {

    BlockVector3 corner1;
    BlockVector3 corner2;
    BlockVector3 spawn;

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

    public BlockVector3 getSpawn() {
        return spawn;
    }

    public void setSpawn(BlockVector3 spawn) {
        this.spawn = spawn;
    }
}
