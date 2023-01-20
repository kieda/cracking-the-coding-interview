package common.tree;

import common.HasMin;

public class MinHeap<X extends Comparable<X>> implements HasMin<X> {
    public static class Node<E extends Comparable<E>> {
        private Node<E> left;
        private Node<E> right;
        private E elem;
    }
    @Override
    public X getMin() {
        return null;
    }
}
