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
     * Rebalances the node by analyzing the balance of its subtrees, making appropriate rotations, and
     * modifying the resulting balance factor.
     *
     * Cool fact: we can rebalance without storing the height for an arbitrary max balance factor!
     */
    private void rebalance(Node node) {
        if(!node.inBounds())
            return;

        // node out of bounds implies that abs(balance) > 1
        // this also implies that the node will at least have a child and a grandchild, as if it has no grandchildren
        // its max balance factor is +- 1

        if(node.isLeftHeavy()) {
            Node child = node.getLeft();
            if (child.isLeftHeavy() || child.isBalanced()) {
                /* left left case : do a right rotation
                 * balance(B_0) <= 0
                 * and balance(A_0) < 0
                 */
                Node A = node;
                Node B = child;

                node.rightRotate();
                /* A_1 = A_0 - B_0 + 1
                 * B_1 = B_0 + 1
                 * C_1 = C_0
                 */
                A.setBalanceFactor(A.getBalanceFactor() - B.getBalanceFactor() + 1);
                B.setBalanceFactor(B.getBalanceFactor() + 1);
            } else {
                Node A = node;
                Node B = child;
                Node C = child.getRight();

                boolean isLeafC = C.isLeaf();
                int balanceC = C.getBalanceFactor();

                node.leftRightRotate();

                if (isLeafC) {
                    /* LEFT RIGHT, and C is a leaf
                     *      A                       C
                     *     /  \                   /   \
                     *    B    T1                B     A
                     *  /   \  (f)   ====>
                     * T4    C
                     * (i)  (0)
                     *
                     *
                     * since balance(A_0) < 0 and balance(B_0) > 0, this implies that
                     * i == 0 and f == 0. Thus, we will always have the tree to the right if we rebalance.
                     *
                     * A_1 = 0
                     * B_1 = 0
                     * C_1 = 0
                     */

                    A.setBalanceFactor(0);
                    B.setBalanceFactor(0);
                    C.setBalanceFactor(0);
                } else if(balanceC > 0) {
                    /* LEFT RIGHT, and C is right heavy
                     *      A                       C
                     *     /  \                   /   \
                     *    B    T1                B     A
                     *  /   \  (f)   ====>      / \   / \
                     * T4    C                T4  T3 T2  T1
                     * (i)  / \              (i) (h) (g) (f)
                     *    T3  T2
                     *   (h)  (g)
                     * let height(T1) = f
                     *     height(T2) = g
                     *     height(T3) = h
                     *     height(T4) = i
                     *
                     *     and we know balance(C_0) > 0     (g > h)
                     *     and balance(B_0) > 0             (g + 1 - i > 0)
                     *     and balance(A_0) < 0             (f - (g + 2) <= 0)
                     * then balance(C_0) = g - h
                     *      balance(B_0) = g + 1 - i
                     *      balance(A_0) = f - (g + 2)
                     *      balance(B_1) = h - i          = B_0 - C_0 - 1
                     *      balance(A_1) = f - g          = A_0 + 2
                     *      balance(C_1) = g - max(i, h)
                     *
                     * (i <= h) iff B_1 >= 0
                     * then C_1 = g - h = C_0
                     * else C_1 = g - i = B_0 - 1
                     */

                    int originalBalanceFactorB = B.getBalanceFactor();
                    A.setBalanceFactor(A.getBalanceFactor() + 2);
                    B.setBalanceFactor(originalBalanceFactorB - C.getBalanceFactor() - 1);
                    if(B.getBalanceFactor() < 0) {
                        C.setBalanceFactor(originalBalanceFactorB - 1);
                    }
                } else {
                    /* LEFT RIGHT, and C is left heavy (or balanced)
                     *      A                       C
                     *     /  \                   /   \
                     *    B    T1                B     A
                     *  /   \  (f)   ====>      / \   / \
                     * T4    C                T4  T3 T2  T1
                     * (i)  / \              (i) (h) (g) (f)
                     *    T3  T2
                     *   (h)  (g)
                     * let height(T1) = f
                     *     height(T2) = g
                     *     height(T3) = h
                     *     height(T4) = i
                     *
                     *     and we know balance(C_0) <= 0    (h >= g)
                     *     and balance(B_0) > 0             (g + 1 - i > 0)
                     *     and balance(A_0) < 0             (f - (g + 2) <= 0)
                     * then balance(C_0) = g - h
                     *      balance(B_0) = h + 1 - i
                     *      balance(A_0) = f - (h + 2)
                     *      balance(B_1) = h - i          = B_0 - 1
                     *      balance(A_1) = f - g          = A_0 + C_0 + 2
                     *      balance(C_1) = max(g, f) - h
                     *
                     * (g >= f) iff A_1 <= 0
                     * then C_1 = g - h = C_0
                     * else C_1 = f - h = A_0 + 2
                     */
                    int originalBalanceFactorA = A.getBalanceFactor();
                    A.setBalanceFactor(A.getBalanceFactor() + C.getBalanceFactor() + 2);
                    B.setBalanceFactor(B.getBalanceFactor() - 1);
                    if(A.getBalanceFactor() > 0) {
                        C.setBalanceFactor(originalBalanceFactorA + 2);
                    }
                }
            }
        } else if(node.isRightHeavy()) {
            Node child = node.getRight();
            if (child.isRightHeavy() || child.isBalanced()) {
                /* right right case : do a left rotation
                 * balance(B_0) >= 0
                 * and balance(A_0) > 0
                 */
                Node A = node;
                Node B = child;

                node.leftRotate();
                /* A_1 = A_0 - B_0 - 1
                 * B_1 = B_0 - 1
                 * C_1 = C_0
                 */
                A.setBalanceFactor(A.getBalanceFactor() - B.getBalanceFactor() - 1);
                B.setBalanceFactor(B.getBalanceFactor() - 1);
            } else {
                Node A = node;
                Node B = child;
                Node C = child.getLeft();

                boolean isLeafC = C.isLeaf();
                int balanceC = C.getBalanceFactor();

                node.rightLeftRotate();

                if (isLeafC) {
                    /* RIGHT LEFT, and C is a leaf
                     *      A                       C
                     *     /  \                   /   \
                     *    T1   B                 A     B
                     *   (f)  / \   ====>
                     *       C   T4
                     *      (0)  (i)
                     *
                     *
                     * since balance(A_0) > 0 and balance(B_0) < 0, this implies that
                     * i == 0 and f == 0. Thus, we will always have the tree to the right if we rebalance.
                     *
                     * A_1 = 0
                     * B_1 = 0
                     * C_1 = 0
                     */

                    A.setBalanceFactor(0);
                    B.setBalanceFactor(0);
                    C.setBalanceFactor(0);
                } else if(balanceC < 0) {
                    /* RIGHT LEFT, and C is left heavy
                     *      A                       C
                     *     /  \                   /   \
                     *    T1   B                 A     B
                     *   (f)  / \   ====>       / \   / \
                     *       C   T4           T1  T2 T3  T4
                     *      / \  (i)         (f) (g) (h) (i)
                     *     T2  T3
                     *     (g) (h)
                     *
                     * let height(T1) = f
                     *     height(T2) = g
                     *     height(T3) = h
                     *     height(T4) = i
                     *
                     *     and we know balance(C_0) < 0     (g > h)
                     *     and balance(B_0) < 0
                     *     and balance(A_0) > 0
                     * then balance(C_0) = h - g
                     *      balance(B_0) = i - (g + 1)
                     *      balance(A_0) = g + 2 - f
                     *      balance(B_1) = i - h          = (B_0 - C_0 + 1)
                     *      balance(A_1) = g - f          = (A_0 - 2)
                     *      balance(C_1) = max(h, i) - g
                     *
                     * (h >= i) iff B_1 <= 0
                     * then C_1 = h - g = C_0
                     * else C_1 = i - g = B_0 + 1
                     */
                    int originalBalanceFactorB = B.getBalanceFactor();
                    A.setBalanceFactor(A.getBalanceFactor() - 2);
                    B.setBalanceFactor(B.getBalanceFactor() - C.getBalanceFactor() + 1);
                    if(B.getBalanceFactor() > 0) {
                        C.setBalanceFactor(originalBalanceFactorB + 1);
                    }
                } else {
                    /* RIGHT LEFT, and C is right heavy (or balanced)
                     *      A                       C
                     *     /  \                   /   \
                     *    T1   B                 A     B
                     *   (f)  / \   ====>       / \   / \
                     *       C   T4           T1  T2 T3  T4
                     *      / \  (i)         (f) (g) (h) (i)
                     *     T2  T3
                     *     (g) (h)
                     *
                     * let height(T1) = f
                     *     height(T2) = g
                     *     height(T3) = h
                     *     height(T4) = i
                     *
                     *     and we know balance(C_0) >= 0    (h >= g)
                     *     and balance(B_0) < 0
                     *     and balance(A_0) > 0
                     * then balance(C_0) = h - g
                     *      balance(B_0) = i - (h + 1)
                     *      balance(A_0) = h + 2 - f
                     *      balance(B_1) = i - h          = (B_0 + 1)
                     *      balance(A_1) = g - f          = (A_0 - C_0 - 2)
                     *      balance(C_1) = h - max(f, g)
                     *
                     * (g >= f) iff A_1 >= 0
                     * then C_1 = h - g = C_0
                     * else C_1 = h - f = A_0 - 2
                     */
                    int originalBalanceFactorA = A.getBalanceFactor();
                    A.setBalanceFactor(A.getBalanceFactor() - C.getBalanceFactor() - 2);
                    B.setBalanceFactor(B.getBalanceFactor() + 1);
                    if(A.getBalanceFactor() < 0) {
                        C.setBalanceFactor(originalBalanceFactorA - 2);
                    }
                }
            }
        }
    }

    /**
     * @param node the node we added
     */
    private void nodeAdded(Node node) {
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

            if(!parent.inBounds()) {
                rebalance(parent);
                // we only rebalance once on an insertion, as the newly inserted node is moved up one level.
                // thus, the rest does not need to be rebalanced
                return;
            }

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

    @Override
    public Node makeNode(E elem) {
        return new Node(elem);
    }

    @Override
    public Node getHead() {
        return (Node)super.getHead();
    }

    public Node insert(E elem) {
        Node newNode = makeNode(elem);
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

    /**
     * signifies that a node was removed under removalPoint with removalRelation == LEFT or RIGHT
     * we climb up removalPoint and its ancestors to recalculate balance and rebalance the tree
     */
    private void nodeRemoved(Node removalPoint, ParentRelation removalRelation) {
        while(removalPoint != null) {
            Node parent = removalPoint.getParent();
            ParentRelation parentRelation = ParentRelation.getRelation(removalPoint);

            int initialBalance = removalPoint.getBalanceFactor();
            switch(removalRelation) {
                case LEFT:
                    removalPoint.setBalanceFactor(initialBalance + 1);
                    break;
                case RIGHT:
                    removalPoint.setBalanceFactor(initialBalance - 1);
                    break;
                default:
                    throw new IllegalStateException();
            }

            if(!removalPoint.inBounds()) {
                // since the height of removalPoint's child has decreased, if removalPoint is out of bounds
                // it implies that the height of the other child must be greater than this child.
                // so, rebalance will traverse down the other child and perform rotations as necessary

                rebalance(removalPoint);
            }

            // traverse up the tree
            removalRelation = parentRelation;
            removalPoint = parent;
        }
    }

    public void remove(Node node) {
        // idea: if it's a leaf node or a node with 1 child, basic deletion
        // otherwise, find the next node in the series and SWAP the two
        // then we delete the (now leaf) node
        // then, using the removal point and removal relation we climb up the tree and rebalance

        Node removalPoint; // represents the first node to have balance change as result of a removal
        ParentRelation removalRelation; // represents the relation from the node removed to the removal point

        if(node.isLeaf()) {
            ParentRelation relation = ParentRelation.getRelation(node);
            if(relation == ParentRelation.HEAD) {
                // this is a tree with one node. Delete it.
                // no need for rotations or AVL balancing
                setHead(null);
                return;
            } else {
                // parent's balance changes
                removalPoint = node.getParent();
                removalRelation = relation;

                // replace this leaf with null in the tree
                relation.replaceNode(node, null);
                node.setParent(null); // remove node from the tree
            }
        } else if(!node.isFull()) {
            // this is not a leaf but one of the children are null, we can swap the non-empty child
            // with this one
            // easy case: left or right node on this node is null
            Node replacementNode = node.getLeft() == null ? node.getRight() : node.getLeft();
            Node parent = node.getParent();

            // parent's balance changes. replacementNode's balance stays the same as the tree is unchanged
            removalPoint = parent;
            removalRelation = ParentRelation.getRelation(node);

            removalRelation.replaceNode(node, replacementNode);

            // remove this node from the tree
            node.setParent(null);
            node.setRight(null);
            node.setLeft(null);
            node.setBalanceFactor(0);

            // new parent is this node's parent
            replacementNode.setParent(parent);

            if(removalRelation == ParentRelation.HEAD) {
                // nothing happens, as the tree is now more balanced than before
                // replacementNode will have the same balance as the tree is unchanged
                setHead(replacementNode);
                return;
            }
        } else {
            // find the replacementNode that will be put in the place of node. replacementNode will be a leaf
            Node replacementNode = (Node)node.getInorderSuccessorRandom();

            // parent, left, and right in the middle of the tree
            Node parent = node.getParent();
            Node left = node.getLeft();
            Node right = node.getRight();

            removalPoint = replacementNode.getParent();
            removalRelation = ParentRelation.getRelation(replacementNode);

            // spot where replacementNode was is removed from the tree, then grafted where node is
            removalRelation.replaceNode(replacementNode, null);

            // this node is replaced with our replacement node
            ParentRelation.getRelation(node).replaceNode(node, replacementNode);
            replacementNode.setLeft(left);
            replacementNode.setRight(right);
            replacementNode.setParent(parent);
            replacementNode.setBalanceFactor(node.getBalanceFactor());
            left.setParent(replacementNode);
            right.setParent(replacementNode);

            // node's edges are also from the tree
            node.setParent(null);
            node.setLeft(null);
            node.setRight(null);
            node.setBalanceFactor(0);
        }

        nodeRemoved(removalPoint, removalRelation);
    }
}
