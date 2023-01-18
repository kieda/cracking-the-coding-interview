package chapter3;

import common.EmptyStackException;
import common.SimpleQueue;
import common.SimpleStack;
import common.SingleLinkedList;

/**
 * Create a queue implementation from two stacks
 */
public class QueueViaStacks {

    // basic idea: have stack1 fill up till we request one of the last elements.
    // then, we dump the contents from stack1 into stack2, which will be in reverse order such that the oldest is on top
    // we don't dump again until stack2 is empty
    public static class StackQueue<X> implements SimpleQueue<X> {
        private SimpleStack<X> stack1 = new SingleLinkedList<>();
        private SimpleStack<X> stack2 = new SingleLinkedList<>();

        @Override
        public void addFirst(X elem) {
            stack1.addFirst(elem);
        }

        private void dump() {
            while(!stack1.isEmpty()) {
                stack2.addFirst(stack1.getFirst());
                stack1.removeFirst();
            }
        }

        private void dumpIfPossible() {
            if(isEmpty())
                throw new EmptyStackException();
            if(stack2.isEmpty())
                dump();
        }

        @Override
        public X getLast() {
            dumpIfPossible();
            return stack2.getFirst();
        }

        @Override
        public void removeLast() {
            dumpIfPossible();
            stack2.removeFirst();
        }

        @Override
        public boolean isEmpty() {
            return stack1.isEmpty() && stack2.isEmpty();
        }
    }
}
