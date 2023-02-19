package chapter4;

import common.lists.DoubleLinkedList;
import common.lists.SingleLinkedList;
import common.tree.BinaryTree;
import common.tree.DepthTraverser;

import java.util.ArrayList;
import java.util.List;

/**
 * Given a binary tree, design an algorithm which creates a linked list of all the nodes
 * at each depth (e.g., if you have a tree with depth D, you'll have D linked lists).
 */
public class ListOfDepths<X> {

    private class ListOfDepthsAccumulator extends DepthTraverser.DepthAccumulator {
        private List<DoubleLinkedList<BinaryTree<X>.Node>> depthItems;
        public ListOfDepthsAccumulator() {
            this.depthItems = new ArrayList<>();
        }

        private void expand(int lastIndex) {
            while(depthItems.size() <= lastIndex) {
                depthItems.add(new DoubleLinkedList<>());
            }
        }

        public void addNode(BinaryTree<X>.Node node) {
            int depth = getDepth();
            expand(depth);
            depthItems.get(depth).addLast(node);
        }

        public List<DoubleLinkedList<BinaryTree<X>.Node>> getDepthItems() {
            return depthItems;
        }
    }

    public List<DoubleLinkedList<BinaryTree<X>.Node>> getNodesAtDepths(BinaryTree<X> tree) {
        return tree.traverse(new ListOfDepthsAccumulator(), new DepthTraverser<>((a, n, dir) -> {
            a.addNode(n);
            return a;
        })).getDepthItems();
    }
}
