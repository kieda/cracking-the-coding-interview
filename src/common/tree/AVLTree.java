package common.tree;

public class AVLTree<E extends Comparable<E>> extends BinarySearchTree<E> {
    private final int maxBalanceFactor;
    public AVLTree(int maxBalanceFactor) {
        this.maxBalanceFactor = maxBalanceFactor;
    }
    public AVLTree() {
        this(1);
    }
    public class Node extends BinarySearchTree<E>.Node {
        private int balanceFactor = 0;
        public Node(E elem) {
            super(elem);
        }

        public boolean isLeftHeavy() {
            return balanceFactor < 0;
        }
        public boolean isRightHeavy() {
            return balanceFactor > 0;
        }
        public boolean isBalanced() {
            return balanceFactor == 0;
        }
    }

    public Node insert(E elem) {
        Node newNode = new Node(elem);
        if(isEmpty()) {
            setHead(newNode);
            return newNode;
        }
        return null; // todo
    }

}
