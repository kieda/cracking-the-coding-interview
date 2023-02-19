package common.tree;

import chapter4.Successor;
import common.function.Function3;
import common.tuple.Tuple2;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * a basic binary tree without restrictions. Has some useful functions for rearranging the BT.
 * Implementing classes may impose things like balancing or order
 * @param <E>
 */
// todo - perhaps make Node type an argument of the BinaryTree itself, then overriding classes can use their own Node implementations
//        this would remove the need to have overrides just to change or check the type of the node (in a traversal, lookup, etc)
public class BinaryTree<E> {
    private Node head;

    protected static <X> void  throwMissingChild(BinaryTree<X>.Node parent, BinaryTree<X>.Node child) {
        throw new MissingChildException("child", parent, child);
    }

    /**
     * defines the relationship between this node's parent and its grandparent.
     * "ZIG" means that the child is to the left, and "ZAG" means the child is to the right
     * If there is no parent, the relationship is HEAD
     * If there is a problem (e.g. parent doesn't have this node as a child), then NONE is returned
     */
    enum GrandparentRelation{
        ZIG(ParentRelation.LEFT, ParentRelation.HEAD),
        ZAG(ParentRelation.RIGHT, ParentRelation.HEAD),
        ZIGZAG(ParentRelation.LEFT, ParentRelation.RIGHT),
        ZAGZIG(ParentRelation.RIGHT, ParentRelation.LEFT),
        ZIGZIG(ParentRelation.LEFT, ParentRelation.LEFT),
        ZAGZAG(ParentRelation.RIGHT, ParentRelation.RIGHT),
        HEAD(ParentRelation.HEAD, ParentRelation.NONE), // if this node is the head
        NONE(ParentRelation.NONE, ParentRelation.NONE); // if there is a problem in our relationship

        private final static Map<Tuple2<ParentRelation, ParentRelation>, BinaryTree.GrandparentRelation> lookupMap;
        static {
            // populate map from mask -> SearchFlags for fast lookup
            BinaryTree.GrandparentRelation[] values = values();
            Map.Entry<Tuple2<ParentRelation, ParentRelation>, BinaryTree.GrandparentRelation>[] enumEntries = new Map.Entry[values.length];
            for(int i = 0; i < values.length; i++) {
                enumEntries[i] = Map.entry(Tuple2.make(values[i].parent, values[i].grandparent), values[i]);
            }
            lookupMap = Map.ofEntries(enumEntries);
        }

        private final ParentRelation parent;
        private final ParentRelation grandparent;
        GrandparentRelation(ParentRelation parent, ParentRelation grandparent) {
            this.parent = parent;
            this.grandparent = grandparent;
        }

        public static <E> GrandparentRelation getRelation(BinaryTree<E>.Node node) {
            ParentRelation parentRelation = ParentRelation.getRelation(node);
            if(parentRelation == ParentRelation.HEAD)
                return HEAD;
            if(parentRelation == ParentRelation.NONE)
                return NONE;
            ParentRelation grandparentRelation = ParentRelation.getRelation(node.getParent());
            return lookupMap.getOrDefault(Tuple2.make(parentRelation, grandparentRelation), GrandparentRelation.NONE);
        }
    }

    public class Node {
        private Node parent;
        private Node left;
        private Node right;
        private E elem;

        public Node(E elem) {
            this.elem = elem;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public Node getParent() {
            return parent;
        }

        public Node getLeft() {
            return left;
        }

        public Node getRight() {
            return right;
        }

        public void setLeft(Node left) {
            this.left = left;
            left.parent = this;
        }

        public void setRight(Node right) {
            this.right = right;
            right.parent = this;
        }

        public void setElem(E elem) {
            this.elem = elem;
        }

        public E getElem() {
            return elem;
        }

        public boolean isHead() {
            return getParent() == null;
        }

        /**
         * Swaps positions in the tree by swapping the nodes
         */
        public void swapUp() {
            if(isHead()) {
                return;
            }
            Node parent = getParent();
            Node grandParent = parent.getParent();

            Node parentLeft = parent.getLeft();
            Node parentRight = parent.getRight();

            Node thisLeft = getLeft();
            Node thisRight = getRight();

            // replaces our parent with this node in the tree
            ParentRelation.getRelation(parent)
                .replaceNode(parent, this);

            setParent(grandParent);
            if(parentRight == this) {
                setLeft(parentLeft);
                setRight(parent);
            } else if(parentLeft == this){
                setLeft(parent);
                setRight(parentRight);
            } else {
                throwMissingChild(parent, this);
            }
            parent.setLeft(thisLeft);
            parent.setRight(thisRight);
            parent.setParent(this);

            if(isHead())
                setHead(this);
        }

        /**
         * Rearranges nodes by a left shift. Preserves BST properties.
         *
         * We rotate 10 to the left:
         *
         *        30
         *       /
         *     (10)
         *    /    \
         *   1       20
         *  / \    /   \
         * 0   2  12    22
         *
         * ========================>
         *
         *            30
         *           /
         *         20
         *        /  \
         *     (10)   22
         *    /   \
         *   1    12
         *  / \
         * 0   2
         */
        public void leftRotate() {
            Node parent = getParent();
            Node right = getRight();

            if(right == null) {
                throw new CannotRotateException("Cannot rotate to the left, as right child is null!", this);
            }
            Node grandChildLeft = right.getLeft();
            ParentRelation relation = ParentRelation.getRelation(this);
            // sets the parent's child to our left node
            relation.replaceNode(this, right);

            if(relation == ParentRelation.HEAD) {
                // set new head to the right, which is our new parent
                setHead(right);
            }

            // right is our new parent
            right.setLeft(this);

            // sets the right's parent to our parent, as it will be positioned above us
            right.setParent(parent);

            // reposition the grandchild to be our child
            setRight(grandChildLeft);
            grandChildLeft.setParent(this);

            setParent(right); // our new parent is our right node
        }


        /**
         * Rearranges nodes by a right shift. Preserves BST properties.
         *
         * We rotate 20 to the right:
         *           30
         *          /
         *        (20)
         *       /  \
         *     10   22
         *    /  \
         *   1   12
         *  / \
         * 0   2
         *
         * ========================>
         *
         *        30
         *       /
         *      10
         *    /    \
         *   1     (20)
         *  / \    /   \
         * 0   2  12    22
         */
        public void rightRotate() {
            Node parent = getParent();
            Node left = getLeft();

            if(left == null) {
                throw new CannotRotateException("Cannot rotate to the right, as left child is null!", this);
            }
            Node grandChildRight = left.getRight();
            // sets the parent's child to our left node
            ParentRelation relation = ParentRelation.getRelation(this);
            relation.replaceNode(this, left);

            if(relation == ParentRelation.HEAD) {
                // set head to the left node, which is our new parent
                setHead(left);
            }
            // left is our new parent
            left.setRight(this);

            // sets the left's parent to our parent, as it will be positioned above us
            left.setParent(parent);

            // reposition the grandchild to be our child
            setLeft(grandChildRight);
            grandChildRight.setParent(this);

            setParent(left); // our new parent is our left node
        }

        public void rotate(ParentRelation direction) {
            switch (direction) {
                case LEFT:
                    leftRotate();
                    break;
                case RIGHT:
                    rightRotate();
                    break;
                default:
                    throw new IllegalArgumentException("cannot rotate to direction " + direction);
            }
        }

        /**
         * performs a rotation on the node X that has the form
         *        Z
         *       /
         *      X
         *       \
         *        Y
         * will have the final form
         *        Y
         *      /  \
         *     X    Z
         */
        public void leftRightRotate() {
            if(ParentRelation.getRelation(this) != ParentRelation.LEFT) {
                throw new CannotRotateException("left right rotation needs to be situated to left of parent", this);
            }
            Node parent = getParent();
            leftRotate();
            parent.rightRotate();
        }

        /**
         * performs a rotation on the node X that has the form
         *        Z
         *         \
         *          X
         *         /
         *        Y
         * will have the final form
         *        Y
         *      /  \
         *     Z    X
         */
        public void rightLeftRotate() {
            if(ParentRelation.getRelation(this) != ParentRelation.RIGHT) {
                throw new CannotRotateException("right left rotation needs to be situated to right of parent", this);
            }
            Node parent = getParent();
            rightRotate();
            parent.leftRotate();
        }

        /**
         * Simple, stupid version of removal. We provide a function to place the left and right branches, which is called
         * every time.
         * @param placement
         *      args: (null, null, null) implies this was the only element in the tree (and is therefore the head)
         *      args: (parent, null, null) implies this node is a leaf
         *      args: (null, left, right) implies this node is at the head of the tree
         *      args: (parent, left, right) implies this node is in the middle of the tree
         * @return the element removed
         */
        public E remove(Function3<Node, Node, Node, Node> placement) {
            E thisElem = getElem();

            Node parent = getParent();
            Node left = getLeft();
            Node right = getRight();
            // cull this node from the tree
            ParentRelation.getRelation(this).replaceNode(this, null);

            // left and right nodes are free-floating
            setLeft(null);
            setRight(null);
            // our left and right nodes are now free
            left.setParent(null);
            right.setParent(null);

            // place left and right nodes
            Node newHead = placement.apply(parent, left, right);
            setHead(newHead);

            return thisElem;
        }
        /**
         * removes this node from the tree. Performs some basic operations to move nodes when possible
         * We provide user functions to rebalance the tree or update the nodes
         * @param placement function: (Node parent, Node right, Node left) -> Node newHead
         *                  finds a location to put the left and right children of this tree
         *                  returns the new head of the entire tree. We do this in case the tree needs to be rebalanced as a result of the removal operation
         *                  If we are removing the head of this tree, the parent argument will be null
         *
         * @param onDeletion function: (Node nodeDeleted, Node oldParent) -> Node newHead
         *                 We use this as a callback when a node is deleted
         *                 if oldParent is null it implies nodeDeleted was the head of the tree
         *
         * @param onNodeMoved function: (Node nodeMoved, Node oldParent, Node newParent) -> Node newHead
         *                 This is called when nodes are moved around as result of a deletion. For example, if we have a nearby null child we can simply
         *                 shift the tree around. However, the user might want to rebalance the tree or update data on the node when it moves in depth.
         *                 For this reason we also return newHead which is used when the tree might be rebalanced
         *
         *                 args: (node, oldParent, newParent == null) implies node is the new head
         *                 args: (node, oldParent, newParent != null) implies node has moved to a new parent
         * @return the element at this node
         */
        public E remove(Function3<Node, Node, Node, Node> placement,
                        BiFunction<Node, Node, Node> onDeletion, // (node, oldParent) -> newHead
                        Function3<Node, Node, Node, Node> onNodeMoved) { // (node, oldParent, newParent) -> newHead
            E thisElem = getElem();

            if(getLeft() == null && getRight() == null) {
                // easy case: this is a leaf node.
                ParentRelation relation = ParentRelation.getRelation(this);
                if(relation == ParentRelation.HEAD) {
                    // this is a tree with one node. Delete it.
                    setHead(null);
                    setHead(onDeletion.apply(this, null));
                } else {
                    Node parent = getParent();
                    // otherwise replace this node with null in the tree
                    relation.replaceNode(this, null);
                    setHead(onDeletion.apply(this, parent));
                }
            } else if(getLeft() == null || getRight() == null) {
                // easy case: left or right node on this node is null
                Node replacementNode = getLeft() == null ? getRight() : getLeft();
                Node parent = getParent();
                switch(ParentRelation.getRelation(this)) {
                    case HEAD:
                        // replace head with available tree
                        setHead(replacementNode);
                        break;
                    case RIGHT:
                        // replace the parent's right node with the available tree
                        parent.setRight(replacementNode);
                        break;
                    case LEFT:
                        // mirrored above
                        parent.setLeft(replacementNode);
                        break;
                    case NONE:
                        throwMissingChild(parent, this);
                        break;
                }
                // remove this node from the tree
                setParent(null);
                setRight(null);
                setLeft(null);
                // new parent is this node's parent
                replacementNode.setParent(parent);
                setHead(onDeletion.apply(this, parent)); // this node is deleted
                setHead(onNodeMoved.apply( replacementNode, this, parent)); // replacementNode's new parent is this parent
            } else if(getParent() != null && (getParent().getLeft() == null || getParent().getRight() != null)) {
                // easy case: one of our parent's children is null.
                // then we replace our parent's children with our own children
                Node parent = getParent();
                Node left = getLeft();
                Node right = getRight();
                parent.setRight(right);
                parent.setLeft(left);
                setParent(null);
                setLeft(null);
                setRight(null);
                setHead(onDeletion.apply(this, parent)); // this node is deleted
                setHead(onNodeMoved.apply( left, this, parent)); // left's new parent is this parent
                setHead(onNodeMoved.apply( right, this, parent)); // right's new parent is this parent
            } else{
                Node parent = getParent();
                Node left = getLeft();
                Node right = getRight();
                // cull this node from the tree
                ParentRelation.getRelation(this).replaceNode(this, null);

                // left and right nodes are free-floating
                setLeft(null);
                setRight(null);
                // our left and right nodes are now free
                left.setParent(null);
                right.setParent(null);

                setHead(onDeletion.apply(this, parent)); // this node is deleted

                // place left and right nodes
                Node newHead = placement.apply(parent, left, right);
                setHead(newHead);
            }

            return thisElem;
        }

        @Override
        public String toString() {
            return "Node<" + getElem() + ">";
        }

        public boolean isLeaf() {
            return getLeft() == null && getRight() == null;
        }

        public boolean isFull() {
            return getLeft() != null && getRight() != null;
        }
    }

    public Node makeNode(E elem) {
        return new Node(elem);
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public Node getHead() {
        return head;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public <A> A traverseElems(A initial, BiFunction<A, E, A> accumulator) {
        return traverseElems(initial, accumulator, (a, elem) -> false);
    }

    public <A> A traverseElems(A initial, BiFunction<A, E, A> accumulator, BiPredicate<A, E> stop) {
        return traverseNodes(initial, (a, node) -> accumulator.apply(a, node.getElem()), (a, node ) -> stop.test(a, node.getElem()));
    }

    /**
     * traverses through the entire tree, without stopping
     */
    public <A> A traverseNodes(A initial, BiFunction<A, Node, A> accumulator) {
        return traverseNodes(initial, accumulator, (a, elem) -> false);
    }

    /**
     * Utilize morris traversal, in-order.
     * If we ever reach a node where the predicate "stop" returns true, we stop the accumulator
     * and will return the result. Note that stop here does not immediately exit the traversal, since we may have
     * nodes that need to be restored to their previous configuration
     */
    public <A> A traverseNodes(A initial, BiFunction<A, Node, A> accumulator, BiPredicate<A, Node> stop) {
        int edgesAdded = 0;
        boolean stopVisiting = false;
        Node current = getHead();
        while(current != null) {
            if(current.getLeft() == null) {
                if(!stopVisiting) {
                    // visit current node
                    initial = accumulator.apply(initial, current);
                    // test for stopping condition
                    stopVisiting = stop.test(initial, current);

                    // stop condition: we reset all our edges and we've reached a stopping point
                    if(edgesAdded == 0 && stopVisiting)
                        return initial;
                }
                current = current.getRight();
            } else {
                // make current the the right child of the rightmost node in current's left subtree
                Node previous = current.getLeft();
                while(previous.getRight() != null && previous.getRight() != current) {
                    previous = previous.getRight();
                }

                if(previous.getRight() == null) {
                    edgesAdded++;
                    previous.setRight(current);
                    current = current.getLeft();
                } else {
                    edgesAdded--;
                    previous.setRight(null);

                    // stop condition: we reset all our edges and we've reached a stopping point
                    if(edgesAdded == 0 && stopVisiting)
                        return initial;

                    // visit the current node
                    if(!stopVisiting) {
                        initial = accumulator.apply(initial, current);
                        stopVisiting = stop.test(initial, current);
                    }

                    current = current.getRight();
                }
            }
        }
        return initial;
    }


    public <A, N extends BinaryTree<E>.Node> A traverse(A initial, BinaryTreeTraverser<A, E, N> traverser) {
        return traverse(initial, (N) getHead(), traverser);
    }

    public <A, N extends BinaryTree<E>.Node> A traverse(A initial, N start, BinaryTreeTraverser<A, E, N> traverser) {
        return traverse(initial, start, traverser::visitNode, traverser::visitDown, traverser::visitUp, traverser::stop);
    }
    public <A, N extends BinaryTree<E>.Node> A traverse(A initial, N start, Function3<A, N, ParentRelation, A> visitNode,
            Function3<A, N, ParentRelation, A> visitDown, Function3<A, N, ParentRelation, A> visitUp, BiPredicate<A, N> stop) {
        N previous = null;
        N node = start;

        // get leftmost item
        N firstElem = start;
        if(firstElem != null)
            initial = visitDown.apply(initial, firstElem, ParentRelation.HEAD);
        while(firstElem != null && firstElem.getLeft() != null) {
            firstElem = (N) firstElem.getLeft();
            initial = visitDown.apply(initial, firstElem, ParentRelation.LEFT);
        }

        // stop and return if we run out of nodes, if we reach our stop condition
        // note that the condition may traverse into negative depth (above our initial node)
        // if this is not desired, ensure this is in the stop condition
        while(node != null && !stop.test(initial, node)) {
            // determine direction from previous visited node to this node
            ParentRelation direction;
            if(previous == null)
                direction = ParentRelation.HEAD;
            else if(node.getRight() == previous || previous.getRight() == node)
                direction = ParentRelation.RIGHT;
            else if(node.getLeft() == previous || previous.getLeft() == node)
                direction = ParentRelation.LEFT;
            else
                direction = ParentRelation.NONE;

            initial = visitNode.apply(initial, node, direction);

            // get the next in-order node in the traversal
            N nextNode = null;
            {
                N last = null; // keep previous node so we don't traverse back down the tree
                while (node != null) {
                    if (node.getRight() != null && node.getRight() != last) {
                        N result = (N) node.getRight();
                        initial = visitDown.apply(initial, result, ParentRelation.RIGHT);
                        while (result.getLeft() != null) {
                            result = (N) result.getLeft();
                            initial = visitDown.apply(initial, result, ParentRelation.LEFT);
                        }
                        nextNode = result;
                        break;
                    } else if (ParentRelation.getRelation(node) == ParentRelation.RIGHT) {
                        last = node;
                        // if we're to the right of our parent we continue traversing up
                        nextNode = (N) node.getParent();
                        initial = visitUp.apply(initial, nextNode, ParentRelation.RIGHT);
                    } else {
                        // if the child is on the left hand side, parent will be next element
                        nextNode = (N) node.getParent();
                        initial = visitUp.apply(initial, nextNode, ParentRelation.LEFT);
                        break;
                    }
                }
            }
            previous = node;
            node = nextNode;
        }
        return initial;
    }
}
