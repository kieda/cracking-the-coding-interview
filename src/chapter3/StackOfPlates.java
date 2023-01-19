package chapter3;

import common.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Imagine a (literal) stack of plates. If the stack gets too high, it might topple.
 * Therefore, in real life, we would likely start a new stack when the previous stack exceeds some
 * threshold. Implement a data structure SetOfStacks that mimics this. SetOfStacks should be
 * composed of several stacks and should create a new stack once the previous one exceeds capacity.
 * SetOfStacks.push() and SetOfStacks.pop() should behave identically to a single stack
 * (that is, pop() should return the same values as it would if there were just a single stack).
 * FOLLOW UP
 * Implement a function popAt(int index) which performs a pop operation on a specific sub-stack
 */
public class StackOfPlates {
    public static class SetOfStacks<X> implements SimpleStack<X> {
        // idea: we can use amortized analysis to find out when and where we should merge multiple stacks together
        // refactor when currentCapacity <= maxCapacity / 2
        // Interesting idea: we could also use a BST so we can detect when a "run" of plates is at half capacity.
        // This would save space in plenty of cases. However, it would take O(log n) to remove from the middle of the stack
        // to update weights, and the worst case is still where currentCapacity <= maxCapacity / 2.

        // (stack, currentStackHeight, nodeInNonEmptyIndices)
        private List<Tuple3<SimpleDeque<X>, Integer, DoubleLinkedList.Node<Integer>>> plateList = new ArrayList<>();
        private DoubleLinkedList<Integer> nonEmptyIndices = new DoubleLinkedList<>(); // list of indices that are not empty
            // Since we only refactor the plates when we're at half capacity, it's possible that retreiveing the first
            // element will take O(n) time if we're above half capacity but have a long list of emptied cells by removing them from the middle.
            // We can make this O(1) again by keeping track of the stacks that still have elements in them, and remove them from the list
            // if the stack is empty. This holds since we only remove from the center of the stacks but cannot add to them.


        private final int maxHeight;
        private int numPlates = 0;

        private int getCurrentMaxPlates() {
            return plateList.size() * maxHeight;
        }
        private int getNumPlates() {
            return numPlates;
        }
        public SetOfStacks(int maxHeight) {
            this.maxHeight = maxHeight;
        }
        public X getAt(int index) {
            return plateList.get(index).getFirst().getFirst();
        }

        public int getNumStacks() {
            return plateList.size();
        }

        public int getStackHeight(int index) {
            return plateList.get(index).getSecond();
        }

        // returns the min and max index where we find plates with a height
        public Tuple2<Integer, Integer> getStackDomain() {
            return Tuple2.make(nonEmptyIndices.getFirst(), nonEmptyIndices.getLast());
        }

        private boolean shouldRefactor() {
            // no point in doing a refactor for 0 or 1 elements
            return getNumPlates() >= 1 && getNumPlates() <= getCurrentMaxPlates() / 2;
        }

        private void refactorPlates() {
            // O(n) complexity. However, by amortized analysis, only occurs every O(n) calls to removeAt()
            //

            // We traverse the stacks of plates by index. We only advance the index when our current stack is full
            // Otherwise we copy elements from the next stacks to fill up our current stacks, or terminate early if
            // there are no more elements to copy.
            int plateIndex = 0;

            // represents the next stack that has elements (not including our current stack).
            // If there are no more, this is null
            DoubleLinkedList.Node<Integer> nextNonEmptyStack = nonEmptyIndices.getHead();
            while(plateIndex < plateList.size()) {
                Tuple3<SimpleDeque<X>, Integer, DoubleLinkedList.Node<Integer>> currentPlateEntry = plateList.get(plateIndex);
                if(nextNonEmptyStack == null) {
                    // the rest of the stacks are empty.
                    // We trim the plateList to our current index

                    // remove our current index if it's empty. Only should happen if we've removed every element from the list
                    int removeCurrent = currentPlateEntry.getSecond() == 0 ? 1 : 0;
                    for(int removalIndex = plateList.size() - 1; removalIndex + removeCurrent > plateIndex; removalIndex--) {
                        plateList.remove(removalIndex);
                    }
                    // shrink the capacity of the plateList
                    plateList = new ArrayList<>(plateList);
                    return;
                }
                if(nextNonEmptyStack.getItem() <= plateIndex) {
                    nextNonEmptyStack = nextNonEmptyStack.getNext();
                }
                if(currentPlateEntry.getSecond() >= maxHeight) {
                    // nothing we can do here except advance our index
                    plateIndex++;
                } else if(currentPlateEntry.getSecond() == 0) {
                    // if this is empty, just get the next stack and copy it into this position, then we continue at the same index

                    // note tha nextNonEmptyStack cannot be null, since this stack is also empty.
                    // So the if statement advancing nextNonEmptyStack would have never been executed since this stack is empty
                    // and all following stacks are empty
                    int stackIdxToTransfer = nextNonEmptyStack.getItem();
                    Tuple3<SimpleDeque<X>, Integer, DoubleLinkedList.Node<Integer>> nextPlateEntry = plateList.get(stackIdxToTransfer);
                    // swap information from the current stack to the next stack
                    // update the index in the nonEmptyIndices list.
                    SimpleDeque<X> emptyDeque = currentPlateEntry.getFirst();
                    currentPlateEntry.setFirst(nextPlateEntry.getFirst());
                    currentPlateEntry.setSecond(nextPlateEntry.getSecond());
                    // NOTE: nextNonEmptyStack == nextPlateStack.getThird()
                    assert nextNonEmptyStack == nextPlateEntry.getThird();
                    nextNonEmptyStack.setItem(plateIndex); // note: we can set this index to our current one without invalidating order
                    currentPlateEntry.setThird(nextNonEmptyStack);

                    // since we transferred the contents from the next plate to this one, we set the queue to empty
                    nextPlateEntry.setFirst(emptyDeque);
                    nextPlateEntry.setSecond(0);
                    nextPlateEntry.setThird(null);

                    // don't advance the index, we may want to copy elements from future stacks into this one
                } else if(nextNonEmptyStack == null) {
                    // this implies we are at the end of the stacks that still have elements, but our current index
                    // still has some items. These will be culled on next pass
                    continue;
                } else {
                    // final case: we have a non-empty, non-full stack at the current index AND there are
                    //             stacks with elements further down.
                    //             We copy as many elements as possible (until we reach maxHeight) from the next
                    //             available stack and update the next available stack's size. If the next available
                    //             stack's size goes to zero, we advance nextNonEmptyStack and remove the node from nonEmptyIndices
                    int stackIdxToCopy = nextNonEmptyStack.getItem();
                    Tuple3<SimpleDeque<X>, Integer, DoubleLinkedList.Node<Integer>> nextPlateEntry = plateList.get(stackIdxToCopy);

                    SimpleDeque<X> nextPlateStack = nextPlateEntry.getFirst();
                    SimpleDeque<X> currentPlateStack = currentPlateEntry.getFirst();
                    int currentStackSize = currentPlateEntry.getSecond();
                    int nextStackSize = nextPlateEntry.getSecond();
                    while(currentStackSize < maxHeight && nextStackSize > 0) {
                        X bottom = nextPlateStack.getLast();
                        nextPlateStack.removeLast();
                        currentPlateStack.addFirst(bottom);
                        nextStackSize--;
                        currentStackSize++;
                    }
                    
                    // update sizes
                    currentPlateEntry.setSecond(currentStackSize);
                    nextPlateEntry.setSecond(nextStackSize);

                    if(nextStackSize == 0) {
                        nonEmptyIndices.delete(nextPlateEntry.getThird());
                        nextPlateEntry.setThird(null);
                    }
                }
            }
        }

        public void removeAt(int index) {
            Tuple3<SimpleDeque<X>, Integer, DoubleLinkedList.Node<Integer>> entry = plateList.get(index);
            SimpleStack<X> stack = entry.getFirst();
            if(!stack.isEmpty()) {
                stack.removeFirst();
                int newHeight = entry.getSecond() - 1;
                entry.setSecond(newHeight);
                numPlates--;
                if(newHeight == 0) {
                    // remove the node from the linked list
                    DoubleLinkedList.Node<Integer> node = entry.getThird(); // should never be null if the stack is not empty
                    nonEmptyIndices.delete(node);

                    // no longer exists in the linked list. prevents memory leakage
                    // and allows us to fail fast if there's a problem with our logic
                    entry.setThird(null);
                }

                if(shouldRefactor())
                    refactorPlates();
            } else {
                throw new EmptyStackException();
            }
        }

        private boolean atCapacity() {
            // NOTE: does NOT mean that all plate stacks are full. Rather, it means that
            //       the next insertion point requires us to allocate a new stack of plates
            return plateList.isEmpty() ||
                    plateList.get(plateList.size() - 1).getSecond() >= maxHeight;
        }

        @Override
        public void addFirst(X elem) {
            if(atCapacity()) {
                // this will insert a new stack at the end of the array list
                // create a new stack with size 1. Record this index as having elements
                SimpleDeque<X> newStack = new DoubleLinkedList<>();
                newStack.addFirst(elem);
                nonEmptyIndices.addLast(plateList.size()); // new index will be at size()
                DoubleLinkedList.Node<Integer> plateNode = nonEmptyIndices.getTail();

                plateList.add(Tuple3.make(newStack, 1, plateNode));
            } else if(plateList.get(nonEmptyIndices.getLast()).getSecond() >= maxHeight) {
                // this will use an existing (empty) stack that we will start to fill
                // if the last plate stack is full, then we need to start filling the next index in the plateList
                // and also add an entry to nonEmptyIndices to indicate that this entry now has an item
                int newIndex = nonEmptyIndices.getLast() + 1;
                nonEmptyIndices.addLast(newIndex);
                DoubleLinkedList.Node<Integer> plateNode = nonEmptyIndices.getTail();
                Tuple3<SimpleDeque<X>, Integer, DoubleLinkedList.Node<Integer>> entry = plateList.get(newIndex);
                entry.getFirst().addFirst(elem);
                entry.setSecond(1); // stack should only have one element
                entry.setThird(plateNode);
            } else {
                // we are filling a partially full stack of plates
                Tuple3<SimpleDeque<X>, Integer, DoubleLinkedList.Node<Integer>> entry = plateList.get(nonEmptyIndices.getLast());
                entry.getFirst().addFirst(elem);
                entry.setSecond(entry.getSecond() + 1);

                // nonEmptyIndices/Node in entry should not change, as the stack is already partially full
            }
            numPlates++;
        }

        @Override
        public X getFirst() {
            if(isEmpty())
                throw new EmptyStackException();
            // get the last stack that still has items in it, then return the top of the stack
            return plateList.get(nonEmptyIndices.getLast()).getFirst().getFirst();
        }

        @Override
        public void removeFirst() {
            removeAt(nonEmptyIndices.getLast());
        }

        @Override
        public boolean isEmpty() {
            return numPlates == 0;
        }
    }
}
