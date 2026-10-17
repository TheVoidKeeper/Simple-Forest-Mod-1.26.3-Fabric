package thevoidkeeper.simpleforest.worldgen;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import thevoidkeeper.simpleforest.SimpleForest;
import thevoidkeeper.simpleforest.tree.species.birch.variants.BirchTreeFeature;
import thevoidkeeper.simpleforest.tree.species.oak.variants.OakTreeFeature;
import thevoidkeeper.simpleforest.tree.species.oak.variants.LargeOakTreeFeature;

public final class TreeFeatureTypes {
    private TreeFeatureTypes() {}
    public static void register() {
        Registry.register(BuiltInRegistries.FEATURE_TYPE, SimpleForest.id("oak_tree"), OakTreeFeature.CODEC);
        Registry.register(BuiltInRegistries.FEATURE_TYPE, SimpleForest.id("large_oak_tree"), LargeOakTreeFeature.CODEC);
        Registry.register(BuiltInRegistries.FEATURE_TYPE, SimpleForest.id("birch_tree"), BirchTreeFeature.CODEC);
    }
}
