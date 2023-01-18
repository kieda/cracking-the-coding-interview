package chapter2;

import common.*;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static common.SingleLinkedList.Node;

/**
 * You have two numbers represented by a linked list, where each node contains a single digit. The digits are stored
 * in reverse order, such that the 1's digit is at the head of the list.
 * Write a function that adds the two numbers and returns the sum as a linked list
 * (You are not allowed to "cheat" and just convert the linked list to an integer)
 */
public class SumLists implements Testable {
    //    9  9  9  9  9
    //    9  9  9  9  9
    // 1  9  9  9  9  8
    //             1  8
    //          1  9
    //       1  9
    //    1  9
    // 1  9

    // Note: only need to carry one digit at a time, which is 1 or 0
    // this means we just traverse from the (beginning or end) of each list, and keep a boolean carry digit
    // and do so in a for loop
    public SingleLinkedList<Byte> sumLists(SingleLinkedList<Byte> digits1, SingleLinkedList<Byte> digits2) {
        SingleLinkedList<Byte> result = new SingleLinkedList<>();
        Node<Byte> digit1 = digits1.getHead();
        Node<Byte> digit2 = digits2.getHead();
        boolean carryDigit = false;

        while(digit1 != null && digit2 != null) {
            byte resultDigit = (byte)(digit1.getItem() + digit2.getItem()
                    + (carryDigit ? 1 : 0));
            carryDigit = resultDigit >= 10;
            if(carryDigit) {
                resultDigit = (byte)(resultDigit % 10);
            }
            result.addFirst(resultDigit);
            digit1 = digit1.getNext();
            digit2 = digit2.getNext();
        }
        SingleLinkedList.Node<Byte> endDigits = digit1 == null ? digit2 : digit1;

        while(endDigits != null) {
            byte resultDigit = (byte)(endDigits.getItem() + (carryDigit ? 1 : 0));
            carryDigit = resultDigit >= 10;
            if(carryDigit) {
                resultDigit = (byte)(resultDigit % 10);
            }
            result.addFirst(resultDigit);
            endDigits = endDigits.getNext();
        }
        if(carryDigit) {
            byte one = 1;
            result.addFirst(one);
        }

        // We built the result list back to front, so now we need to reverse it
        result.reverse();

        return result;
    }

    private static SingleLinkedList<Byte> toDigits(long i) {
        SingleLinkedList<Byte> result = new SingleLinkedList<>();
        while(i > 0) {
            byte digit = (byte)(i % 10);
            result.addFirst(digit);
            i = i / 10;
        }
        i++;
        result.reverse();
        return result;
    }
    private static long fromDigits(SingleLinkedList<Byte> i) {
        long result = 0;
        long multiplier = 1;
        SingleLinkedList.Node<Byte> node = i.getHead();
        while(node != null) {
            result += node.getItem() * multiplier;
            multiplier *= 10;
            node = node.getNext();
        }
        return result;
    }

    private final List<Tuple2<Tuple2<SingleLinkedList<Byte>, SingleLinkedList<Byte>>, SingleLinkedList<Byte>>> testCases = Tuple3.<Long, Long, Long>of3(
        2912932L, 939L, 2913871L,
            0L, 2932913L, 2932913L,
            0L, 0L, 0L,
            2932L, 959501239L, 959504171L,
            99999999L, 99999999L, 199999998L
    ).stream().map(test -> Tuple2.make(Tuple2.make(toDigits(test.getFirst()), toDigits(test.getSecond())), toDigits(test.getThird())))
            .collect(Collectors.toUnmodifiableList());

    private List<Tuple2<String, BinaryOperator<SingleLinkedList<Byte>>>> testFunctions = List.of(
            Tuple2.make("sumLists", this::sumLists)
    );

    @Override
    public void test() throws TestException {
        Testable.check(testFunctions, testCases, SumLists.class, "");
    }
}
