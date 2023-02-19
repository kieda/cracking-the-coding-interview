package common.tree;

public interface BinaryTreeTraverser<A, E, N extends BinaryTree<E>.Node> {
    /**
     * visits a new node. N will always be unique for each of these calls
     */
    public A visitNode(A accumulator, N node, ParentRelation relation);

    /**
     * when we are traversing and we go up a level in our tree.
     * Does not necessarily mean we are visiting a new node, and we may visit the same node more than once.
     * This should be used to do things like modify our current depth
     */
    public default A visitUp(A accumulator, N node, ParentRelation relation) {
        return accumulator;
    };

    /**
     * when we are traversing and we go down a level in our tree.
     * Does not necessarily mean we are visiting a new or unique node, and we may visit the same node more than once.
     * This should be used to do things like modify our current depth
     */
    public default A visitDown(A accumulator, N node, ParentRelation relation) {
        return accumulator;
    };

    /**
     * Condition to stop our traversal.
     */
    public default boolean stop(A accumulator, N node) {
        return false;
    };
}
