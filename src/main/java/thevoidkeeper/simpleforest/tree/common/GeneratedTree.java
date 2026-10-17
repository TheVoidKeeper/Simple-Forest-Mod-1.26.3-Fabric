package thevoidkeeper.simpleforest.tree.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashMap;
import java.util.Map;

/** Transient generation blueprint. It does not persist tree IDs yet. */
public final class GeneratedTree {
    public record Entry(BlockState state, TreeBlockRole role) { }

    private final Map<BlockPos, Entry> blocks = new LinkedHashMap<>();

    public void wood(BlockPos pos, BlockState state, TreeBlockRole role) {
        blocks.put(pos.immutable(), new Entry(state, role));
    }

    public void leaf(BlockPos pos, BlockState state) {
        blocks.putIfAbsent(pos.immutable(), new Entry(state, TreeBlockRole.LEAVES));
    }

    public void detail(BlockPos pos, BlockState state) {
        blocks.putIfAbsent(pos.immutable(), new Entry(state, TreeBlockRole.DECORATION));
    }

    public boolean contains(BlockPos pos) {
        return blocks.containsKey(pos);
    }

    public boolean isWood(BlockPos pos) {
        Entry e = blocks.get(pos);
        return e != null && (e.role == TreeBlockRole.TRUNK
                || e.role == TreeBlockRole.BRANCH
                || e.role == TreeBlockRole.ROOT
                || e.role == TreeBlockRole.TWIG);
    }

    public boolean hasWood(BlockPos pos) {
        return isWood(pos);
    }

    public boolean hasLeaf(BlockPos pos) {
        Entry e = blocks.get(pos);
        return e != null && e.role == TreeBlockRole.LEAVES;
    }

    /** Commit is all-or-nothing for structural blocks; foliage may overlap existing foliage. */
    public boolean commit(WorldGenLevel world) {
        for (Map.Entry<BlockPos, Entry> e : blocks.entrySet()) {
            TreeBlockRole role = e.getValue().role();
            if (role == TreeBlockRole.LEAVES || role == TreeBlockRole.DECORATION) continue;
            BlockState existing = world.getBlockState(e.getKey());
            if (!world.getFluidState(e.getKey()).isEmpty() || !existing.isAir()) return false;
        }

        for (Map.Entry<BlockPos, Entry> e : blocks.entrySet()) {
            BlockPos pos = e.getKey();
            Entry entry = e.getValue();
            if (entry.role() != TreeBlockRole.LEAVES && entry.role() != TreeBlockRole.DECORATION) {
                world.setBlock(pos, entry.state(), 2);
            } else if (world.getBlockState(pos).isAir() && world.getFluidState(pos).isEmpty()) {
                world.setBlock(pos, entry.state(), 2);
            }
        }
        return true;
    }
}
