package chapter1;

import common.tests.TestException;
import common.tests.Testable;
import common.tuple.Tuple2;
import common.tuple.Tuple3;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implement a method to perform basic string compression using the counts of repeated characters.
 * For example, the string "aabcccccaaa" would become "a2b1c5a3"
 * If the "compressed" string would not become smaller than the original string, your method should return the original string
 * You can assume the string has only uppercase and lowercase letters (a-z)
 */
public class StringCompression implements Testable {

    private int numDigits(int number) {
        // max value will be
        // 2147483647
        // 1000000000 -> 10
        //  100000000 -> 9
        //
        //   10000000 -> 8
        //    1000000 -> 7
        //     100000 -> 6

        //      10000 -> 5
        //       1000 -> 4
        //        100 -> 3
        //         10 -> 2
        //          1 -> 1
        if(number >= 100000) {
            // 6-10
            if(number >= 100000000) {
                // 9-10
                if(number >= 1000000000) return 10;
                else return 9;
            } else {
                // 6-8
                if(number >= 1000000) {
                    //7-8
                    if(number >= 10000000) return 8;
                    else return 7;
                } else return 6;
            }
        } else {
            // 1-5
            if(number >= 100) {
                // 3-5
                if(number >= 1000) {
                    // 4-5
                    if(number >= 10000) {
                        return 5;
                    } return 4;
                } else return 3;
            } else {
                if(number >= 10) return 2;
                else return 1;
            }
        }
    }
    private int countRunLengthForward(char[] string, int position) {
        if(position >= string.length || position < 0) {
            return -1;
        }
        char current = string[position];
        int idx = position;
        for(;idx < string.length && string[idx] == current; idx++) {}
        return idx - position;
    }
    private int countRunLengthBackward(char[] string, int position) {
        if(position >= string.length || position < 0) {
            return -1;
        }
        char current = string[position];
        int idx = position;
        for(;idx >= 0 && string[idx] == current; idx--) {}
        return position - idx;
    }
    // fills the digits of the number, going in reverse from index
    // returns the number of digits filled. Only works for positive, nonzero numbers
    private int fillDigits(char[] string, int index, int number) {
        int count = 0;
        while(number > 0) {
            int digit = number % 10;
            string[index] = Character.forDigit(digit, 10);
            number = number / 10;
            count++;
            index--;
        }
        return count;
    }

    public Tuple2<char[], Integer> stringCompressionLinear(char[] input) {
        // do string compression in-place
        // first, we find the resulting size of the string
        // if the result would be larger, then we return the original input
        // otherwise, work backwards from the end replacing the characters in the string

        char[] result = Arrays.copyOf(input, input.length); // modify this array in place. Keep the original for testing purposes

        // aaaaabc
        int finalLength = 0;
        {
            int idx = 0;
            while(true) {
                int runLength = countRunLengthForward(input, idx);
                if(runLength < 0) break;
                idx += runLength;
                finalLength += 1 + numDigits(runLength);
            }
        }
        if(finalLength > input.length) {
            return Tuple2.make(result, result.length);
        }

        //  mark our current index = I
        //  run through the char array keeping track of what our index should be. When we come across something that
        //  puts actual index >= expected index, then we can fill. This guarantees that we actually have the space to
        //  start filling
        //  note that we are guaranteed to find this point due to our check above.

        Character previous = null;
        int expectedLength = 0;
        int markedIndex = 0;
        int currentRunIndex = 0;
        int overwrittenChars = 0;
        int overwrittenIndex = -1;
        for(int idx = 0; idx < result.length + 1; idx++) {
            Character current = idx == result.length ? null : result[idx];

            if(current == null || (previous != null && previous != current)) {
                int runLength = idx - currentRunIndex;
                expectedLength += 1 + numDigits(runLength);
                currentRunIndex = idx; // start a new run at the current index
            }
            // add an additional char if next one doesn't match, in case we have a series of single-chars
            int estimatedLength = expectedLength + (idx + 1 < result.length && result[idx + 1] != current ? 1 : 0);
            int backwardsTraversal = currentRunIndex - 1; // go from right before this run begins to the marked index
            if(idx + 1 >= estimatedLength && backwardsTraversal >= markedIndex) {
                // we have reached an inflection point: the length of the current string is longer than the resulting string
                int overwrittenCharsPrev = overwrittenChars;
                int overwrittenIndexPrev = overwrittenIndex;
                // we will be overwriting chars, store this for later
                int fillInLocation = expectedLength - 1; // we fill in starting from the end result's final index, going backwards
                overwrittenChars = countRunLengthBackward(result, fillInLocation);
                overwrittenIndex = fillInLocation + countRunLengthForward(result, fillInLocation) - 1; // get the last index of this run, not the first index of the next run

                while(backwardsTraversal >= markedIndex) {
                    char runChar = result[backwardsTraversal];
                    int runLength = countRunLengthBackward(result, backwardsTraversal);
                    // we have encountered a segment we have previously overwritten. Add the char count we saved earlier.
                    if(backwardsTraversal >= overwrittenIndexPrev && backwardsTraversal - runLength + 1 <= overwrittenIndexPrev) {
                        runLength += overwrittenCharsPrev;
                    }
                    // fill in digits (in reverse)
                    int charsFilledCount = fillDigits(result, fillInLocation, runLength);
                    fillInLocation -= charsFilledCount;
                    // then fill in the char
                    result[fillInLocation] = runChar;
                    fillInLocation--; // decrement once accounting for the char
                    backwardsTraversal -= runLength; // traverse back through the runs
                }
                markedIndex = currentRunIndex;
            }
            previous = current;
        }

        // add values for final character
        return Tuple2.make(result, finalLength);
    }

    private final List<Tuple2<char[], Tuple2<char[], Integer>>> testCases = Tuple3.<String, String, Integer>of3(
            // input length, input string, output length, output string
            // note some strings exceed their expected length, and we expect the trailing characters to be unmodified
            "", "", 0,
            "a", "a", 1,
            "aa", "a2", 2,
            "ab", "ab", 2,
            "aaa", "a3a", 2,
            "aaaaaaaaaaaabbbbbbbbbbbbb", "a12b13aaaaaabbbbbbbbbbbbb", 6,
            "aaaaaaaaaaabcadfghi", "a11b1c1a1d1f1g1h1i1", 19,
            "bcadfghiaaaaaaaaaaa", "b1c1a1d1f1g1h1i1a11", 19,
            "bcadaaaaaaaaaaafghi", "b1c1a1d1a11f1g1h1i1", 19,
            "baaacaaadaaa", "b1a3c1a3d1a3", 12,
            "baaacaaadaaaab", "b1a3c1a3d1a4b1", 14,
            "aabbccdd", "a2b2c2d2", 8,
            "aaabbbcccdddeeefffabcde", "a3b3c3d3e3f3a1b1c1d1e1e", 22,
            "abcdeaaabbbcccdddeeefff", "a1b1c1d1e1a3b3c3d3e3f3f", 22,
            "bcadaaaaaaaaafgh", "b1c1a1d1a9f1g1h1", 16,
            "bcadaaaaaaaaaafgh", "b1c1a1d1a10f1g1h1", 17,
            "bcadaaaaaaaaaaafghibcadaaaaaaaaaaafghibcadaaaaaaaaaaafghi", "b1c1a1d1a11f1g1h1i1b1c1a1d1a11f1g1h1i1b1c1a1d1a11f1g1h1i1", 19*3
    ).stream().map(testCase -> Tuple2.make(
            // args : char[]
            testCase.getFirst().toCharArray(),
            // result : char[], finalLength : int
            Tuple2.make(testCase.getSecond().toCharArray(), testCase.getThird())
    )).collect(Collectors.toUnmodifiableList());

    private final List<Tuple2<String, Function<char[], Tuple2<char[], Integer>>>> testFunctions = List.of(
            Tuple2.make("stringCompressionLinear", this::stringCompressionLinear)
    );
    @Override
    public void test() throws TestException {
        Testable.check(testFunctions, testCases,  StringCompression.class, "");
    }
}