package chapter1;

import common.tests.TestSuite;
import common.tests.Testable;

import java.util.List;

public class ArraysAndStrings implements TestSuite {
    @Override
    public List<Testable> getTests() {
        return List.of(
            new IsUnique(),
            new CheckPermutation(),
            new URLify(),
            new OneAway(),
            new PalindromePermutation(),
            new StringCompression(),
            new RotateMatrix(),
            new StringRotation(),
            new ZeroMatrix()
        );
    }
}
