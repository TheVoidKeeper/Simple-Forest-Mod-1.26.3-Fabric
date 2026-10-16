package thevoidkeeper.simpleforest;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thevoidkeeper.simpleforest.worldgen.BiomeTreeRegistry;
import thevoidkeeper.simpleforest.worldgen.TreeFeatureTypes;

public final class SimpleForest implements ModInitializer {
    public static final String MOD_ID = "simple-forest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        TreeFeatureTypes.register();
        BiomeTreeRegistry.register();
        LOGGER.info("Simple Forest Phase 2: forest tree replacement registered for Minecraft 26.3");
    }
}
