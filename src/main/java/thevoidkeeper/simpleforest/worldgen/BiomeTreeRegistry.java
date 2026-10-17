package thevoidkeeper.simpleforest.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import thevoidkeeper.simpleforest.SimpleForest;

public final class BiomeTreeRegistry {
    private BiomeTreeRegistry() { }

    private static ResourceKey<PlacedFeature> vanillaFeature(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                Identifier.fromNamespaceAndPath("minecraft", name));
    }

    public static void register() {
        // Scope intentionally limited to minecraft:forest during Phase 1.
        // Do not delete the entire VEGETAL_DECORATION step: it contains other plants.
        BiomeModifications.create(SimpleForest.id("replace_forest_trees"))
                .add(ModificationPhase.REMOVALS,
                        BiomeSelectors.includeByKey(Biomes.FOREST),
                        (selection, modification) -> {
                            modification.getGenerationSettings()
                                    .removeFeature(net.minecraft.data.worldgen.placement.VegetationPlacements.TREES_BIRCH_AND_OAK_LEAF_LITTER);
                        });

        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.FOREST),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                TreeFeatureKeys.FOREST_OAK);
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.FOREST),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                TreeFeatureKeys.FOREST_BIRCH);
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.FOREST),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                TreeFeatureKeys.FOREST_LARGE_OAK);
    }
}
