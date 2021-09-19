package me.untouchedodin0.privatemines.world;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class EmptyWorldGenerator extends ChunkGenerator {

    /**
     * Shapes the chunk for the given coordinates.
     * <p>
     * This method must return a ChunkData.
     * <p>
     * Notes:
     * <p>
     * This method should <b>never</b> attempt to get the Chunk at
     * the passed coordinates, as doing so may cause an infinite loop
     * <p>
     * This method should <b>never</b> modify a ChunkData after it has
     * been returned.
     * <p>
     * This method <b>must</b> return a ChunkData returned by {@link ChunkGenerator#createChunkData(World)}
     *
     * @param world  The world this chunk will be used for
     * @param random The random generator to use
     * @param x      The X-coordinate of the chunk
     * @param z      The Z-coordinate of the chunk
     * @param biome  Proposed biome values for chunk - can be updated by
     *               generator
     * @return ChunkData containing the types for each block created by this
     * generator
     */

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        return createChunkData(world);
    }
}
