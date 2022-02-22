package me.untouchedodin0.privatemines.we_7.worldedit;

import com.sk89q.worldedit.world.World;
import org.bukkit.util.BlockVector;

public class RelativePointsWE7 {

    World world;
    BlockVector spawn;
    BlockVector corner1;
    BlockVector corner2;

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public BlockVector getSpawn() {
        return spawn;
    }

    public void setSpawn(BlockVector spawn) {
        this.spawn = spawn;
    }

    public BlockVector getCorner1() {
        return corner1;
    }

    public void setCorner1(BlockVector corner1) {
        this.corner1 = corner1;
    }

    public BlockVector getCorner2() {
        return corner2;
    }

    public void setCorner2(BlockVector corner2) {
        this.corner2 = corner2;
    }
}
