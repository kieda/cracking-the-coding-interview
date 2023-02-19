package chapter4;

import common.tree.BinaryTree;
import common.tree.ParentRelation;

/**
 * Write an algorithm to find the "next" node (i.e., in-order successor) of a given node in a
 * binary search tree. You may assume that each node has a link to its parent.
 */
public class Successor<X> {
    /**
     * returns next item to the right of the tree.
     * If this is the last node in the tree, we return null
     */
    public BinaryTree<X>.Node getNextInOrder(BinaryTree<X>.Node node) {


        /*
         *       10
         *      /
         *     3
         *       \
         *        4
         *         \
         *          5
         *
         *      10
         *      / \
         *     7   11
         *    /
         *   4
         *
         *   1
         *    \
         *     7
         *    /
         *   4
         */
        BinaryTree<X>.Node previous = null; // keep previous node so we don't traverse back down the tree
        while(node != null) {
            if(node.getRight() != null && node.getRight() != previous){
                BinaryTree<X>.Node result = node.getRight();
                while(result.getLeft() != null) {
                    result = result.getLeft();
                }
                return result;
            } else if (ParentRelation.getRelation(node) == ParentRelation.RIGHT) {
                previous = node;
                // if we're to the right of our parent we continue traversing up
                node = node.getParent();
            } else {
                // if the child is on the left hand side, parent will be next element
                return node.getParent();
            }
        }
        return null;
    }
}
