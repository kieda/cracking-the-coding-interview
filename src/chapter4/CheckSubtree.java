package chapter4;

import common.tree.BinaryTree;
import common.tree.BinaryTreeTraverser;
import common.tree.DepthTraverser;
import common.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static common.tree.DepthTraverser.DepthAccumulator;

/**
 * T1 and T2 are two very large binary trees, with T1 much bigger than T2. Create an
 * algorithm to determine if T2 is a subtree of T1.
 * A tree T2 is a subtree of T1 if there exists a node n in T1 such that the subtree of n is identical to T2.
 * That is, if you cut off the tree at node n, the two trees would be identical.
 */
public class CheckSubtree<X> {

    private class NodeAndDepth{
        BinaryTree<X>.Node node = null;
        int depth;
        public NodeAndDepth(BinaryTree<X>.Node node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }
    private class SubtreeAccumulator {
        private boolean foundSubtree = false;
        private boolean isMismatch = false;
        private BinaryTree<X>.Node currentNode = null;
        private BinaryTree<X>.Node restartNode = null;
        private int restartDepthDelta = 0; // is the starting depth
    }

    /* simple traverser for T2, where we collect the depth information */
    private final DepthTraverser<DepthAccumulator<Void>, X, BinaryTree<X>.Node> simpleTraverse = new DepthTraverser<>((a, n) -> a);

    private class SubtreeTraverser implements BinaryTreeTraverser<DepthAccumulator<SubtreeAccumulator>, X, BinaryTree<X>.Node> {
        private int leftMostDepthDelta;
        private final BinaryTree<X>.Node t2LeftMost;
        private Tuple2<DepthAccumulator<Void>, BinaryTree<X>.Node> t1Increment;
        private Tuple2<DepthAccumulator<Void>, BinaryTree<X>.Node> t2Increment;

        // todo - move t2Accumulator to SubtreeAccumulator. t2Accumulator will also keep track of our current node in t2
        public SubtreeTraverser(BinaryTree<X>.Node leftMost, int leftMostDepthDelta,
                                // used to advance T1 and T2 to the next node
                                Tuple2<DepthAccumulator<Void>, BinaryTree<X>.Node> t1Increment,
                                Tuple2<DepthAccumulator<Void>, BinaryTree<X>.Node> t2Increment ) {
            this.t2LeftMost = leftMost;
            this.t1Increment = t1Increment;
            this.t2Increment = t2Increment;
            this.leftMostDepthDelta = leftMostDepthDelta;
        }

        @Override
        public DepthAccumulator<SubtreeAccumulator> visitNode(DepthAccumulator<SubtreeAccumulator> accumulator, BinaryTree<X>.Node t1Node) {
            SubtreeAccumulator elem = accumulator.getElem();
            // traverse T2 forward
            BinaryTree.nextNode(t2Increment, simpleTraverse);
            int t1Depth = accumulator.getDepth();
            int t2Depth = t2Increment.getFirst().getDepth(); // current depth

            // we are already traversing a tree, however if this one does not work out set the restartNode
            // restartNode is set to the next node after this one in T1 so we don't have to check this twice
            // we found a new node where we can restart at the leftMost position
            if(elem.foundSubtree && t1Node.getLeft() == null && elem.restartNode == null
                    && Objects.equals(t1Node.getElem(), t2LeftMost.getElem())) {
                // if we have to restart, restart from the next node right after the first match
                // also store the depth that this traversal took, which is used in our next traversal
                t1Increment.setSecond(t1Node);
                t1Increment.getFirst().setDepth(t1Depth);
                BinaryTree.nextNode(t1Increment, simpleTraverse);
                elem.restartNode = t1Increment.getSecond();

                // the delta we add to the depth when we restart from the restartNode
                // should be final T1 depth - initial T1 depth
                elem.restartDepthDelta = t1Increment.getFirst().getDepth() - t1Depth;
            }

            boolean nodesEqual = Objects.equals(t1Node.getElem(), elem.currentNode.getElem());
            if(nodesEqual) {
                if (elem.foundSubtree) {
                    t1Depth = t1Depth + elem.restartDepthDelta;
                    t2Depth = t2Depth + leftMostDepthDelta;
                } else {
                    // we found a match for the first time (T2's leftMost and T1's currentNode)
                    // Since we're traversing starting from the leftmost node of T2, its initial depth should be zero
                    // T1 depth should also be zero when we start
                    elem.restartDepthDelta = -t1Depth;
                    leftMostDepthDelta = 0;
                    t1Depth = 0;
                }
            }

            if(nodesEqual && t1Depth == t2Depth) {
                // we have another match from T1 to T2
                elem.currentNode = t2Increment.getSecond();
                elem.foundSubtree = true;
            } else if(elem.foundSubtree) {
                // we are already traversing a subtree, but we found a mismatch either in the node we're visiting or in the depth
                // therefore, this is a mismatch
                elem.isMismatch = true;
                // if we haven't found a node where we should restart from, restart from the next node
                if(elem.restartNode == null) {
                    // restart T1 from the next position, after our mismatch
                    // also store the depth
                    t1Increment.setSecond(t1Node);
                    t1Increment.getFirst().setDepth(t1Depth);
                    BinaryTree.nextNode(t2Increment, simpleTraverse);
                    elem.restartNode = t1Increment.getSecond();
                    elem.restartDepthDelta = t1Increment.getFirst().getDepth() - t1Depth;
                    elem.foundSubtree = false; // when we restart, we are not currently in a subtree
                }
            }

            return accumulator;
        }

        @Override
        public boolean stop(DepthAccumulator<SubtreeAccumulator> accumulator, BinaryTree<X>.Node node) {
            // if it's a mismatch, we stop
            // we also stop if we have found a subtree and we have traversed t2 till there are no more nodes left
            return accumulator.getElem().isMismatch || (accumulator.getElem().foundSubtree && t2Increment.getSecond() == null);
        }
    }


    public boolean isSubtree(BinaryTree<X> t1, BinaryTree<X> t2) {
        // t1 should always have a null node
        if(t2.isEmpty())
            return true;

        // get first two in-order nodes of T2
        // when restarting a search, we can restart from the second node rather than starting from the first again.
        List<NodeAndDepth> t2LeftElems = t2.traverse(
                new DepthAccumulator<>(new ArrayList<NodeAndDepth>(2)),
                new DepthTraverser<>(
                    // get our first element, should be leftmost
                    (accumulator, node) -> {
                        accumulator.getElem().add(new NodeAndDepth(node, accumulator.getDepth()));
                        return accumulator;
                    },
                    // stop condition: stop after visiting both elements
                    (accumulator, node) -> accumulator.getElem().size() >= 2 // stop when we reach two elements
        )).getElem();
        NodeAndDepth t2LeftMost = t2LeftElems.get(0);
        NodeAndDepth t2LeftMostNext = t2LeftElems.size() <= 1 ? null : t2LeftElems.get(1);
        BinaryTree<X>.Node t2LeftMostNode = t2LeftMost.node;
        BinaryTree<X>.Node t2LeftMostNextNode = t2LeftMostNext == null ? null : t2LeftMostNext.node;
        int t2LeftDelta = (t2LeftMostNext == null ? 0 : t2LeftMostNext.depth) - t2LeftMost.depth;

        DepthAccumulator<SubtreeAccumulator> result;
        BinaryTree<X>.Node restartNode = t1.getHead();
        Tuple2<DepthAccumulator<Void>, BinaryTree<X>.Node> t1Increment = Tuple2.make(new DepthAccumulator<>(null), t1.getHead());
        Tuple2<DepthAccumulator<Void>, BinaryTree<X>.Node> t2Increment = Tuple2.make(new DepthAccumulator<>(null), t2LeftMostNode);
        SubtreeAccumulator t1Accumulator = new SubtreeAccumulator();
        do {
            // reset this flag
            t1Accumulator.isMismatch = false;

            // traverse t1. If we encounter a mismatch, we go back the point we should restart from and traverse from there
            // if there are no points where we can restart from, we will return false
            // if we found the entire sub-tree, we will return true
            result = t1.traverse(
                new DepthAccumulator<>(t1Accumulator),
                restartNode,
                new DepthTraverser<>(new SubtreeTraverser(t2LeftMostNode, t2LeftDelta, t1Increment, t2Increment))
            );
            // node in T1 we should restart from
            restartNode = result.getElem().restartNode;

            // on a mismatch reset T2.
            if(result.getElem().isMismatch) {
                // reset depth info on T2
                t2Increment.setFirst(new DepthAccumulator<>(null));
                // if we're starting from a subtree, start from the next node since leftmost was already checked
                // otherwise start from leftmost node
                t2Increment.setSecond(result.getElem().foundSubtree ? t2LeftMostNextNode : t2LeftMostNode);
            }

            // clear out t1 restart node
            t1Accumulator.restartNode = null;
            t1Accumulator.foundSubtree = result.getElem().foundSubtree;
        } while(restartNode != null && result.getElem().isMismatch);

        // edge case : nodes of T2 match the end of T1, but not all nodes of T2 are traversed.
        // we traverse one more time and check that the final node in T2 will be null, implying the entire T2 was traversed
        BinaryTree.nextNode(t2Increment, simpleTraverse);

        return result.getElem().foundSubtree && !result.getElem().isMismatch && t2Increment.getSecond() == null;
    }

}
