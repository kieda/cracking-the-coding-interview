package common.tree;

import common.Sort;

public class AVLTree<E extends Comparable<E>> extends BinarySearchTree<E> {
    private final int maxBalanceFactor;
    public AVLTree(int maxBalanceFactor) {
        if(maxBalanceFactor < 1)
            throw new IllegalArgumentException("Max Balance factor " + maxBalanceFactor + " must be >= 1");
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

        @Override
        public Node getParent() {
            return (Node) super.getParent();
        }

        @Override
        public Node getLeft() {
            return (Node) super.getLeft();
        }

        @Override
        public Node getRight() {
            return (Node) super.getRight();
        }

        protected int getBalanceFactor() {
            return balanceFactor;
        }
        protected void setBalanceFactor(int balanceFactor) {
            this.balanceFactor = balanceFactor;
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
        public boolean inBounds() {
            return Math.abs(balanceFactor) <= maxBalanceFactor;
        }
    }


    /**
     * @param node the node we added
     */
    private void nodeAdded(Node node) {
        Node grandChild = null;
        Node child = null;

        // walk up the ancestors and update the balance factor on each node
        while(node != null) {
            // if the node added is to the right of the parent, we update the parent's balance factor to indicate we added a node to the right
            // if the node is added to the left of the parent, do the same.
            // if the added node sets balance to zero, then the ancestors will be unaffected

            ParentRelation parentRelation = ParentRelation.getRelation(node);
            Node parent = node.getParent();
            switch(parentRelation) {
                case NONE:
                    throw new MissingChildException("child", node.getParent(), node);
                case LEFT:
                    parent.setBalanceFactor(parent.getBalanceFactor() - 1);
                    break;
                case RIGHT:
                    parent.setBalanceFactor(parent.getBalanceFactor() + 1);
                    break;
            }

            // if balance factor becomes zero, we can stop retracing, as all above nodes will not change
            // because parent is balanced on either side
            if(parent.isBalanced())
                return;

            /*
             * When a node is added, we have the following derivations for what the new balance factor should be,
             * regardless of the max balance factor. The derivations are photographed and can be viewed in
             *    /derivations/avltree
             *
             * Let A be the unbalanced node, B its child, and C its grandchild
             *
             * RIGHT RIGHT:
             *      A_1 = A_0 - B_0 - 1
             *      B_1 = B_0 - 1
             *      C_1 = C_0
             * LEFT LEFT:
             *      A_1 = A_0 - B_0 + 1
             *      B_1 = B_0 + 1
             *      C_1 = C_0
             * LEFT RIGHT, and node inserted is C
             * RIGHT LEFT, and node inserted is C
             *      A_1 = 0
             *      B_1 = 0
             *      C_1 = 0
             *      this case will only occur if the other children of A and B are both null.
             *      Children of C will also be null since this node was just inserted.
             *      Thus, the max balance factor would also have to be 1 in order for this case to trigger
             * RIGHT LEFT, and node inserted into C's LEFT child:
             *      A_1 = A_0 - 2
             *      B_1 = B_0 - C_0 + 1
             *      C_1 = C_0
             * RIGHT LEFT, and node inserted into C's RIGHT child:
             *      A_1 = A_0 - C_0 - 2
             *      B_1 = B_0 + 1
             *      C_1 = C_0
             * LEFT RIGHT, and node inserted into C's LEFT child:
             *      A_1 = A_0 - C_0 + 2
             *      B_1 = B_0 - 1
             *      C_1 = C_0
             * LEFT RIGHT, and node inserted into C's RIGHT child:
             *     A_1 = A_0 + 2
             *     B_1 = B_0 - C_0 - 1
             *     C_1 = C_0
             */
            if(!parent.inBounds()) {
                switch (parentRelation) {
                    case LEFT:
                        if(node.isLeftHeavy() || node.isBalanced()) {
                            Node A = parent;
                            Node B = node;
                            // Node C = node.getLeft()

                            parent.rightRotate();
                            /* A_1 = A_0 - B_0 + 1
                             * B_1 = B_0 + 1
                             * C_1 = C_0
                             */
                            A.setBalanceFactor(A.getBalanceFactor() - B.getBalanceFactor() + 1);
                            B.setBalanceFactor(B.getBalanceFactor() + 1);
                        } else {
                            Node A = parent;
                            Node B = node;
                            Node C = node.getRight();

                            parent.leftRightRotate();
                            if(grandChild == null) {
                                /* LEFT RIGHT, and node inserted is C
                                 * A_1 = 0
                                 * B_1 = 0
                                 * C_1 = 0
                                 */
                                A.setBalanceFactor(0);
                                B.setBalanceFactor(0);
                            } else switch(ParentRelation.getRelation(grandChild)) {
                                case LEFT:
                                    /* LEFT RIGHT, and node inserted into C's LEFT child:
                                     * A_1 = A_0 - C_0 + 2
                                     * B_1 = B_0 - 1
                                     * C_1 = C_0
                                     */
                                    A.setBalanceFactor(A.getBalanceFactor() - C.getBalanceFactor() + 2);
                                    B.setBalanceFactor(B.getBalanceFactor() - 1);
                                    break;
                                case RIGHT:
                                    /* LEFT RIGHT, and node inserted into C's RIGHT child:
                                     * A_1 = A_0 + 2
                                     * B_1 = B_0 - C_0 - 1
                                     * C_1 = C_0
                                     */
                                    A.setBalanceFactor(A.getBalanceFactor() + 2);
                                    B.setBalanceFactor(B.getBalanceFactor() - C.getBalanceFactor() - 1);
                                    break;
                                default:
                                    throw new IllegalStateException();
                            }
                        }
                    case RIGHT:
                        if(node.isRightHeavy() || node.isBalanced()) {
                            Node A = parent;
                            Node B = node;
                            // Node C = node.getRight()

                            parent.leftRotate();
                            /* A_1 = A_0 - B_0 - 1
                             * B_1 = B_0 - 1
                             * C_1 = C_0
                             */
                            A.setBalanceFactor(A.getBalanceFactor() - B.getBalanceFactor() - 1);
                            B.setBalanceFactor(B.getBalanceFactor() - 1);
                        } else {
                            Node A = parent;
                            Node B = node;
                            Node C = node.getLeft();
                            // grandChild.getParent() == C

                            parent.rightLeftRotate();

                            if(grandChild == null) {
                                /* RIGHT LEFT, and node inserted is C. Happens when grandChild == null
                                 * A_1 = 0
                                 * B_1 = 0
                                 * C_1 = 0
                                 */
                                A.setBalanceFactor(0);
                                B.setBalanceFactor(0);
                            } else switch(ParentRelation.getRelation(grandChild)) {
                                case LEFT:
                                    /* RIGHT LEFT, and node inserted into C's LEFT child:
                                     * A_1 = A_0 - 2
                                     * B_1 = B_0 - C_0 + 1
                                     * C_1 = C_0
                                     */
                                    A.setBalanceFactor(A.getBalanceFactor() - 2);
                                    B.setBalanceFactor(B.getBalanceFactor() - C.getBalanceFactor() + 1);
                                    break;
                                case RIGHT:
                                    /* RIGHT LEFT, and node inserted into C's RIGHT child:
                                     * A_1 = A_0 - C_0 - 2
                                     * B_1 = B_0 + 1
                                     * C_1 = C_0
                                     */
                                    A.setBalanceFactor(A.getBalanceFactor() - C.getBalanceFactor() - 2);
                                    B.setBalanceFactor(B.getBalanceFactor() + 1);
                                    break;
                                default:
                                    throw new IllegalStateException();
                            }
                        }
                }
            }
            grandChild = child;
            child = node;
            node = node.getParent();
        }
    }

    @Override
    public Node lookup(E data) {
        return (Node)super.lookup(data);
    }

    @Override
    public Node lookup(E data, SearchFlags flags) {
        return (Node)super.lookup(data, flags);
    }

    public Node insert(E elem) {
        Node newNode = new Node(elem);
        if(isEmpty()) {
            setHead(newNode);
            return newNode;
        }

        Node insertionPoint = lookup(elem, SearchFlags.INSERTION_POINT);
        if(Sort.compare(elem, insertionPoint.getElem()) > 0) {
            insertionPoint.setRight(newNode);
        } else {
            insertionPoint.setLeft(newNode);
        }

        nodeAdded(insertionPoint);

        return newNode;
    }
    public void remove(Node node) {

    }
}
