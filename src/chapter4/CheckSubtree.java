package chapter4;

import common.tree.BinaryTree;
import common.tree.BinaryTreeTraverser;
import common.tree.DepthTraverser;
import common.tuple.Tuple2;

import java.util.Objects;

import static common.tree.DepthTraverser.DepthAccumulator;

/**
 * T1 and T2 are two very large binary trees, with T1 much bigger than T2. Create an
 * algorithm to determine if T2 is a subtree of T1.
 * A tree T2 is a subtree of T1 if there exists a node n in T1 such that the subtree of n is identical to T2.
 * That is, if you cut off the tree at node n, the two trees would be identical.
 */
public class CheckSubtree<X> {

    private class SubtreeAccumulator {
        private boolean foundSubtree = false;
        private boolean isMismatch = false;
        private BinaryTree<X>.Node currentNode = null;
        private BinaryTree<X>.Node restartNode = null;
    }

    private class SubtreeTraverser implements BinaryTreeTraverser<DepthAccumulator<SubtreeAccumulator>, X, BinaryTree<X>.Node> {
        private final BinaryTree<X>.Node t2LeftMost;
        private Tuple2<DepthAccumulator<Void>, BinaryTree<X>.Node> t2Accumulator;
        private final DepthTraverser<DepthAccumulator<Void>, X, BinaryTree<X>.Node> simpleTraverse = new DepthTraverser<>((a, n) -> a);

        public SubtreeTraverser(BinaryTree<X>.Node leftMost, Tuple2<DepthAccumulator<Void>, BinaryTree<X>.Node> t2Accumulator) {
            this.t2LeftMost = leftMost;
            this.t2Accumulator = t2Accumulator;
        }

        @Override
        public DepthAccumulator<SubtreeAccumulator> visitNode(DepthAccumulator<SubtreeAccumulator> accumulator, BinaryTree<X>.Node t1Node) {
            SubtreeAccumulator elem = accumulator.getElem();

            // we found a new node where we can restart at the leftMost position
            if(elem.foundSubtree && t1Node.getLeft() == null && elem.restartNode == null
                    && Objects.equals(t1Node.getElem(), t2LeftMost.getElem())) {
                // traverse T2
                BinaryTree.nextNode(t2Accumulator, simpleTraverse);
                elem.restartNode = t2Accumulator.getSecond();
            }

            if(Objects.equals(t1Node.getElem(), elem.currentNode.getElem()) &&
                    accumulator.getDepth() == t2Accumulator.getFirst().getDepth()) {
                BinaryTree.nextNode(t2Accumulator, simpleTraverse);
                elem.currentNode = t2Accumulator.getSecond();
                elem.foundSubtree = true;
            } else if(elem.foundSubtree) {
                elem.isMismatch = true;
                if(elem.restartNode == null) {
                    BinaryTree.nextNode(t2Accumulator, simpleTraverse);
                    elem.restartNode = t2Accumulator.getSecond();
                }
            }

            return accumulator;
        }

        @Override
        public boolean stop(DepthAccumulator<SubtreeAccumulator> accumulator, BinaryTree<X>.Node node) {
            return accumulator.getElem().foundSubtree || accumulator.getElem().isMismatch;
        }
    }

    public boolean isSubtree(BinaryTree<X> t1, BinaryTree<X> t2) {
        // t1 should always have a null node
        if(t2.isEmpty())
            return true;

        DepthAccumulator<Tuple2<BinaryTree<X>.Node, BinaryTree<X>.Node>> t2LeftElems = new DepthAccumulator<>(Tuple2.make(null, null));
        t2LeftElems = t2.traverse(t2LeftElems, new DepthTraverser<>(
            // get our first element, should be leftmost
            (accumulator, node) -> {
                if(accumulator.getElem().getFirst() == null) {
                    accumulator.getElem().setFirst(node);
                } else if(accumulator.getElem().getSecond() == null) {
                    accumulator.getElem().setSecond(node);
                }
                return accumulator;
            },
            // stop condition: stop after visiting both elements
            (accumulator, node) -> accumulator.getElem().getFirst() != null && accumulator.getElem().getSecond() != null
        ));
        BinaryTree<X>.Node t2LeftMost = t2LeftElems.getElem().getFirst();
        BinaryTree<X>.Node t2LeftMostNext = t2LeftElems.getElem().getSecond();

        DepthAccumulator<SubtreeAccumulator> result;
        BinaryTree<X>.Node restartNode = t1.getHead();
        Tuple2<DepthAccumulator<Void>, BinaryTree<X>.Node> t2Accumulator = Tuple2.make(new DepthAccumulator<>(null), t2LeftMost);
        do {
            // traverse t1. If we encounter a mismatch, we go back the point we should restart from and traverse from there
            // if there are no points where we can restart from, we will return false
            // if we found the entire sub-tree, we will return true
            result = t1.traverse(
                new DepthAccumulator<>(new SubtreeAccumulator()),
                restartNode,
                new DepthTraverser<>(new SubtreeTraverser(t2LeftMost, t2Accumulator))
            );
            restartNode = result.getElem().restartNode;
            t2Accumulator.setSecond(t2LeftMostNext);
        } while(restartNode != null && result.getElem().isMismatch);

        return result.getElem().foundSubtree && !result.getElem().isMismatch;
    }

}
