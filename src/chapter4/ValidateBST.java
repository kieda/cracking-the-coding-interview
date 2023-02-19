package chapter4;

import common.Sort;
import common.tree.BinaryTree;
import common.tuple.Tuple2;

import java.util.Optional;

/**
 * Implement a function to check if a binary tree is a binary search tree.
 */
public class ValidateBST<X extends Comparable<X>> {
    public boolean isBinarySearchTreeInOrder(BinaryTree<X> tree) {
        // todo - traverse with current depth passed in. This would cover cases where there are duplicates if we check
        //        that if previous == current then previousDepth also has to be below currentDepth
        return tree.traverseNodes(
            new Tuple2<Boolean, Optional<X>>(true, Optional.empty()),
            (accum, node) -> {
                if(accum.getSecond().isEmpty())
                    accum.setSecond(Optional.of(node.getElem()));
                else if(Sort.compare(accum.getSecond().get(), node.getElem()) > 0)
                    accum.setFirst(false); // next item is greater than our current one, BST invariant is invalid and we exit

                accum.setSecond(Optional.of(node.getElem()));
                return accum;
            },
            // Exit early
            (accum, elem) -> !accum.getFirst()
        ).getFirst();
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
    public boolean isBinarySearchTreeMinMax(BinaryTree<X> tree) {
        return isBinarySearchTreeRecursive(tree.getHead(), Optional.empty(), Optional.empty());
    }
}
