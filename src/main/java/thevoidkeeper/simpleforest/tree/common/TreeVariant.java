package thevoidkeeper.simpleforest.tree.common;

/** Independent dimensions: one species can eventually have dead/fallen large forms. */
public record TreeVariant(Size size, Condition condition, Orientation orientation) {
    public enum Size { TINY, SMALL, MEDIUM, LARGE, ANCIENT }
    public enum Condition { ALIVE, DEAD, DECAYING }
    public enum Orientation { STANDING, LEANING, FALLEN }

    public TreeVariant {
        if (size == null || condition == null || orientation == null)
            throw new IllegalArgumentException("Tree variant dimensions must not be null");
    }
}
