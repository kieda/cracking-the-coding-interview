package chapter1;

import common.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * Write a method to replace all spaces in a string with '%20'. You may assume that the string
 * has sufficient space at the end to hold the additional characters, and that you are given the "true"
 * length of the string.
 *
 * Note: if implementing in Java, please use a character array so you can perform this operation in place
 */
public class URLify implements Testable {

    /**
     * @param in input char array, that is modified by this method and returned in place
     * @param length the length of the string that we want to analyze, such that
     *               input.length >= {(' ', idx) in input where idx < length}.length * 2 + length
     *               essentially meaning that the length represents the end of the sequence of characters we care about,
     *               and the rest is disposable and can be overwritten, and we have enough characters in the initial array
     *               to produce the final version
     *
     *               The expected runtime is O(length) and takes no auxilary space.
     * @return (urlOutput, newLength)
     */
    public Tuple2<char[], Integer> urlifyLinear(char[] in, int length) {
        // make a copy of this
        char[] input = Arrays.copyOf(in, in.length);
        // find total number of spaces
        int numSpaces = 0;
        for(int i = 0; i < length; i++) {
            numSpaces += (input[i] == ' ' ? 1 : 0);
        }
        // new total length of the URL
        final int newLength = numSpaces * 2 + length;

        // work backwards copying the input. Doing so backwards allows us to perform this in place without losing/overwriting
        // data or having to store it in an array
        int currentIndex = length - 1; // index we are copying from
        for(int i = newLength - 1; i >= 0; i--) { // index we are copying to.

            assert currentIndex <= i : String.format("index copying from %d > index copying to %d", currentIndex, i);
            if(input[currentIndex] == ' ') {
                // copy over the space. Index we're copying into goes back as well as the current index
                assert i >= 2;
                assert numSpaces-- > 0; // assert how many spaces we've used and decrement
                input[i - 2] = '%';
                input[i - 1] = '2';
                input[i] = '0';
                i-= 2;
                currentIndex--;
            } else {
                // copy over the character
                input[i] = input[currentIndex--];
            }
            // we have inserted all of the spaces and now all of the characters should be the same before this point
            if(currentIndex == i) {
                assert numSpaces == 0; // this should be zero
                break;
            }
        }

        return Tuple2.make(input, newLength);
    }

    public Tuple2<char[], Integer> urlifyParallel(char[] in, int length) {
        //CharBuffer.w
        //List.of().stream().parallel().
        //Collectors.
        //Arrays.parallelPrefix();
        //basically, we want to map each character in the array to a datastructure
        // int idx, char c -> (int final_length, idx, c), where final_length = 3 if c = ' ' and 1 otherwise
        //then, we use parallel prefix to collect the indices to find their final position in the array
        //finally, we insert the character(s) into the final array in parallel. Note that the final array must be volatile
        //due to the possibility of processor caching

        Tuple2<Integer, Character>[] lengths = new Tuple2[length];
        // Work O(length) Span O(1)
        Arrays.parallelSetAll(lengths, idx -> {
            char val = in[idx];
            return Tuple2.make(val == ' ' ? 3 : 1, val);
        });

        // Work O(length) Span O(log(length))
        Arrays.parallelPrefix(lengths, (left, right) -> {
            // 5, "x y z    ", 9, "x%20y%20z"
            // initial:
            // (1, 0, x) (3, 1, _) (1, 2, y) (3, 3, _) (1, 4, z)
            // final :
            // (1, 0, x) (4, 1, _) (5, 4, y) (8, 5, _) (9, 8, z)
            int totalLength = left.getFirst() + right.getFirst();
            return Tuple2.make(totalLength, right.getSecond());
        });
        // copy into a new array for testing purposes
        char[] out = Arrays.copyOf(in, in.length);
        Arrays.stream(lengths).parallel()
                .forEach(lengthItem -> {
                    int totalLength = lengthItem.getFirst();
                    char insertChar = lengthItem.getSecond();
                    if(insertChar == ' ') {
                        int insertionIndex = totalLength - 3;
                        out[insertionIndex] = '%';
                        out[insertionIndex + 1] = '2';
                        out[insertionIndex + 2] = '0';
                    } else {
                        int insertionIndex = totalLength - 1;
                        out[insertionIndex] = insertChar;
                    }
                });
        return Tuple2.make(out, length == 0 ? 0 : lengths[length - 1].getFirst());
    }

    private List<Tuple2<Tuple2<char[], Integer>, Tuple2<char[], Integer>>> testCases = Tuple4.<Integer, String, Integer, String>of4(
            // input length, input string, output length, output string
            // note some strings exceed their expected length, and we expect the trailing characters to be unmodified
        4, "asdf    ", 4, "asdf    ",
            3, " xy__", 5, "%20xy",
            3, "xy %2", 5, "xy%20",
            7, "%20 %20  ", 9, "%20%20%20",
            5, "x y z    ", 9, "x%20y%20z",
            7, "x   yzw %20 _", 13, "x%20%20%20yzw",
            7, "xyz   w______%20 ", 13, "xyz%20%20%20w%20 ",
            0, "", 0, ""
    ).stream().map(testCase -> Tuple2.make(
            // args : char[], initialLength : int
            Tuple2.make(testCase.getSecond().toCharArray(), testCase.getFirst()),
            // result : char[], finalLength : int
            Tuple2.make(testCase.getFourth().toCharArray(), testCase.getThird())
    )).collect(Collectors.toUnmodifiableList());

    private List<Tuple2<String, BiFunction<char[], Integer, Tuple2<char[], Integer>>>> testFunctions = List.of(
            Tuple2.make("urlifyLinear", this::urlifyLinear),
            Tuple2.make("urlifyParallel", this::urlifyParallel)
    );
    @Override
    public void test() throws TestException {
        Testable.check(testFunctions, testCases, Objects::equals,
                //(expected, actual) ->
                //Objects.equals(expected.getSecond(), actual.getSecond()) && Arrays.equals(expected.getFirst(), actual.getFirst()),
                URLify.class, "did not correctly serialize URL");
    }
}
