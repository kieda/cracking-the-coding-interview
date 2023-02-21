package chapter4;

import common.lists.DoubleLinkedList;
import common.tree.BinaryTree;
import common.tree.DepthTraverser;

import java.util.ArrayList;
import java.util.List;

/**
 * Given a binary tree, design an algorithm which creates a linked list of all the nodes
 * at each depth (e.g., if you have a tree with depth D, you'll have D linked lists).
 */
public class ListOfDepths<X> {
    public List<DoubleLinkedList<BinaryTree<X>.Node>> getNodesAtDepths(BinaryTree<X> tree) {
        List<DoubleLinkedList<BinaryTree<X>.Node>> initial = new ArrayList<>();
        return tree.traverse(new DepthTraverser.DepthAccumulator<>(initial), new DepthTraverser<>((a, n) -> {
            int depth = a.getDepth();
            List<DoubleLinkedList<BinaryTree<X>.Node>> elem = a.getElem();
            while(elem.size() <= depth) {
                elem.add(new DoubleLinkedList<>());
            }

            elem.get(depth).addLast(n);
            a.setElem(elem);
            return a;
        })).getElem();
    }
}
