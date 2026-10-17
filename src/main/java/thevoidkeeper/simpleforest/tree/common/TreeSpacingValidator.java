package thevoidkeeper.simpleforest.tree.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Conservative proximity test restricted to the candidate's own chunk.
 * Deliberately avoids forcing reads in neighboring ungenerated chunks.
 * This is NOT a globally order-independent forest spacing guarantee.
 */
public final class TreeSpacingValidator {
    private TreeSpacingValidator() {}

    public static boolean clearOfTrunks(WorldGenLevel level, BlockPos origin, int radius) {
        int chunkMinX = (origin.getX() >> 4) << 4;
        int chunkMinZ = (origin.getZ() >> 4) << 4;
        int yMin = origin.getY() - 2;
        int yMax = origin.getY() + 8;
        for (int x = Math.max(chunkMinX, origin.getX() - radius); x <= Math.min(chunkMinX + 15, origin.getX() + radius); x++) {
            for (int z = Math.max(chunkMinZ, origin.getZ() - radius); z <= Math.min(chunkMinZ + 15, origin.getZ() + radius); z++) {
                int dx = x - origin.getX(), dz = z - origin.getZ();
                if (dx * dx + dz * dz > radius * radius) continue;
                for (int y = yMin; y <= yMax; y++) {
                    BlockState state = level.getBlockState(new BlockPos(x, y, z));
                    if (state.is(Blocks.OAK_LOG) || state.is(Blocks.OAK_WOOD)
                            || state.is(Blocks.BIRCH_LOG) || state.is(Blocks.BIRCH_WOOD)) return false;
                }
            }
        }
        return true;
    }
}
