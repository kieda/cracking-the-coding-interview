package common.tree;

import common.function.Consumer3;
import common.function.Function3;
import common.function.Function4;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * a basic binary tree without restrictions. Has some useful functions for rearranging the BT.
 * Implementing classes may impose things like balancing or order
 * @param <E>
 */
public class BinaryTree<E> {
    private Node head;

    protected static <X> void  throwMissingChild(BinaryTree<X>.Node parent, BinaryTree<X>.Node child) {
        throw new MissingChildException("child", parent, child);
    }

    enum ParentalRelation{
        HEAD,
        LEFT,
        RIGHT,
        NONE;

        public <E> void replaceNode(BinaryTree<E>.Node node, BinaryTree<E>.Node replacement) {
            BinaryTree<E>.Node parent = node.getParent();
            switch(this) {
                case HEAD:
                    break;
                case LEFT:
                    parent.setLeft(replacement);
                    break;
                case RIGHT:
                    parent.setRight(replacement);
                    break;
                case NONE:
                    throwMissingChild(parent, node);
            }
        }

        public static <E> ParentalRelation getRelation(BinaryTree<E>.Node node) {
            BinaryTree<E>.Node parent = node.getParent();
            if(parent == null) {
                return HEAD;
            } else if(parent.getLeft() == node) {
                return LEFT;
            } else if(parent.getRight() == node) {
                return RIGHT;
            } else {
                return NONE;
            }
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
            ParentalRelation.getRelation(parent)
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
            ParentalRelation relation = ParentalRelation.getRelation(this);
            // sets the parent's child to our left node
            relation.replaceNode(this, right);

            if(relation == ParentalRelation.HEAD) {
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
            ParentalRelation relation = ParentalRelation.getRelation(this);
            relation.replaceNode(this, left);

            if(relation == ParentalRelation.HEAD) {
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
            ParentalRelation.getRelation(this).replaceNode(this, null);

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
                ParentalRelation relation = ParentalRelation.getRelation(this);
                if(relation == ParentalRelation.HEAD) {
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
                switch(ParentalRelation.getRelation(this)) {
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
                ParentalRelation.getRelation(this).replaceNode(this, null);

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
}
