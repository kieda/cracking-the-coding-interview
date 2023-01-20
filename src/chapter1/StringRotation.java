package chapter1;

import common.tests.TestException;
import common.tests.Testable;
import common.tuple.Tuple2;
import common.tuple.Tuple3;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Assume you have a method isSubstring which checks if one word is a substring of another.
 * Given two strings, s1 and s2, write code to check if s2 is a rotation of s1 using only one call to isSubstring
 */
public class StringRotation implements Testable {
    public boolean isSubstring(CharSequence large, String subseq) {
        if(subseq.length() == 0)
            return true;
        char firstchar = subseq.charAt(0);
        for(int i = 0; i < large.length(); i++) {
            search:
            if(firstchar == large.charAt(i)) {
                for(int j = 1; j < subseq.length(); j++) {
                    if(i + j >= large.length() || large.charAt(i + j) != subseq.charAt(j)) {
                        break search;
                    }
                }
                return true;
            }
        }
        return false;
    }
    public boolean isRotation(String s1, String s2) {
        if(s1.length() != s2.length())
            return false;

        StringBuilder sb = new StringBuilder(s1.length() * 2);
        // abcdef
        // defabc

        // abcdefg
        // fgabcde
        // cdefgab
        // defgabc
        // efgabcd

        return isSubstring(sb.append(s1).append(s1), s2);
    }
    private final List<Tuple2<Tuple2<String, String>, Boolean>> testCases = Tuple3.of2(
        Tuple2.make("abcdef", "defabc"), true,
            Tuple2.make("abcdefg", "fgabcde"), true,
            Tuple2.make("abcdefg", "cdefgab"), true,
            Tuple2.make("abcdefg", "defgabc"), true,
            Tuple2.make("abcdefg", "efgabcd"), true,
            Tuple2.make("abcdefg", "cdefaab"), false,
            Tuple2.make("", ""), true,
            Tuple2.make("a", "a"), true
    );
    private final List<Tuple2<String, BiFunction<String, String, Boolean>>> testFunctions = List.of(
        Tuple2.make("isRotation", this::isRotation)
    );
    @Override
    public void test() throws TestException {
        Testable.check(testFunctions, testCases, StringRotation.class, "");
    }
}
