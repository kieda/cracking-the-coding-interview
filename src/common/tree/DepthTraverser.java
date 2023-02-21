package common.tree;

import chapter4.CheckBalanced;
import common.function.Function3;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class DepthTraverser<A extends DepthTraverser.DepthAccumulator, E, N extends BinaryTree<E>.Node> implements BinaryTreeTraverser<A, E, N>{
    private BinaryTreeTraverser<A, E, N> delegate;

    public DepthTraverser(BiFunction<A, N, A> visitNode, BiPredicate<A, N> stop) {
        this.delegate = new BinaryTreeTraverser<>() {
            @Override
            public A visitNode(A accumulator, N node) {
                return visitNode.apply(accumulator, node);
            }

            @Override
            public A visitUp(A accumulator, N node) {
                return accumulator;
            }

            @Override
            public A visitDown(A accumulator, N node) {
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
    public A visitNode(A accumulator, N node) {
        // determine direction from previous visited node to this node
        ParentRelation direction;
        BinaryTree<?>.Node previous = accumulator.getPreviousNode();

        if(previous == null)
            direction = ParentRelation.HEAD;
        else if(node.getRight() == previous || previous.getRight() == node)
            direction = ParentRelation.RIGHT;
        else if(node.getLeft() == previous || previous.getLeft() == node)
            direction = ParentRelation.LEFT;
        else
            direction = ParentRelation.NONE;

        accumulator.setRelation(direction);
        A result = delegate.visitNode(accumulator, node);
        accumulator.setPreviousNode(node);
        return result;
    }

    @Override
    public A visitUp(A accumulator, N node) {
        accumulator.decrementDepth();
        BinaryTree<?>.Node previous = accumulator.getPrevious();
        ParentRelation direction;
        if(previous == null)
            // first time we are visiting, will be from the head of the tree
            direction = ParentRelation.HEAD;
        else if(node.getRight() == previous)
            direction = ParentRelation.RIGHT;
        else if(node.getLeft() == previous)
            direction = ParentRelation.LEFT;
        else
            direction = ParentRelation.NONE;
        accumulator.setRelation(direction);
        A result = delegate.visitUp(accumulator, node);
        accumulator.setPrevious(node);
        return result;
    }

    @Override
    public A visitDown(A accumulator, N node) {
        accumulator.incrementDepth();
        BinaryTree<?>.Node previous = accumulator.getPrevious();
        ParentRelation direction;
        if(previous == null)
            // first time we are visiting, will be from the head of the tree
            direction = ParentRelation.HEAD;
        else if(previous.getRight() == node)
            direction = ParentRelation.RIGHT;
        else if(previous.getLeft() == node)
            direction = ParentRelation.LEFT;
        else
            direction = ParentRelation.NONE;
            // should never occur in a regular traversal, as visitUp and visitDown only move at most one space
        accumulator.setRelation(direction);
        A result = delegate.visitDown(accumulator, node);
        accumulator.setPrevious(node);
        return result;
    }

    @Override
    public boolean stop(A accumulator, N node) {
        return delegate.stop(accumulator, node);
    }

    public static class DepthAccumulator<Y> {
        private Y elem;
        private ParentRelation relation;
        private BinaryTree<?>.Node previousNode;
        private BinaryTree<?>.Node previous;
        private int depth;

        public DepthAccumulator(Y initial) {
            this.elem = initial;
            depth = -1;
        }
        BinaryTree<?>.Node getPrevious() {
            return previous;
        }
        void setPrevious(BinaryTree<?>.Node previous) {
            this.previous = previous;
        }

        BinaryTree<?>.Node getPreviousNode() {
            return previousNode;
        }

        void setPreviousNode(BinaryTree<?>.Node previousNode) {
            this.previousNode = previousNode;
        }

        public ParentRelation getRelation() {
            return relation;
        }
        void setRelation(ParentRelation relation) {
            this.relation = relation;
        }

        public Y getElem() {
            return elem;
        }
        public void setElem(Y elem) {
            this.elem = elem;
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
