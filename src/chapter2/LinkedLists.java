package chapter2;

import common.TestSuite;
import common.Testable;

import java.util.List;

public class LinkedLists implements TestSuite {
    @Override
    public List<Testable> getTests() {
        return List.of(
            new SumLists(),
            Palindrome.INSTANCE,
            KthToLast.INSTANCE,
            DeleteMiddleNode.INSTANCE,
            Intersection.INSTANCE
        );
    }
}
