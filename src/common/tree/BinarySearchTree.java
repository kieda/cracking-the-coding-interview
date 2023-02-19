package common.tree;

import common.HasMin;
import common.Sort;
import common.lists.EmptyCollectionException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class BinarySearchTree<X extends Comparable<X>> extends BinaryTree<X> implements HasMin<X> {
    @Override
    public X getMin() {
        if(isEmpty())
            throw new EmptyCollectionException();
        Node least = getHead();
        while(least.getParent() != null) {
            least = least.getParent();
        }
        return least.getElem();
    }

    public class Node extends BinaryTree<X>.Node{
        public Node(X elem) {
            super(elem);
        }

        @Override
        public void setLeft(BinaryTree<X>.Node left) {
            if(!(left instanceof BinarySearchTree.Node)) {
                throw new IllegalArgumentException("left child " + left + " is not of type " + BinarySearchTree.Node.class.getSimpleName());
            }
            super.setLeft(left);
        }

        @Override
        public void setRight(BinaryTree<X>.Node right) {
            if(!(right instanceof BinarySearchTree.Node)) {
                throw new IllegalArgumentException("right child " + right + " is not of type " + BinarySearchTree.Node.class.getSimpleName());
            }
            super.setRight(right);
        }

        @Override
        public void setParent(BinaryTree<X>.Node parent) {
            if(!(parent instanceof BinarySearchTree.Node)) {
                throw new IllegalArgumentException("parent " + parent + " is not of type " + BinarySearchTree.Node.class.getSimpleName());
            }
            super.setParent(parent);
        }

        @Override
        public BinarySearchTree<X>.Node getLeft() {
            return (BinarySearchTree<X>.Node)super.getLeft();
        }

        @Override
        public BinarySearchTree<X>.Node getRight() {
            return (BinarySearchTree<X>.Node)super.getRight();
        }

        @Override
        public BinarySearchTree<X>.Node getParent() {
            return (BinarySearchTree<X>.Node)super.getParent();
        }

        public Node getInorderSuccessorRandom() {
            return getInorderSuccessor(Math.random() <= 0.5);
        }
        /**
         * @return the in-order successor of this node, traversing the left or right path based on the argument
         * We traverse till we reach the closest node that is also a leaf.
         * If this Node is a leaf, we will return this node
         */
        public Node getInorderSuccessor(boolean right) {
            // find the replacementNode that will be put in the place of node. replacementNode will be a leaf
            SearchFlags searchForNode = right ? SearchFlags.LESS_OR_EQUAL : SearchFlags.GREATER_OR_EQUAL;
            return (right ? getRight() : getLeft()).lookup(getElem(), searchForNode);
        }

        /**
         * looks up a value in subtrees of this node, including this node
         */
        public Node lookup(X data) {
            return lookup(data, SearchFlags.EQUAL_TO);
        }

        /**
         * looks up a value in subtrees and this node, according to SearchFlags
         */
        public Node lookup(X data, SearchFlags flags) {
            Consumer<Node> doNothing = n -> {};
            return lookup(data, doNothing, doNothing, flags);
        }

        /**
         * looks up a value in subtrees and this node, according to SearchFlags and with functions to visit the nodes
         * when traversing down and back up
         */
        public Node lookup(X data, Consumer<Node> visitDown, Consumer<Node> visitUp, SearchFlags flags) {
            Node traverser = this;

            // find the best node according to the search flags.
            // We attempt to find the node that's furthest down the tree in the case of duplicates

            boolean foundExact = false;
            Node foundNode = null;
            while(traverser != null) {
                int comparison = Sort.compare(data, traverser.getElem());
                if(comparison == 0 && flags.equalTo()) {
                    // we found the exact node
                    foundNode = traverser;
                    foundExact = true;
                } else if(foundExact && comparison != 0){
                    // we already found an exact match but we don't have a duplicate element.
                    // exit early
                    break;
                } else if(comparison > 0) {
                    if(flags.lessThan())
                        foundNode = traverser;
                    traverser = traverser.getRight();
                } else {
                    if(flags.greaterThan())
                        foundNode = traverser;
                    traverser = traverser.getLeft();
                }
            }

            if(foundNode == null)
                return null;

            // now, we traverse down the tree, visiting each node, then go back up again
            traverser = this;
            while(traverser != null) {
                // traverse down to we get to our foundNode
                visitDown.accept(traverser);
                int comparison = Sort.compare(foundNode.getElem(), traverser.getElem());
                if(traverser == foundNode) {
                    // traverse back up again
                    Node traverseUp = traverser;
                    while(traverseUp != null) {
                        visitUp.accept(traverseUp);
                        traverseUp = traverseUp.getParent();
                    }
                    return traverser;
                } else if(comparison > 0) {
                    traverser = traverser.getRight();
                } else {
                    // if we have an implementation that allows duplicates, duplicates will be put to the left
                    // thus we traverse left if comparison == 0 but it isn't the node we found
                    traverser = traverser.getLeft();
                }
            }

            // should never occur unless we break order invariants while traversing down
            throw new MissingChildException("descendant", this, foundNode);
        }
    }

    /**
     * Specify a searchflag when looking up an element in the tree. Will return the closest node according to
     * the comparison flag without violating it.
     */
    public enum SearchFlags{
        EQUAL_TO(1),
        GREATER_THAN(1<<1),
        LESS_THAN(1<<2),
        GREATER_OR_EQUAL((1<<1) | 1),
        LESS_OR_EQUAL((1<<2) | 1),
        // this will traverse the binary search tree down till we find the Node that we can insert the given element
        // and preserve the BST invariant. Node will have left or right free for insertion.
        INSERTION_POINT( (1<<2) | (1<<1));
        private final int mask;
        private final static Map<Integer, SearchFlags> lookupMap;
        static {
            // populate map from mask -> SearchFlags for fast lookup
            SearchFlags[] values = values();
            Map.Entry<Integer, SearchFlags>[] enumEntries = new Map.Entry[values.length];
            for(int i = 0; i < values.length; i++) {
                enumEntries[i] = Map.entry(values[i].getMask(), values[i]);
            }
            lookupMap = Map.ofEntries(enumEntries);
        }
        SearchFlags(int mask) {
            this.mask = mask;
        }
        public SearchFlags and(SearchFlags that) {
            int newMask = that.getMask() | getMask();
            if(lookupMap.containsKey(newMask)) {
                return lookupMap.get(newMask);
            }
            throw new IllegalArgumentException("Invalid flag combo " + this + " and " + that);
        }
        public boolean equalTo() {
            return (getMask() & EQUAL_TO.getMask()) != 0;
        }
        public boolean greaterThan() {
            return (getMask() & GREATER_THAN.getMask()) != 0;
        }
        public boolean lessThan() {
            return (getMask() & LESS_THAN.getMask()) != 0;
        }
        public int getMask() {
            return mask;
        }
    }

    @Override
    public void setHead(BinaryTree<X>.Node head) {
        if(!(head instanceof BinarySearchTree.Node)) {
            throw new IllegalArgumentException("head " + head + " is not of type " + BinarySearchTree.Node.class.getSimpleName());
        }
        super.setHead(head);
    }

    @Override
    public BinarySearchTree<X>.Node getHead() {
        return (BinarySearchTree<X>.Node)super.getHead();
    }

    public Node lookup(X data) {
        final Node head = getHead();
        return head == null ? null : head.lookup(data);
    }
    public Node lookup(X data, SearchFlags flags) {
        final Node head = getHead();
        return head == null ? null : head.lookup(data, flags);
    }

    /**
     * Finds a node in this BST. We add visitor functions that are called while we are traversing down or up the tree,
     * these may modify the tree structure themselves but should not break the BST ordering invariant. This is useful
     * for cache-based BSTs, like a splay tree
     *
     * If there are no valid nodes that match our search criteria, no nodes are visited.
     *
     * @param data the item (or closest item) we are searching for
     * @param visitDown visitor function while we're traversing down the tree (top-down)
     * @param visitUp visitor function while we're traversing back up the tree (bottom-up)
     * @param flags changes the criteria for the node that we're searching for. For example,
     * @return
     */
    public Node lookup(X data, Consumer<Node> visitDown, Consumer<Node> visitUp, SearchFlags flags) {
        final Node head = getHead();
        return head == null ? null : head.lookup(data, visitDown, visitUp, flags);
    }

    @Override
    public BinarySearchTree<X>.Node makeNode(X elem) {
        return new BinarySearchTree<X>.Node(elem);
    }
}
