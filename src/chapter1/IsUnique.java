package chapter1;

import common.TestException;
import common.Testable;
import common.Tuple2;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implement an algorithm to determine if a string has all unique characters. What if you cannot use additional data structures?
 */
public class IsUnique implements Testable {
    /**
     * Uses java stream implementation for a simpler yet unsatisfactory method for checking the uniqueness of all strings
     *
     * Note that if we have a parallel implementation, we could achieve an O(log n) span implementation
     * O(log n) span if we sort in parallel and then O(1) to count in parallel
     */
    public boolean isUniqueStream(String toCheck) {
        // simple java stream implementation
        return toCheck.chars().distinct().count() == (long)toCheck.length();
    }

    /**
     * Runs through the string and accumulates the characters into a hashmap
     * If we find the character again in the hashmap we can terminate early since we know it's available
     *
     * O(1) work done for each character visited, one for accessing set and one for adding item to the set
     * O(1) space done for each character visited
     *
     * O(n) work and space done total.
     * Note that there may be slowdowns from many lookups. For certain types of strings it might be better to run through all characters,
     * then return toCheck.length() == charsSeen.size()
     */
    public boolean isUniqueHashSet(String toCheck) {
        // use a hashset for O(n) implementation
        HashSet<Character> charsSeen = new HashSet<>();
        for(int i = 0; i < toCheck.length(); i++) {
            Character currentChar = toCheck.charAt(i);
            if(charsSeen.contains(currentChar)) {
                return false;
            }
            charsSeen.add(currentChar);
        }
        return true;
    }

    /**
     * Sorts the in place first, then runs through the entire length in order to see if any neighbors are the same
     * O(1) space used.
     * O(n log n) total. n log n from sorting, n for checking each neighbor
     *
     * Note: technically we use O(n) space from toCharArray which creates a copy. However we could just pass in char[]
     * if we really wanted it to be completely in place
     */
    public boolean isUniqueSorted(String toCheck) {
        char[] toCheckChars = toCheck.toCharArray();
        // sort the array first
        Arrays.sort(toCheckChars);
        // next iterate and see if we have two chars in a row
        for(int i = 1; i < toCheckChars.length; i++) {
            if(toCheckChars[i - 1] == toCheckChars[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests for uniqueness using parallel computation
     */
    public boolean isUniqueSortedParallel(String toCheck) {
        char[] toCheckChars = toCheck.toCharArray();

        // sort array in parallel, O(log n) span
        Arrays.parallelSort(toCheckChars);

        return IntStream.range(0, toCheckChars.length)
                .parallel()
                // map to (index, character)
                .mapToObj(i -> new Tuple2<>(i, toCheckChars[i]))
                // map to a boolean - false if neighbors are not unique, true otherwise
                // O(1) span
                .map(idxAndChar -> {
                    if(idxAndChar.getFirst() == 0)
                        return true;
                    return !idxAndChar.getSecond().equals(toCheckChars[idxAndChar.getFirst() - 1]);
                })
                // reduce booleans in parallel via logical and
                // O(log n) span
                .collect(Collectors.reducing(true, Boolean::logicalAnd));
    }
    List<Tuple2<String, Boolean>> testCases = Tuple2.of2(
        "abcdef", true,
        "bacdeaf", false,
        "bacfdef", false,
        "a", true,
        "", true
    );
    public List<Tuple2<String, Predicate<String>>> testFunctions = List.of(
        Tuple2.make("isUniqueHashSet", this::isUniqueHashSet),
        Tuple2.make("isUniqueStream", this::isUniqueStream),
        Tuple2.make("isUniqueSorted", this::isUniqueSorted),
        Tuple2.make("isUniqueSortedParallel", this::isUniqueSortedParallel)
    );

    public void test() throws TestException {
        Testable.check(testFunctions, testCases, Objects::equals, IsUnique.class, "function did not detect isUnique!");
    }
}
