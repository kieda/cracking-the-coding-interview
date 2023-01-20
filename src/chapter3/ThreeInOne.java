package chapter3;

import common.lists.*;
import common.tuple.Tuple2;

/**
 * Use a single array to implement three stacks
 */
public class ThreeInOne {
    /**
     * This solution does NOT group consecutive items in the stack nearby in the array
     * Instead, we keep track of the "free" elements that we can insert to
     *
     * This actually allows us to insert and remove from any stack in O(1) time, and there is no need for any sort
     * of refactoring or complexity
     */
    public static class ArrayBackedMultiStack<X> {
        private final int totalCapacity;
        private final int numStacks;

        // keeps track of empty slots that we can fill. When a slot in the buffer opens up, we add it to
        // this queue. When we want to allocate another item to a stack, we take it from this queue.
        private SimpleQueue<Integer> freeSlots;

        // these stacks simply keep track of the head element in the buffer, or null if the stack is empty
        private SimpleStack<X>[] stacks;

        // (data, next index for traversal)
        private Tuple2<X, Integer>[] buffer;
        private int curentItemCount = 0;

        public ArrayBackedMultiStack(int totalCapacity, int numStacks) {
            this.curentItemCount = 0;
            this.numStacks = numStacks;
            this.totalCapacity = totalCapacity;
            this.buffer = new Tuple2[totalCapacity];
            this.stacks = new SimpleStack[numStacks];
            this.freeSlots = new DoubleLinkedList<>();

            // upon creation, all indices are free
            for(int idx = 0; idx < totalCapacity; idx++) {
                freeSlots.addFirst(idx);
            }
            for(int stackNum = 0; stackNum < numStacks; stackNum++) {
                stacks[stackNum] = new ArrayBackedStack();
            }
        }

        public boolean isEmpty() {
            return curentItemCount == 0;
        }
        public boolean isFull() {
            return curentItemCount == totalCapacity;
        }

        public SimpleStack<X> getStack(int stackNum) {
            return stacks[stackNum];
        }

        private class ArrayBackedStack implements SimpleStack<X> {
            // represents the head of the stack, or -1 if the stack is empty
            private int head = -1;

            public ArrayBackedStack() {}

            @Override
            public void addFirst(X elem) {
                if(ArrayBackedMultiStack.this.isFull())
                    throw new FullCollectionException();
                int nextFreeSlot = freeSlots.getLast();
                freeSlots.removeLast();
                // entry contains the elem we want to store, as well as a pointer to the previous element in the stack (current head)
                Tuple2<X, Integer> newEntry = Tuple2.make(elem, head);
                buffer[nextFreeSlot] = newEntry;
                // current head is the freeSlot we just took
                head = nextFreeSlot;

                curentItemCount++;
            }

            @Override
            public X getFirst() {
                if(ArrayBackedStack.this.isEmpty())
                    throw new EmptyCollectionException();

                return buffer[head].getFirst();
            }

            @Override
            public void removeFirst() {
                if(ArrayBackedStack.this.isEmpty())
                    throw new EmptyCollectionException();

                Tuple2<X, Integer> headEntry = buffer[head];
                buffer[head] = null; // remove this entry from the stack
                freeSlots.addFirst(head); // the head index is now marked as free to use
                head = headEntry.getSecond(); // head is moved back to previous position

                curentItemCount--;
            }

            @Override
            public boolean isEmpty() {
                return head < 0;
            }
        }
    }
}
