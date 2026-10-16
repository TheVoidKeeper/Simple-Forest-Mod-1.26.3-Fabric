# Simple Forest – Phase 2, Minecraft 26.3

First draft of a custom oak feature. **Not build-tested**: local Minecraft/Fabric dependencies and Gradle wrapper are unavailable. Compile in your existing working 26.3 project and report any errors.

Changes: registers `simple-forest:oak_tree` as a `FEATURE_TYPE` codec; `worldgen/feature/forest_oak.json` supplies its entry, and `placed_feature/forest_oak.json` points to it. Birch remains vanilla. Forest biome modification uses `TREES_BIRCH_AND_OAK_LEAF_LITTER`.

Bigger-stem slope support is reserved for later: current trees are 1-block-wide trunks. The validator samples 3x3 ground, accepts >=6/9 valid soil blocks and maximum 2 block height variation, requires valid ground directly under trunk, and rejects water.

Run `./gradlew build` (Windows `./gradlew.bat build`), then `./gradlew.bat runClient` and create a NEW world. Keep a backup of the working Phase 1 project. If registry, codec or compile errors arise, send complete output.

Important current limitations: canopy density, branch continuity, leaf persistence/distance updates and cross-chunk worldgen need additional iteration before release. No roots yet. Does not rewrite old chunks.
