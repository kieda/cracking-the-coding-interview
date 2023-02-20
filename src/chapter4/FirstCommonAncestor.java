package chapter4;

import common.lists.SingleLinkedList;
import common.tree.BinaryTree;

/**
 * Design an algorithm and write code to find the first common ancestor
 * of two nodes in a binary tree. Avoid storing additional nodes in a data structure. NOTE: This is not
 * necessarily a binary search tree.
 */
public class FirstCommonAncestor<X> {
    /**
     * First case: we have links to the parent nodes.
     * Then the traversal is rather simple as we find the heads of each, compare the two, and traverse again to find the correct node
     *
     * We basically copy/pasted the solution for chapter2.Intersection and made it fit for binary trees
     *
     * Runs in O(depth(a) + depth(b))
     * Takes O(1) space
     */
    public BinaryTree<X>.Node getFirstCommonAncestorViaParents(final BinaryTree<X>.Node a, final BinaryTree<X>.Node b) {
        BinaryTree<X>.Node aNode = a;
        BinaryTree<X>.Node bNode = b;

        BinaryTree<X>.Node aPrevious = null;
        BinaryTree<X>.Node bPrevious = null;
        while(aNode != null && bNode != null) {
            aPrevious = aNode;
            bPrevious = bNode;
            aNode = aNode.getParent();
            bNode = bNode.getParent();
        }

        // run to the end of the length
        int aExtra = 0;
        int bExtra = 0;
        while(aNode != null) {
            aExtra++;
            aPrevious = aNode;
            aNode = aNode.getParent();
        }
        while(bNode != null) {
            bExtra++;
            bPrevious = bNode;
            bNode = bNode.getParent();
        }
        if(aPrevious != bPrevious) {
            return null;
        }
        aNode = a;
        bNode = b;
        // move forward from the start position the extra amount of space in the two lists
        for(int i = 0; i < aExtra; i++)
            aNode = aNode.getParent();
        for(int i = 0; i < bExtra; i++)
            bNode = bNode.getParent();
        while(aNode != bNode) {
            aNode = aNode.getParent();
            bNode = bNode.getParent();
        }
        return aNode;
    }

    /**
     * Find common ancestor without links to the parents
     *
     * Better than the book solution, which requires (recursive) stacks to traverse the tree and pass up the ancestor.
     * Here, we can do better than that by utilizing our good friend the morris traversal
     *
     * O(Max(pos(a), pos(b))) runtime complexity
     * O(1) space complexity
     */
    public BinaryTree<X>.Node getFirstCommonAncestorNoParents(BinaryTree<X> tree, BinaryTree<X>.Node a, BinaryTree<X>.Node b) {
        if(a == null || b == null)
            return null;

        // idea:
        // First stage: Find the first node (in a visit). This is "result". Then, enter second stage.
        // Second stage: If we visit the second node, then we clean up and return result (such that result does not change by the below conditions)
        //               If we visit a node where node.left == result, then result = node
        //                     (move up one, we traversed the subtrees of result and didn't find it)
        //               If we are going to exit the rightmost traversal and we come across the first node, then result = current.left
        //                     (move up right subtrees)

        BinaryTree<X>.Node first = null;
        BinaryTree<X>.Node second = null;


        boolean stop = false; // flag to stop visiting nodes
        int edgesAdded = 0;

        BinaryTree<X>.Node result = null;
        BinaryTree.Node current = tree.getHead();
        while(current != null) {
            if(current.getLeft() == null) {
                if(!stop) {
                    if(first == null && (current == a || current == b)) {
                        // we found the first node
                        // we set the first node we found, along with the second node we expect to see
                        first = current == a ? a : b;
                        second = current == b ? a : b;
                        // result is the first node we visited, this may traverse upwards
                        result = first;
                    }

                    if(second == current) {
                        stop = true;
                    }

                    // we found both nodes and we've cleaned up all our added edges.
                    // Return our result.
                    if(stop && edgesAdded == 0) {
                        return result;
                    }
                }

                current = current.getRight();
            } else {
                // make current the the right child of the rightmost node in current's left subtree
                boolean foundResult = false;
                BinaryTree.Node previous = current.getLeft();

                while(previous.getRight() != null && previous.getRight() != current) {
                    previous = previous.getRight();
                    if(!stop && result != null && previous == result) {
                        foundResult = true;
                    }
                }

                if(previous.getRight() == null) {
                    // add an edge back to the current node
                    edgesAdded++;
                    previous.setRight(current);
                    current = current.getLeft();
                } else {
                    // remove added edge
                    edgesAdded--;
                    previous.setRight(null);

                    // visit the current node
                    if(first == null && (current == a || current == b)) {
                        // we found the first node
                        // we set the first node we found, along with the second node we expect to see
                        first = current == a ? a : b;
                        second = current == b ? a : b;
                        // result is the first node we visited, this may traverse upwards
                        result = first;
                    }

                    if(!stop && (foundResult || (result != null && current.getLeft() == result))) {
                        // we are exiting this subtree for good (by visiting the rightmost node)
                        // so, we start searching in the parent's subtree for the node
                        // traverse up the tree
                        result = current;
                    }

                    // second will be null if we haven't found first. current cannot be null by loop invariant
                    // thus we will only execute this branch if we found first and second
                    if(second == current) {
                        stop = true;
                    }

                    // stop condition: we reset all our edges and we've reached a stopping point
                    if(stop && edgesAdded == 0)
                        return result;

                    current = current.getRight();
                }
            }
        }

        // stop is true if and only if we found both nodes
        // if we did not find both nodes, return null
        return stop ? result : null;
    }
}
