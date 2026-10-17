package thevoidkeeper.simpleforest.tree.species.oak;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Default survival-compatible oak material palette. */
public final class OakMaterials {
    public static final BlockState LOG = Blocks.OAK_LOG.defaultBlockState();
    public static final BlockState WOOD = Blocks.OAK_WOOD.defaultBlockState();
    public static final BlockState LEAVES = Blocks.OAK_LEAVES.defaultBlockState()
            .setValue(LeavesBlock.PERSISTENT, true);
    private OakMaterials() {}
}
