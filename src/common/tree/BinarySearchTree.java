package common.tree;

import common.Sort;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class BinarySearchTree<X extends Comparable<X>> extends BinaryTree<X>{
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
        LESS_OR_EQUAL((1<<2) | 1);
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

    public Node lookup(X data) {
        return lookup(data, SearchFlags.EQUAL_TO);
    }
    public Node lookup(X data, SearchFlags flags) {
        Consumer<Node> doNothing = n -> {};
        return lookup(data, doNothing, doNothing, flags);
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
        Node traverser = head;

        // find the best node according to the search flags
        Node foundNode = null;
        while(traverser != null) {
            int comparison = Sort.compare(data, traverser.getElem());
            if(comparison == 0 && flags.equalTo()) {
                // we found the exact node
                foundNode = traverser;
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
        traverser = head;
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
        throw new MissingChildException("descendant", head, foundNode);
    }

    @Override
    public BinarySearchTree<X>.Node makeNode(X elem) {
        return new BinarySearchTree<X>.Node(elem);
    }
}
