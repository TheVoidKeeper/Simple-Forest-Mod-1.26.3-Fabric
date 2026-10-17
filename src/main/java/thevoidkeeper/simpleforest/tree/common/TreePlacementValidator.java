package thevoidkeeper.simpleforest.tree.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class TreePlacementValidator {
    private TreePlacementValidator() {}

    public static boolean validGround(BlockState s) {
        return s.is(Blocks.GRASS_BLOCK) || s.is(Blocks.DIRT) || s.is(Blocks.COARSE_DIRT)
                || s.is(Blocks.PODZOL) || s.is(Blocks.ROOTED_DIRT) || s.is(Blocks.MOSS_BLOCK);
    }

    public static boolean canGrow(WorldGenLevel world, BlockPos origin, int height) {
        if (!world.getFluidState(origin).isEmpty() || !world.getBlockState(origin).isAir()) return false;
        int valid = 0;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        // Single-block trunks for phase 2. 3x3 ground footprint is checked conservatively.
        for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
            BlockPos floor = origin.offset(dx, -1, dz);
            // Terrain surface variation: consider one block above/below the expected ground.
            int foundY = Integer.MIN_VALUE;
            for (int dy = 1; dy >= -1; dy--) {
                BlockPos test = floor.offset(0, dy, 0);
                if (validGround(world.getBlockState(test)) && world.getFluidState(test.above()).isEmpty()) {
                    foundY = test.getY(); break;
                }
            }
            if (foundY != Integer.MIN_VALUE) {
                valid++;
                minY = Math.min(minY, foundY);
                maxY = Math.max(maxY, foundY);
            }
        }
        if (valid < 6 || maxY - minY > 2 || !validGround(world.getBlockState(origin.below()))) return false;
        for (int y = 0; y <= height + 1; y++) {
            BlockPos pos = origin.above(y);
            if (!world.getBlockState(pos).isAir() || !world.getFluidState(pos).isEmpty()) return false;
        }
        return true;
    }
}
