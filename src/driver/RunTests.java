package driver;

import chapter2.LinkedLists;
import common.tests.TestException;
import common.tests.Testable;

import java.util.List;

public class RunTests {
    private static List<Testable> tests = List.of(
        //new ArraysAndStrings(),
        new LinkedLists()
    );

    public static void main(String[] args) {
        for(Testable test : tests) {
            try {
                test.test();
            } catch (TestException e) {
                e.printStackTrace();
            }
        }
    }
}
