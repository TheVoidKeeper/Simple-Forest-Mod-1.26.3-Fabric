package thevoidkeeper.simpleforest.tree;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.HashMap;
import java.util.Map;

/** Phase 2: conservative procedural single-stem oaks, small and medium. */
public final class OakTreeFeature implements Feature {
    public static final OakTreeFeature INSTANCE = new OakTreeFeature();
    public static final MapCodec<OakTreeFeature> CODEC = MapCodec.unit(INSTANCE);
    private OakTreeFeature() {}

    @Override
    public MapCodec<OakTreeFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        int height = 8 + random.nextInt(8);
        if (!TreePlacementValidator.canGrow(level, origin, height)) return false;

        Map<BlockPos, BlockState> planned = new HashMap<>();
        BlockState wood = Blocks.OAK_LOG.defaultBlockState();
        BlockState leaves = Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        double angle = random.nextDouble() * Math.PI * 2;
        double tilt = (random.nextDouble() - 0.5) * 0.12;
        BlockPos[] trunk = new BlockPos[height];

        for (int y = 0; y < height; y++) {
            int x = (int) Math.round(Math.cos(angle) * tilt * y);
            int z = (int) Math.round(Math.sin(angle) * tilt * y);
            BlockPos at = origin.offset(x, y, z);
            trunk[y] = at;
            planned.put(at, wood);
            if (y > 0) {
                BlockPos previous = trunk[y - 1];
                // Bridge any horizontal shift: keep the trunk connected.
                if (previous.getX() != at.getX() || previous.getZ() != at.getZ())
                    planned.put(new BlockPos(at.getX(), previous.getY(), at.getZ()), wood);
            }
        }
        int branches = 3 + random.nextInt(3);
        for (int b = 0; b < branches; b++) {
            int start = height / 2 + random.nextInt(Math.max(1, height / 3));
            BlockPos from = trunk[start];
            double a = 2 * Math.PI * b / branches + random.nextDouble() * 0.6;
            int length = 2 + random.nextInt(3);
            BlockPos tip = from;
            for (int step = 1; step <= length; step++) {
                int dx = (int) Math.round(Math.cos(a) * step);
                int dz = (int) Math.round(Math.sin(a) * step);
                int dy = step / 2;
                tip = from.offset(dx, dy, dz);
                planned.put(tip, wood);
            }
            canopy(planned, tip, 2, leaves, random);
        }
        canopy(planned, trunk[height - 1], 3, leaves, random);

        // Validate every planned wood position before any world writes occur.
        for (Map.Entry<BlockPos, BlockState> entry : planned.entrySet()) {
            BlockState existing = level.getBlockState(entry.getKey());
            if (entry.getValue().is(Blocks.OAK_LOG) && !existing.isAir()) return false;
            if (!level.getFluidState(entry.getKey()).isEmpty()) return false;
        }
        for (Map.Entry<BlockPos, BlockState> entry : planned.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            if (state.is(Blocks.OAK_LOG) || level.getBlockState(pos).isAir())
                level.setBlock(pos, state, 2);
        }
        return true;
    }

    private static void canopy(Map<BlockPos, BlockState> plan, BlockPos center, int radius,
                               BlockState leaves, RandomSource random) {
        for (int dx = -radius; dx <= radius; dx++) for (int dz = -radius; dz <= radius; dz++)
            for (int dy = -2; dy <= 2; dy++) {
                double distance = dx * dx + dz * dz + dy * dy * 1.8;
                if (distance > radius * radius + random.nextDouble() * 2) continue;
                BlockPos at = center.offset(dx, dy, dz);
                plan.putIfAbsent(at, leaves);
            }
    }
}
