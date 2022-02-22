package me.untouchedodin0.privatemines.we_7.worldedit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import org.bukkit.Material;

import java.util.Objects;

public class Utils {

    Material getType(World world, BlockVector3 blockVector3) {
        return BukkitAdapter.adapt(Objects.requireNonNull(world).getFullBlock(blockVector3).getBlockType());
    }
}
