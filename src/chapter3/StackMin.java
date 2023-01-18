package chapter3;

import common.EmptyStackException;
import common.SingleLinkedList;
import common.Sort;

/**
 * How would you design a stack which, in addition to push and pop, has a function min
 * which returns the minimum element? Push, pop and min should all operate in 0(1) time
 */
public class StackMin {

    public interface HasMin<X extends Comparable<X>>{
        public X getMin();
    }
    /**
     * this implementation uses an internal stack to keep track of the minimum
     */
    public static class StackMinTwoStacks<X extends Comparable<X>> extends SingleLinkedList<X> implements HasMin<X> {
        private SingleLinkedList<X> minStack = new SingleLinkedList<>();
        @Override
        public void addFirst(X item) {
            if(minStack.isEmpty() || Sort.compare(item, minStack.getFirst()) <= 0) {
                // if the stack is currently empty or if the new item being inserted is less than the top item,
                // add it to the min stack
                minStack.addFirst(item);
            }
            super.addFirst(item);
        }

        @Override
        public X getMin() {
            if(minStack.isEmpty())
                throw new EmptyStackException();
            return minStack.getFirst();
        }

        @Override
        public void removeFirst() {
            if(isEmpty())
                throw new EmptyStackException();
            if(Sort.compare(minStack.getFirst(), getFirst()) == 0) {
                minStack.removeFirst();
            }
            super.removeFirst();
        }
    }

    // use a wrapper on the node class
    public static class StackMinWrapper<X extends Comparable<X>> extends SingleLinkedList<X> implements HasMin<X> {
        static class MinNode<Y extends Comparable<Y>> extends Node<Y>{
            private Y currentMin;
            public MinNode(Y item) {
                super(item);
            }

        }

        @Override
        public void setHead(Node<X> head) {
            super.setHead(head);
        }

        @Override
        public X getMin() {
            return null;
        }
    }
}
