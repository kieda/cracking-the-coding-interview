package chapter1;

import common.tests.TestException;
import common.tests.Testable;
import common.tuple.Tuple2;
import common.tuple.Tuple3;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * There are three types of edits that can be formed on strings:
 *   Insert a character
 *   Remove a character
 *   Replace a character
 * Given two strings, write a function to check if they are one edit (or zero edits) away
 */
public class OneAway implements Testable {

    public boolean isOneAwayLinear(String a, String b) {
        // note that remove a character on a is an identical operation to insert a character on b
        // so we just flip it, and just check for insertion on a
        if(a.length() > b.length()) {
            return isOneAwayLinear(b, a);
        } else if(b.length() - a.length() > 1) {
            // note that these two must be exactly one or zero away. If more we are more than one edit away
            return false;
        } else if(a.length() == b.length()) {
            // check if strings are equal or one replacement away.
            // the best we can do is O(n) sequentially since we have no other info that we can use about the strings
            int replacementCount = 0;
            for(int i = 0; i < a.length(); i++) {
                if(a.charAt(i) != b.charAt(i)) {
                    if(++replacementCount > 2) break;
                }
            }
            return replacementCount <= 1;
        } else {
            // check if a can become b by inserting one character.
            // best way to do this sequentially is O(n)
            int insertCount = 0;
            for(int i = 0; i < b.length(); i++) {
                if(a.charAt(i + insertCount) != b.charAt(i)) {
                    // simulate inserting a character into a.
                    // Note that this only works once because a.length() = b.length() - 1
                    if(++insertCount > 2) break;
                }
            }
            return insertCount <=1;
        }
    }

    public boolean isOneAwayParallel(String a, String b) {
        IntPredicate charsEqual = idx -> a.charAt(idx) != b.charAt(idx);
        // note that remove a character on a is an identical operation to insert a character on b
        // so we just flip it, and just check for insertion on a
        if(a.length() > b.length()) {
            return isOneAwayParallel(b, a);
        } else if(b.length() - a.length() > 1) {
            // note that these two must be exactly one or zero away. If more we are more than one edit away
            return false;
        } else {
            // bad way : tabulate every possibility where we insert a special blank character into the string (in parallel)
            //           then check equality on all (in parallel).
            //           Work: O(length ^ 2), Span: O(1)
            // cool way : let N be the count the number of characters that are different in the two strings.
            //            If N = 0 we return true
            //            Otherwise, check the first (b.length - N) characters, then from (b.length - N + 1) to b.length - 1
            //            If both are false, return false.
            //            Work: O(length), Span: O(1)
            int numDifferences = (int)IntStream.range(0, b.length())
                    .parallel()
                    .filter(idx -> {
                        // basically adds one to the count if the lengths are inequal
                        if(idx >= a.length()) {
                            return true;
                        }
                        return a.charAt(idx) != b.charAt(idx);
                    })
                    .count();
            if(numDifferences <= 1) {
                // either one character is replaced
                // or one extra character at the end of b
                return true;
            } else if(a.length() == b.length()) {
                // cannot replace more than one character.
                // we need this clause in a peculiar case where we would return true with two replacements

                // note that A and B would incorrectly match, we check the range [0, 5) and [6, 7) in b against A
                //    012345
                // A: abcfdge
                //    01234 5
                // B: abcfdeg
                return false;
            }

            int midpoint = b.length() - numDifferences;
            // abcefg
            // abcdefg
            // numDifferences = 4, left range = [0, 3), right range = [4, 7)
            // bcdefg
            // abcdefg
            // numDifferences = 7, left range = [0, 0), right range = [1, 7)
            // abcdef
            // abcdefg
            // numDifferences = 1, left range = [0, 6), right range = [7, 7)
            long leftDifferences = IntStream.range(0, midpoint)
                    .parallel()
                    .filter(charsEqual)
                    .count();
            long rightDifferences = IntStream.range(midpoint + 1, b.length())
                    .parallel()
                    .filter(idx -> a.charAt(idx - 1) == b.charAt(idx))
                    .count();
            // there should be no differences
            return leftDifferences + rightDifferences == 0;
        }
    }

    List<Tuple2<Tuple2<String, String>, Boolean>> testCases = Tuple3.<String, String, Boolean>of3(
            "abcdefg", "abcdefg", true,
            "abcdefg", "bcdefg", true,
            "abcefg", "abcdefg", true,
            "abcdef", "abcdefg", true,
            "abcfdge", "abcfdeg", false,
            "abcdefg", "abqdefg", true,
            "x", "", true,
            "", "", true,
            "abcdefg", "abqdlfg", false,
            "abcdefg", "abcqfg", false
    ).stream()
            .map(testCase -> Tuple2.make(Tuple2.make(testCase.getFirst(), testCase.getSecond()), testCase.getThird()))
            .collect(Collectors.toUnmodifiableList());

    public List<Tuple2<String, BiPredicate<String, String>>> testFunctions = List.of(
            Tuple2.make("isOneAwayLinear", this::isOneAwayLinear),
            Tuple2.make("isOneAwayParallel", this::isOneAwayParallel)
    );
    @Override
    public void test() throws TestException {
        Testable.check(testFunctions, testCases, Objects::equals, OneAway.class, "mismatch on OneAway!");
    }
}
