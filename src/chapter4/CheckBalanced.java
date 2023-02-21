package chapter4;

import common.tree.BinaryTree;
import common.tree.DepthTraverser;

import java.util.ArrayList;
import java.util.List;

/**
 * Implement a function to check if a binary tree is balanced. For the purposes of
 * this question, a balanced tree is defined to be a tree such that the heights of the two subtrees of any
 * node never differ by more than one.
 */
public class CheckBalanced {
    public <X> boolean isBalancedParallel(BinaryTree<X> tree) {
        return false; // todo
    }


    /**
     * perform an in-order traversal of all of the nodes
     * @param tree
     * @param <X>
     * @return
     */
    public <X> boolean isBalancedInOrder(BinaryTree<X> tree) {
        if(tree.isEmpty())
            return true;
        List<Integer> initial = new ArrayList<>();
        // traverse in-order and add depth count to the list for each one.
        List<Integer> depthCounts = tree.traverse(new DepthTraverser.DepthAccumulator<>(initial), new DepthTraverser<>((a, n) -> {
            int depth = a.getDepth();
            List<Integer> counts = a.getElem();
            while(counts.size() <= depth) {
                counts.add(0);
            }
            counts.set(depth, counts.get(depth) + 1);
            return a;
        })).getElem();

        // everthing should be filled, except for the last row (which may be partially full)
        int expectedFill = 1;
        for(int depth = 0; depth < depthCounts.size() - 1; depth++) {
            if(!(expectedFill == depthCounts.get(depth)))
                return false;
            expectedFill = expectedFill << 1;
        }
        // need 1 or more on the last row
        return depthCounts.get(depthCounts.size() - 1) > 0;
    }
}
