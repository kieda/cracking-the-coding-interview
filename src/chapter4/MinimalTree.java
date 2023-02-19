package chapter4;

import common.tree.BinarySearchTree;
import common.tree.BinaryTree;
import common.tree.ParentRelation;

/**
 * Given a sorted (increasing order) array with unique integer elements, write an algorithm
 * to create a binary search tree with minimal height.
 */
public class MinimalTree<X extends Comparable<X>> {
    private static int getRealIndex(int inOrderIndex, int cutOff) {
        inOrderIndex--;
        if(inOrderIndex <= cutOff)
            return inOrderIndex;
        else
            // note: here, inOrderIndex should always be odd
            //       and cutOff should always be odd (because a full capacity BST should have an odd number of elements.)
            return (inOrderIndex - cutOff) / 2 + cutOff;
    }
    public BinarySearchTree<X> make(X... orderedItems) {
        BinarySearchTree<X> bst = new BinarySearchTree<>();
        if(orderedItems.length == 0)
            return bst;

        // 1. find out how many levels will be completely filled based on orderedItems.length
        //    find out how many items will be on the bottom row.
        // 2. start from top, and create the tree in a traversal, creating the middle node, then left node,
        //    then right node
        //       - keep track of the current depth and current index we're pulling from
        //       - label the indices of the nodes we're traversing. Transform them to actual indices in the array

        int highBit = Integer.highestOneBit(orderedItems.length); // highBit is the traversal index of the root
        // has the form 111111 representing a complete tree
        int fullTree = ((highBit - 1) | highBit);
        // number of leaves required to complete the bottom row
        int numLeavesToComplete = fullTree - orderedItems.length;

        final int cutOff = fullTree - numLeavesToComplete * 2;

        BinarySearchTree<X>.Node node = bst.makeNode(orderedItems[getRealIndex(highBit, cutOff)]);
        bst.setHead(node);

        int depth = 0;
        int traversalIndex = highBit;
        int indexModifier = (highBit>>>(depth+1));

        int skipDepth = 0; // set to 1 to skip leaves

        constructLoop:
        while(node != null) {
            // start skipping nodes when we reach this one.
            // since we construct from left to right, we will not miss any nodes
            if(skipDepth == 0 && traversalIndex == cutOff + 1)
                skipDepth = 1;

            if(node.getLeft() == null && indexModifier > skipDepth) {
                depth++;
                indexModifier = (highBit>>>(depth+1));
                traversalIndex -= indexModifier;
                // add a node to the left
                BinarySearchTree<X>.Node left = bst.makeNode(orderedItems[getRealIndex(traversalIndex, cutOff)]);
                node.setLeft(left);
                left.setParent(node);
                // traverse left
                node = left;
            } else {
                while(node != null && (node.getRight() != null || indexModifier <= skipDepth)) {
                    depth--;
                    indexModifier = (highBit>>>(depth+1));

                    // traverse up
                    switch(ParentRelation.getRelation(node)) {
                        case LEFT:
                            // traverse upward: add back our index
                            traversalIndex += indexModifier;
                            break;
                        case RIGHT:
                            // traverse upward: subtract back our index
                            traversalIndex -= indexModifier;
                            break;
                        case HEAD:
                            // if we're at the head and our right tree is already filled out, just exit
                            break constructLoop;
                    }
                    node = node.getParent();
                }
                depth++;
                // we add a node to the right
                traversalIndex += indexModifier;

                // add node to the right and continue
                BinarySearchTree<X>.Node right = bst.makeNode(orderedItems[getRealIndex(traversalIndex, cutOff)]);
                node.setRight(right);
                right.setParent(node);
                node = right;
            }
        }

        return bst;
    }
}
