package chapter2;

import common.lists.SingleLinkedList;
import common.Sort;

import java.util.function.UnaryOperator;

import static common.lists.SingleLinkedList.Node;

/**
 * Write code to partition a linked list around value x, such that all nodes less than x come before all nodes greater than
 * or equal to x.
 * (IMPORTANT: the partition element x can appear anywhere in the "right partition"; it does not need to appear between the left and right partitions.
 * The additional spacing in the example below indicates the partition. Yes, the output below is one of many valid outputs)
 *
 * Example:
 * INPUT:  3->5->8->5->10->2->1, partition = 5
 * OUTPUT: 3->1->2  ->  10->5->5->8
 *
 */
public class Partition<X extends Comparable<X>> {
    public SingleLinkedList<X> partition(SingleLinkedList<X> input, UnaryOperator<X> copyFn, X pivot) {
        SingleLinkedList<X> result = input.copy(copyFn);
        Node<X> node = result.getHead();
        Node<X> previous = null;
        Node<X> first = node;
        while(node != null) {
            int comparison = Sort.compare(node.getItem(), pivot);
            if(comparison >= 0) {
                // item is greater than or equal to the pivot
                previous = node;
                node = node.getNext();
            } else {
                Node<X> next = node.getNext();
                if(previous != null) {
                    node.setNext(first);
                    previous.setNext(next);
                    first = node;
                }
                node = next;
            }
        }
        result.setHead(first);
        return result;
    }


}
