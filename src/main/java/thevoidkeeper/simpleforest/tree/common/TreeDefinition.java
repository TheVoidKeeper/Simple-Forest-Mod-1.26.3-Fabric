package thevoidkeeper.simpleforest.tree.common;

/** Immutable composition key for future biome palettes. Does not alter existing spawning. */
public record TreeDefinition(TreeSpeciesId species, TreeVariant variant) {
    public TreeDefinition {
        if (species == null || variant == null)
            throw new IllegalArgumentException("Missing species or variant");
    }
}
