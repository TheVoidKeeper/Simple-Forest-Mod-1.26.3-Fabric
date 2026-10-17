package thevoidkeeper.simpleforest.tree.species.birch.variants;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import thevoidkeeper.simpleforest.tree.common.TreePlacementValidator;
import thevoidkeeper.simpleforest.tree.common.TreeSpacingValidator;

import java.util.HashMap;
import java.util.Map;

/** Conservative, standalone birch feature for forest biomes on Minecraft 26.3. */
public final class BirchTreeFeature implements Feature {
    public static final BirchTreeFeature INSTANCE = new BirchTreeFeature();
    public static final MapCodec<BirchTreeFeature> CODEC = MapCodec.unit(INSTANCE);
    private static final BlockState LOG = Blocks.BIRCH_LOG.defaultBlockState();
    private static final BlockState LEAF = Blocks.BIRCH_LEAVES.defaultBlockState()
            .setValue(LeavesBlock.PERSISTENT, true);
    private BirchTreeFeature() {}

    @Override public MapCodec<BirchTreeFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        int height = 8 + random.nextInt(6);
        if (!TreePlacementValidator.canGrow(level, origin, height)
                || !TreeSpacingValidator.clearOfTrunks(level, origin, 7)) return false;
        Map<BlockPos, BlockState> plan = new HashMap<>();
        for (int y = 0; y < height; y++) plan.put(origin.above(y), LOG);
        int crownStart = height - 5;
        for (int y = crownStart; y <= height + 1; y++) {
            int relative = y - crownStart;
            int radius = relative >= 4 ? 1 : 2;
            for (int x = -radius; x <= radius; x++) for (int z = -radius; z <= radius; z++) {
                if (Math.abs(x) == radius && Math.abs(z) == radius && random.nextInt(3) == 0) continue;
                plan.putIfAbsent(origin.offset(x, y, z), LEAF);
            }
        }
        for (Map.Entry<BlockPos, BlockState> e : plan.entrySet()) {
            if (!level.getFluidState(e.getKey()).isEmpty()) return false;
            if (e.getValue().is(Blocks.BIRCH_LOG) && !level.getBlockState(e.getKey()).isAir()) return false;
        }
        for (Map.Entry<BlockPos, BlockState> e : plan.entrySet()) {
            if (e.getValue().is(Blocks.BIRCH_LOG) || level.getBlockState(e.getKey()).isAir())
                level.setBlock(e.getKey(), e.getValue(), 2);
        }
        return true;
    }
}
