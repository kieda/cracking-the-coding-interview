package chapter1;

import common.lists.DoubleLinkedList;
import common.tests.TestException;
import common.tests.Testable;
import common.tuple.Tuple2;

import java.util.*;
import java.util.function.Predicate;

/**
 * Given a string, write a function to check if it is a permutation of a palindrome.
 * A palindrome is a word or phrase that is the same forwards and backwards.
 * A permutation is a rearrangement of letters. The palindrome does not need to be limited to just dictionary words.
 * You can ignore casing and non-letter characters.
 *
 * Example:
 * Input: "Tact Coa"
 * Output: true. (Permutations: "taco cat", "atco cta", etc.)
 *
 */
public class PalindromePermutation implements Testable {

    public boolean isPalindromePermutationHashMap(String input) {
        // Work: O(length)
        // Space: O(length), one entry per unique character in input
        //        (technically O(1), since it's limited to 2^16, the number of available chars)

        // we build a hashmap that has the counts for each character
        // then we check the parity
        Map<Character, Boolean> characterParities = new HashMap<>();
        for(int i = 0; i < input.length(); i++) {
            char key = input.charAt(i);
            characterParities.computeIfPresent(key, (c, b) -> b ^ true); // if even (0), becomes odd (1). if odd (1) becomes even (0)
            characterParities.putIfAbsent(key, true); // true represents odd (1 found), false represents even
        }
        // count the number of characters with odd parity. Will be a palindrome if <= 1
        return characterParities.entrySet().stream()
                .map(entry -> entry.getValue())
                .filter(isOdd -> isOdd)
                .count() <= 1;
    }
    public boolean isPalindromePermutationSorting(String input) {
        // Work: O(length * log(length))
        // Space: O(1)

        char[] inputChars = input.toCharArray();
        Arrays.sort(inputChars);
        Character previousChar = null;
        boolean parity = false; // start off as 0, even
        int numberOdds = 0; // if this number becomes > 1 return false
        for(int i = 0; i < inputChars.length; i++) {
            if(previousChar != null){
                if(previousChar == inputChars[i]) {
                    parity ^= true; // flip parity
                } else if(parity && ++numberOdds > 1) {
                    // reached end of a boundary, previous one has odd parity so we increment the number of odds
                    // if we have greater than one odd run, return false
                    return false;
                } else {
                    // end of boundary. Reset the parity to 0
                    parity = false;
                }
            } else {
                // count the first character
                parity ^= true;
            }
            previousChar = inputChars[i];
        }
        // do one final check at the end
        if(parity)
            numberOdds++;
        return numberOdds <= 1;
    }

    // represents a "run" of the same characters
    private class CharRunBoundary implements CharSequence {
        private int start;
        private int end;
        private char run;

        public CharRunBoundary(int start, int end, char run) {
            this.start = start;
            this.end = end;
            this.run = run;
        }
        @Override
        public int length() {
            return end - start;
        }
        @Override
        public char charAt(int index) {
            if(index < start || index >= end) {
                throw new IndexOutOfBoundsException();
            }
            return run;
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            // todo - check logic on this
            Objects.checkFromToIndex(start, end, this.end - this.start);
            return new CharRunBoundary(start + this.start, end + this.end, run);
        }
    }
    public boolean isPalindromePermutationParallel(String input) {
        // probably: sort the characters, then reduce to find boundaries for each "run" of characters
        // have a concurrent linked list for each individual element, then these become merged by looking at their neighbors

        // Work: O(n log n)  -- sorting the array
        // Space: O(n)
        // Span: O(log n)
        char[] inputChars = input.toCharArray();
        Arrays.parallelSort(inputChars);

        DoubleLinkedList<CharRunBoundary>[] initial = new DoubleLinkedList[inputChars.length];
        Arrays.parallelSetAll(initial, idx -> {
            DoubleLinkedList<CharRunBoundary> item = new DoubleLinkedList<>();
            item.addFirst(new CharRunBoundary(idx, idx+1, inputChars[idx]));
            return item;
        });

        // Example reduction:
        // AABBCC
        // AAB BCC
        // A AB B CC
        // (A) (A) (B) (B) (C) (C)
        // (A) (A, B) (B) (CC)
        // (AA, B), (B, CC)
        // (AA, BB, CC)
        Optional<DoubleLinkedList<CharRunBoundary>> result = Arrays.stream(initial).parallel()
                .reduce((left, right) -> {
                    if(left.isEmpty())
                        return right;
                    if(right.isEmpty())
                        return left;

                    // get last element from left, and first element from right
                    // merge the two CharRunBoundaries together:
                    //     if they share the same character, then expand the size of the boundary
                    //     if they do not share the same character, then keep them as distinct
                    //     then, merge the left dequeue with the right one

                    CharRunBoundary leftBoundary = left.getLast();
                    CharRunBoundary rightBoundary = right.getFirst();
                    if(leftBoundary.run == rightBoundary.run) {
                        if(leftBoundary.end != rightBoundary.end)
                            throw new IllegalStateException("expected boundary match!");
                        // merge left side, then merge the two queues
                        leftBoundary.end = rightBoundary.end;
                        right.removeFirst();
                        left.merge(right);
                        return left;
                    } else {
                        left.merge(right);
                        return left;
                    }
                });

        if(result.isEmpty())
            return true; // empty string. Is a palindrome by default
        long numberOdds = result.get().toList().parallelStream()
                // TODO : going from linked list -> array list for filter is an O(N) span operation.
                // TODO : we might be able to do this in O(log n) if we use a BST data structure or something else
                // count the number of odd-length runs
                .filter(boundary -> boundary.length() % 2 == 1)
                .count();
        return numberOdds <= 1;
    }

    private List<Tuple2<String, Boolean>> testCases = Tuple2.of2(
            "", true,
            "a", true,
            "aa", true,
            "ab", false,
            "bf kdfdkdjffkb fjdff", true,
            "bf kdfdkdjfkb fjdff", false
    );
    private List<Tuple2<String, Predicate<String>>> testFunctions = List.of(
            Tuple2.make("isPalindromePermutationHashMap", this::isPalindromePermutationHashMap),
            Tuple2.make("isPalindromePermutationSorting", this::isPalindromePermutationSorting),
            Tuple2.make("isPalindromePermutationParallel", this::isPalindromePermutationParallel)
    );

    @Override
    public void test() throws TestException {
        Testable.check(testFunctions, testCases,  PalindromePermutation.class, "");
    }
}
