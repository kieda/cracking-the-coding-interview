package chapter4;

import common.tree.BinarySearchTree;
import common.tree.BinaryTree;

import java.util.ArrayList;
import java.util.List;

/**
 * A binary search tree was created by traversing through an array from left to right
 * and inserting each element. Given a binary search tree with distinct elements, print all possible
 * arrays that could have led to this tree.
 *
 * EXAMPLE:
 *      Input:
 *          2
 *         / \
 *        1   3
 *      Output: {2, 1, 3}, {2, 3, 1}
 */
public class BSTSequences<X> {
    public int getSequences(BinaryTree<X>.Node root, List<X[]> accum, int length, int position) {
        if(root == null) {
            return 0;
        } else if(root.isLeaf()) {
            X[] items = (X[])new Object[length];
            items[length - 1] = root.getElem();
            accum.add(items);
            return 1;
        }
        int before = accum.size();
        int leftSize = getSequences(root.getLeft(), accum, length, before - 1);
        int afterLeft = accum.size();
        int rightSize = getSequences(root.getRight(), accum, length, afterLeft - 1);
        int afterRight = accum.size();

        // add
        for(int i = before; i < afterLeft; i++) {
            X[] leftEntry = accum.get(i);
            leftEntry[leftSize - 1] = root.getElem();
        }
        for(int i = afterLeft; i < afterRight; i++) {
            X[] rightEntry = accum.get(i);
            rightEntry[rightSize - 1] = root.getElem();
        }

        // aftwe weave, we fill elements of X[] such that all elements are included from the left side and right side
        return leftSize + rightSize;
    }

    // leftSize: number of elems in X[] on left that are filled to weave
    // rightSize: number of elems in X[]on right that are filled to weave
    // posLeft, leftLen: position and length of items we added from left traversal
    // posRight, rightLen: position and length of items we added from right traversal
    public void weave(List<X[]> accum, int leftSize, int rightSize, int posLeft, int leftLen, int posRight, int rightLen) {

    }
    public List<X[]> getSequences(BinaryTree<X> tree) {
        int size = tree.traverse(0, (a, n) -> a + 1);
        List<X[]> accum = new ArrayList<>();
        getSequences(tree.getHead(), accum, size, 0);
        return accum;
    }

}
