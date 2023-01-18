package common;

import java.util.List;

public interface TestSuite extends Testable{
    public List<Testable> getTests();
    @Override
    public default void test() throws TestException {
        for(Testable test : getTests()) {
            test.test();
        }
    }
}
