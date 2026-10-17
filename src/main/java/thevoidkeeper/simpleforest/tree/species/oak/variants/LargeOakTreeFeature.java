package thevoidkeeper.simpleforest.tree.species.oak.variants;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import thevoidkeeper.simpleforest.tree.species.oak.generator.LargeOakGenerator;

/** Minecraft 26.3 registry adapter; actual geometry stays inside the oak species. */
public final class LargeOakTreeFeature implements Feature {
    public static final LargeOakTreeFeature INSTANCE = new LargeOakTreeFeature();
    public static final MapCodec<LargeOakTreeFeature> CODEC = MapCodec.unit(INSTANCE);

    private LargeOakTreeFeature() {}

    @Override public MapCodec<LargeOakTreeFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel world, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        return LargeOakGenerator.generate(world, random, origin);
    }
}
