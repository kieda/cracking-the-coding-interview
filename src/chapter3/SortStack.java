package chapter3;

import common.SingleLinkedList;
import common.Sort;

import java.util.function.UnaryOperator;

/**
 * Write a program to sort a stack such that the smallest items are on the top. You can use
 * an additional temporary stack, but you may not copy the elements into any other data structure
 * (such as an array). The stack supports the following operations: push, pop, peek, and is Empty.
 */
public class SortStack<X extends Comparable<X>> {
    public SingleLinkedList<X> sortStack(SingleLinkedList<X> stack, UnaryOperator<X> copyFn) {
        SingleLinkedList<X> result = stack.copy(copyFn);
        X temp = null;
        SingleLinkedList<X> tempStack = new SingleLinkedList<>();

        // idea: take elements from stack out one at a time and find their placement by finding its position in tempStack
        // invariant: tempStack is always sorted
        while(!result.isEmpty()) {
            temp = result.getFirst();
            result.removeFirst();
            while(!tempStack.isEmpty() && Sort.compare(tempStack.getFirst(), temp) > 0) {
                result.addFirst(tempStack.getFirst());
                tempStack.removeFirst();
            }
            tempStack.addFirst(temp);
        }

        while(!tempStack.isEmpty()) {
            result.addFirst(tempStack.getFirst());
            tempStack.removeFirst();
        }
        return result;
    }
}
