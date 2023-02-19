package common.tree;

import chapter4.CheckBalanced;
import common.function.Function3;

import java.util.function.BiPredicate;

public class DepthTraverser<A extends DepthTraverser.DepthAccumulator, E, N extends BinaryTree<E>.Node> implements BinaryTreeTraverser<A, E, N>{
    private BinaryTreeTraverser<A, E, N> delegate;

    public DepthTraverser(Function3<A, N, ParentRelation, A> visitNode) {
        this(visitNode, (a, n) -> a.getDepth() < 0);
    }
    public DepthTraverser(Function3<A, N, ParentRelation, A> visitNode, BiPredicate<A, N> stop) {
        this.delegate = new BinaryTreeTraverser<A, E, N>() {
            @Override
            public A visitNode(A accumulator, N node, ParentRelation relation) {
                return visitNode.apply(accumulator, node, relation);
            }

            @Override
            public A visitUp(A accumulator, N node, ParentRelation relation) {
                return accumulator;
            }

            @Override
            public A visitDown(A accumulator, N node, ParentRelation relation) {
                return accumulator;
            }

            @Override
            public boolean stop(A accumulator, N node) {
                return stop.test(accumulator, node);
            }
        };
    }

    public DepthTraverser(BinaryTreeTraverser<A, E, N> delegate) {
        this.delegate = delegate;
    }

    @Override
    public A visitNode(A accumulator, N node, ParentRelation relation) {
        return delegate.visitNode(accumulator, node, relation);
    }

    @Override
    public A visitUp(A accumulator, N node, ParentRelation relation) {
        accumulator.incrementDepth();
        return delegate.visitUp(accumulator, node, relation);
    }

    @Override
    public A visitDown(A accumulator, N node, ParentRelation relation) {
        accumulator.decrementDepth();
        return delegate.visitDown(accumulator, node, relation);
    }

    @Override
    public boolean stop(A accumulator, N node) {
        return delegate.stop(accumulator, node);
    }

    public static class DepthAccumulator{
        private int depth;
        public DepthAccumulator() {
            depth = -1;
        }
        void incrementDepth() {
            this.depth++;
        }
        void decrementDepth() {
            this.depth--;
        }

        public int getDepth() {
            return depth;
        }
    }
}
