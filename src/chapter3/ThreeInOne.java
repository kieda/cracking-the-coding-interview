package chapter3;

import common.*;

/**
 * Use a single array to implement three stacks
 */
public class ThreeInOne {
    public static class RingBufferMultiStack<X> {
        private final int totalCapacity;
        private final int numStacks;
        // (stack, freeIndex)
        // we use the LinkedRingBuffer to find the index of the next available stack that has free space if we shift the array
        private Tuple2<SimpleStack<X>, LinkedRingBuffer<Integer>>[] stacks;
        private Object[] buffer;
        private int curentItemCount = 0;

        public RingBufferMultiStack(int totalCapacity, int numStacks) {
            this.curentItemCount = 0;
            this.numStacks = numStacks;
            this.totalCapacity = totalCapacity;
            this.buffer = new Object[totalCapacity];
            this.stacks = new Tuple2[numStacks];
        }

        public boolean isEmpty() {
            return curentItemCount == 0;
        }
        public boolean isFull() {
            return curentItemCount == totalCapacity;
        }

        public SimpleStack<X> getStack(int stackNum) {
            return stacks[stackNum].getFirst();
        }

        private class RingBufferStack<X> implements SimpleStack<X> {
            private int length;
            // the index we're starting at in the buffer
            private int startingIndex;
            // the number of cells we currently occupy in the buffer
            private int stackCapacity;

            private X access(int index) {
                if(index >= stackCapacity) {
                    throw new IndexOutOfBoundsException("index " + index + " is past stackCapacity of " + stackCapacity);
                }
                if(index <= 0) {
                    throw new IndexOutOfBoundsException("cannot access negative index " + index);
                }
                int actualIndex = (startingIndex + index) % totalCapacity;
                return (X) stacks[actualIndex];
            }

            public RingBufferStack(int startingIndex, int stackCapacity) {
                this.length = 0;
                this.stackCapacity = stackCapacity;
                this.startingIndex = startingIndex;
            }

            @Override
            public void addFirst(X elem) {
                if(RingBufferMultiStack.this.isFull())
                    throw new FullCollectionException();
                curentItemCount++;
            }

            @Override
            public X getFirst() {
                if(RingBufferStack.this.isEmpty())
                    throw new EmptyCollectionException();

                return access(length - 1);
            }

            @Override
            public void removeFirst() {
                if(RingBufferStack.this.isEmpty())
                    throw new EmptyCollectionException();
                curentItemCount--;
            }

            @Override
            public boolean isEmpty() {
                return length == 0;
            }
        }
    }
}
