package chapter1;

import common.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Given two strings, write a method to decide if one is a permutation of the other
 */
public class CheckPermutation implements Testable {

    private boolean customArrayEquals = true;

    public boolean checkPermutationOneSorted(String left, String right) {
        // sort Right, then run through all in left and find position
        // set the ones found to -1
        if(left.length() != right.length())
            return false;
        IntStream leftChars = left.chars();
        int[] rightChars = right.chars().sorted().toArray();
        for (PrimitiveIterator.OfInt it = leftChars.iterator(); it.hasNext(); ) {
            int leftChar = it.next();
            int position = Sort.binarySearch(rightChars, leftChar);
            if(position == -1) {
                return false;
            }
            rightChars[position] = -1;
        }
        return true;
    }

    public boolean checkPermutationBothSorted(String left, String right) {
        // permutation must be of the same length
        if(left.length() != right.length())
            return false;
        char[] leftChars = left.toCharArray();
        char[] rightChars = right.toCharArray();

        // 1. sort each. O( M log M ) where M = max(|L|, |R|)
        Arrays.sort(leftChars);
        Arrays.sort(rightChars);

        // 2. run through each in parallel and check. Terminate early if there's a difference
        if(customArrayEquals) {
            // custom implementation - run through
            for(int i = 0; i < left.length(); i++) {
                if(leftChars[i] != rightChars[i])
                    return false;
            }
            return true;
        } else {
            return Arrays.equals(leftChars, rightChars);
        }
    }
    public boolean checkPermutationHashMap(String left, String right) {
        if(left.length() != right.length())
            return false;

        // build map for left chars
        Map<Character, Integer> charCounts = new HashMap<>(left.length());
        for(char leftChar : left.toCharArray()) {
            charCounts.putIfAbsent(leftChar, 1);
            charCounts.computeIfPresent(leftChar, (c, i) -> i+1);
        }
        // run through right chars
        for(char rightChar : right.toCharArray()) {
            if(!charCounts.containsKey(rightChar))
                // char is not on the right hand side
                return false;
            charCounts.computeIfPresent(rightChar, (c, i) -> i-1);
        }
        for(int counts : charCounts.values()) {
            if(counts != 0)
                return false;
        }
        return true;
    }

    public boolean checkPermutationParallel(String left, String right) {
        if(left.length() != right.length())
            return false;
        char[] leftChars = left.toCharArray();
        char[] rightChars = right.toCharArray();
        // span: O(log n)
        Arrays.parallelSort(leftChars);
        Arrays.parallelSort(rightChars);

        // span: O(log n) to combine the result of comparing left chars and right chars
        return IntStream.range(0, left.length())
                .parallel()
                .allMatch(i -> leftChars[i] == rightChars[i]);
    }

    private List<Tuple2<Tuple2<String, String>, Boolean>> testCases = Tuple2.of2(
        Tuple2.make("asdf", "fsad"), true,
            Tuple2.make("afffsd", "fsfafd"), true,
            Tuple2.make("afffsfd", "fsfafd"), false,
            Tuple2.make("affsfd", "fsfefd"), false
    );

    private List<Tuple2<String, BiPredicate<String, String>>> testFunctions = List.of(
        Tuple2.make("checkPermutationBothSorted", this::checkPermutationBothSorted),
        Tuple2.make("checkPermutationOneSorted", this::checkPermutationOneSorted),
        Tuple2.make("checkPermutationHashMap", this::checkPermutationHashMap),
        Tuple2.make("checkPermutationParallel", this::checkPermutationParallel)
    );

    @Override
    public void test() throws TestException {
        Testable.check(testFunctions, testCases,  CheckPermutation.class, "");
    }
}
