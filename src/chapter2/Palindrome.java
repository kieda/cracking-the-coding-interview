package chapter2;

import common.*;
import common.lists.SingleLinkedList;
import common.tests.TestException;
import common.tests.Testable;
import common.tuple.Tuple2;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static common.lists.SingleLinkedList.Node;

/**
 * Implement a function to check if a linked list is a palindrome
 */
public class Palindrome<X> implements Testable {
    public boolean isPalindrome(SingleLinkedList<X> list) {
        Node<X> frontRunner = list.getHead();
        Node<X> midRunner = list.getHead();

        if(frontRunner == null || frontRunner.getNext() == null) {
            // 0 and 1 sized lists are both palindromes
            return true;
        }

        boolean parity = false;
        // have frontRunner move two spaces for every space moved by midRunner
        for(; frontRunner != null; parity = parity ^ true) {
            frontRunner = frontRunner.getNext();
            if(parity) midRunner = midRunner.getNext();
        }

        // now, midRunner should be in the middle, and frontRunner should be at the end
        // parity true if odd size list, false if even size
        final Node<X> midPoint = midRunner;
        final Node<X> nextMidPoint = midRunner.getNext();

        try {
            // we reverse the first section of the list till we reach the midPoint
            list.reverse(midPoint);

            // run from midpoint to the end
            // and compare to front to midpoint
            frontRunner = parity ? list.getHead().getNext() : list.getHead();
            midRunner = nextMidPoint;
            while (midRunner != null && frontRunner != nextMidPoint) {
                if (!Objects.equals(frontRunner.getItem(), midRunner.getItem())) {
                    return false;
                }
                frontRunner = frontRunner.getNext();
                midRunner = midRunner.getNext();
            }

            return true;
        } finally {
            // re-reverse the list to put it back
            list.reverse(midPoint);
        }
    }

    private final List<Tuple2<SingleLinkedList<X>, Boolean>> testCases;
    public Palindrome(List<Tuple2<SingleLinkedList<X>, Boolean>> testCases) {
        this.testCases = testCases;
    }
    public Palindrome() {
        this(List.of());
    }
    public static final Palindrome<Character> INSTANCE = new Palindrome<>(Tuple2.<String, Boolean>of2(
        "", true,
            "a", true,
            "ab", false,
            "aba", true,
            "abba", true,
            "abca", false,
            "abcba", true
    ).stream().map(test -> Tuple2.make(SingleLinkedList.make(ArrayUtil.toObject(test.getFirst().toCharArray())), true))
            .collect(Collectors.toUnmodifiableList()));

    private List<Tuple2<String, Predicate<SingleLinkedList<X>>>> testFunctions = List.of(
            Tuple2.make("isPalindrome", this::isPalindrome)
    );

    @Override
    public void test() throws TestException {
        Testable.check(testFunctions, testCases, Palindrome.class, "");
    }
}
