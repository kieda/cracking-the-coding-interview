package common.tree;

import java.util.HashMap;
import java.util.Map;

public class BinarySearchTree<X extends Comparable<X>> extends BinaryTree<X>{
    public class Node extends BinaryTree<X>.Node{
        public Node(X elem) {
            super(elem);
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
    public Node lookup(X data, SearchFlags flag) {
        // todo
        return null;
    }

    @Override
    public BinarySearchTree<X>.Node makeNode(X elem) {
        return new BinarySearchTree<X>.Node(elem);
    }
}
