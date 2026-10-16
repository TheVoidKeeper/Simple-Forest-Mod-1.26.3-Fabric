package thevoidkeeper.simpleforest.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import thevoidkeeper.simpleforest.SimpleForest;

public final class TreeFeatureKeys {
    private TreeFeatureKeys() { }

    public static final ResourceKey<PlacedFeature> FOREST_OAK = key("forest_oak");
    public static final ResourceKey<PlacedFeature> FOREST_BIRCH = key("forest_birch");

    private static ResourceKey<PlacedFeature> key(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, SimpleForest.id(name));
    }
}
