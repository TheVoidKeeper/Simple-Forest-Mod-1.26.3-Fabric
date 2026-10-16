package thevoidkeeper.simpleforest.worldgen;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import thevoidkeeper.simpleforest.SimpleForest;
import thevoidkeeper.simpleforest.tree.OakTreeFeature;

public final class TreeFeatureTypes {
    private TreeFeatureTypes() {}
    public static void register() {
        Registry.register(BuiltInRegistries.FEATURE_TYPE, SimpleForest.id("oak_tree"), OakTreeFeature.CODEC);
    }
}
