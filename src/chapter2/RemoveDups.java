package chapter2;


import common.*;
import common.lists.SingleLinkedList;
import common.tests.TestException;
import common.tests.Testable;
import common.tuple.Tuple2;

import static common.lists.SingleLinkedList.Node;

import java.util.Arrays;
import java.util.Random;
import java.util.function.UnaryOperator;

/**
 * Write code to remove duplicates from an unsorted linked list
 * Follow up: how would you solve this problem if a temporary buffer is not allowed?
 */
public class RemoveDups<X extends Comparable<X>> implements Testable {

    /**
     * Merges parts of a linked list. We assume that start1 .. end1 and start2 .. end2 are in ascending order
     * according to the comparator.
     *
     * We also assume that start1.. end1 comes before start2.. end2 in the list position, such that
     * end1.getNext() == start2. We assume all inputs are non-null.
     *
     * We return the new start and end of the list. The start and end are inclusive
     */
    private Tuple2<Node<X>, Node<X>> mergeLinkedLists(
            SingleLinkedList<X> debug,
        Node<X> start1, Node<X> end1,
        Node<X> start2, Node<X> end2) {
        // since we assume the two lists are already sorted, the head of the list must be one of the two starting positions
        // same is true for the last element of the list
        Node<X> first = Sort.compare(start1.getItem(), start2.getItem()) > 0 ? start2 : start1;
        Node<X> last = Sort.compare(end1.getItem(), end2.getItem()) > 0 ? end1 : end2;

        Node<X> previous1 = null;
        do {
            // merge the second list into the first one. We do this one at a time for simplicity, but we could also
            // use a secondary for loop for fewer operations
            int compareHeads = Sort.compare(start1.getItem(), start2.getItem());
            if(compareHeads > 0) {
                // this means that start1 is greater than or equal to start2's position.

                // we put start2 at the right before start1 as it is a lesser value
                Node<X> next2 = start2.getNext();
                start2.setNext(start1);
                if(previous1 != null) {
                    previous1.setNext(start2);
                }
                previous1 = start2;

                // then we set the end of the first list to the next item in the list
                end1.setNext(next2);

                // start2 is advanced, but start1 remains the same
                // do not advance past the end
                if(start2 != end2) {
                    start2 = next2;
                } else {
                    // we just inserted the last item in the list, which means we can exit the algorithm
                    break;
                }
            } else if(start1 != end1) {
                // start1 is less than start2's position
                // we advance start1 forward
                previous1 = start1;
                start1 = start1.getNext();
            }
            // if start2 is greater than or equal to end1, then from our assumption of order then all
            // items from start2..end2 will be greater than or equal to end1 so we can exit
        } while(Sort.compare(end1.getItem(), start2.getItem()) > 0);

        return Tuple2.make(first, last);
    }

    /**
     * Returns "endPos", the node at the end of the chunk, starting from startPos,
     * Such that the number of nodes from startPos.. endPos is equal to chunkSize
     * If we reach the end of the list, we return the last node in the list
     */
    private Node<X> getChunkEnd(Node<X> startPos, int chunkSize) {
        for(int count = 1; count < chunkSize && startPos.getNext() != null; count++) {
            startPos = startPos.getNext();
        }
        return startPos;
    }

    private void mergeSortLinkedList(SingleLinkedList<X> list) {
        // we do a merge sort from the bottom up
        // we run through the list O(log n) times, where n is the length of the list
        // Running through the list is in O(n). Thus we have O(n log n) complexity
        // This takes O(1) space as we merge iteratively

        int size = list.count();
        int mergeSize = 1;

        while(mergeSize < size) {
            Node<X> start1 = list.getHead();
            Node<X> end1;
            Node<X> start2;
            Node<X> end2 = null;

            mergeChunks : while(true) {
                if(end2 != null) {
                    start1 = end2.getNext();
                }
                if (start1 == null) {
                    // we've reached the end of the list
                    break mergeChunks;
                }
                end1 = getChunkEnd(start1, mergeSize);
                start2 = end1.getNext();
                if (start2 == null) {
                    // case where we don't have enough for two chunks to merge
                    break mergeChunks;
                }
                Node<X> previousChunk = end2;
                end2 = getChunkEnd(start2, mergeSize);

                // merge these two together. This should keep ordering to the next (unsorted) chunks
                Tuple2<Node<X>, Node<X>> startAndEnd = mergeLinkedLists(list, start1, end1, start2, end2);
                end2 = startAndEnd.getSecond(); // end2 might be different after sorting. We set it to the actual position

                // the start of the list might change by sorting. Have this reflected in the list
                if(start1 == list.getHead()) {
                    list.setHead(startAndEnd.getFirst());
                } else {
                    previousChunk.setNext(startAndEnd.getFirst());
                }
            }
            mergeSize <<= 1;
        }
    }

    /**
     * Assume that the list is already sorted. Removes duplicates from the list
     */
    private void removeDuplicatesSortedLinkedList(SingleLinkedList<X> list) {
        Node<X> current = list.getHead();
        if(current == null)
            return;
        Node<X> next;
        while((next = current.getNext()) != null) {
            if(Sort.compare(current.getItem(), next.getItem()) == 0) {
                // remove the next node
                current.setNext(next.getNext());
            } else {
                current = next;
            }
        }
    }

    public SingleLinkedList<X> removeDuplicatesSorting(SingleLinkedList<X> list, UnaryOperator<X> copyOp) {
        SingleLinkedList result = list.copy(copyOp);
        mergeSortLinkedList(result);
        removeDuplicatesSortedLinkedList(result);
        return result;
    }

    public static void main(String[] args) {
        RemoveDups<Integer> test = new RemoveDups<>();

        // test with nulls
        SingleLinkedList<Integer> test1 = SingleLinkedList.make(10, 1, 30, null, 7, 2, 5, null);
        System.out.println(test1);
        test.mergeSortLinkedList(test1);
        System.out.println(test1);

        int maxTestLength = 36;
        int maxTests = 50;

        Random random = new Random();
        // randomized tests
        for(int size = 0; size <= maxTestLength; size++) {
            for(int testCount = 0; testCount < ((size <= 1) ? 1 : maxTests); testCount++) {
                Integer[] testCase = new Integer[size];
                for (int i = 0; i < size; i++) {
                    testCase[i] = random.nextInt() % 10000;
                }
                Integer[] expectedArray = Arrays.copyOf(testCase, size);
                Arrays.sort(expectedArray);
                SingleLinkedList<Integer> expected = SingleLinkedList.make(expectedArray);
                SingleLinkedList<Integer> actual = SingleLinkedList.make(testCase);
                test.mergeSortLinkedList(actual);
                if (!expected.equals(actual)) {
                    System.out.println("MISMATCH: " + Arrays.toString(testCase)
                            + "\nEXPECTED: " + expected
                            + "\nACTUAL: " + actual);
                }
            }
        }
    }

    @Override
    public void test() throws TestException {

    }
}
