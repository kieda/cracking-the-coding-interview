package chapter4;

import common.Sort;
import common.tree.BinaryTree;
import common.tree.BinaryTreeTraverser;
import common.tree.DepthTraverser;
import common.tree.ParentRelation;
import common.tuple.Tuple2;

import java.util.Optional;

/**
 * Implement a function to check if a binary tree is a binary search tree.
 */
public class ValidateBST<X extends Comparable<X>> {
    public boolean isBinarySearchTreeInOrder(BinaryTree<X> tree) {
        /*
         * O(N) time
         * O(1) space
         */
        Tuple2<Boolean, Optional<X>> initial = Tuple2.make(true, Optional.empty());
        return tree.traverse(
                new DepthTraverser.DepthAccumulator<>(initial),
                new DepthTraverser<>(
                        (a, node) -> {
                            Tuple2<Boolean, Optional<X>> state = a.getElem();
                            if(state.getSecond().isEmpty())
                                state.setSecond(Optional.of(node.getElem()));
                            else {
                                int comparison = Sort.compare(state.getSecond().get(), node.getElem());
                                if(comparison > 0 || (comparison == 0 && a.getRelation() != ParentRelation.LEFT))
                                    // next item is greater than our current one, BST invariant is invalid and we exit
                                    // or, next item is the same as this one but is not situated to the LEFT then the BST invariant is invalid
                                    state.setFirst(false);
                            }

                            state.setSecond(Optional.of(node.getElem()));
                            a.setElem(state);
                            return a;
                        },
                        (a, node) -> !a.getElem().getFirst()
                )).getElem().getFirst();
    }


    private boolean isBinarySearchTreeRecursive(BinaryTree<X>.Node node, Optional<X> min, Optional<X> max) {
        // base case: no node is a valid BST
        if(node == null)
            return true;

        // check node value against min and max bounds for violation
        if(min.map(val -> Sort.compare(node.getElem(), min.get()) <= 0).orElse(false)
                || max.map(val -> Sort.compare(val, node.getElem()) > 0).orElse(false))
            return false;

        // traverse left and right for violation
        if(!isBinarySearchTreeRecursive(node.getLeft(), min, Optional.of(node.getElem()))
                || !isBinarySearchTreeRecursive(node.getRight(), Optional.of(node.getElem()), max))
            return false;
        return true;
    }
    /*
     * O(N) time
     * O(log n) space
     */
    public boolean isBinarySearchTreeMinMax(BinaryTree<X> tree) {
        return isBinarySearchTreeRecursive(tree.getHead(), Optional.empty(), Optional.empty());
    }
}
