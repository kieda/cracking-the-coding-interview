package common;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * I know it would be simpler to just use JUnit, but I thought it would be decent practice in writing a library
 * Plus tuples are a good representation for data storage
 */
public interface Testable {
    public void test() throws TestException;
    public static <Func, Arguments, Result> void check(
            // 2nd argument Object is unpacked via reflection into a function or predicate with multiple arguments.
            // Accepts one of the following:
            // * an object with a public method "test" that returns a boolean for predicate
            // * an object with a public method "apply" with one or more arguments for a function
            // * an object with a public method "get" with no arguments for a supplier
            List<Tuple2<String, Func>> testFunctions,  // <testName, testFunction> list
            // Arguments, if of the Tuple type, then will be unpacked and passed into the function
            // Use Tuple0 for supplier arguments
            List<Tuple2<Arguments, Result>> testCases,   // <arguments, result> list
            Class<?> problem,                            // the class that's running the tests
            String description                           // description of the error
    ) throws TestException{
        check(testFunctions, testCases, Objects::equals, problem, description);
    }

    public static <Func, Arguments, Result> void check(
            // 2nd argument Object is unpacked via reflection into a function or predicate with multiple arguments.
            // Accepts one of the following:
            // * an object with a public method "test" that returns a boolean for predicate
            // * an object with a public method "apply" with one or more arguments for a function
            // * an object with a public method "get" with no arguments for a supplier
            List<Tuple2<String, Func>> testFunctions,  // <testName, testFunction> list
            // Arguments, if of the Tuple type, then will be unpacked and passed into the function
            // Use Tuple0 for supplier arguments
            List<Tuple2<Arguments, Result>> testCases,   // <arguments, result> list
            BiPredicate<Result, Result> comparator,      // compares the expected and actual result
            Class<?> problem,                            // the class that's running the tests
            String description                           // description of the error
    ) throws TestException{
        for(Tuple2<String, Func> test : testFunctions) {
            System.out.println("Running: " + problem.getSimpleName() + "." + test.getFirst());
            int count = 0;
            for (Tuple2<Arguments, Result> testCase : testCases) {
                Object function = test.getSecond();

                List<Method> matches = Arrays.stream(function.getClass().getMethods())
                        .filter(method -> MethodCheckUtil.getDefaultInstance().acceptableMethod(method, testCase.getFirst(), testCase.getSecond()))
                        .collect(Collectors.toList());
                for(Method m : matches) {
                    Arguments arg = testCase.getFirst();
                    Result expected = testCase.getSecond();
                    Result actual;
                    try {
                        if(arg instanceof Tuple) {
                            // if this is a tuple, pass the args in as varargs
                            Object[] args = ((Tuple) arg).getItems();
                            actual = (Result)m.invoke(function, args);
                        } else {
                            // otherwise pass it in as a single item
                            actual = (Result)m.invoke(function, arg);
                        }
                    } catch (IllegalAccessException e) {
                        // might occur if there are security measures in place for the method.
                        // However we do check that the method is public which should mitigate most situations
                        e.printStackTrace();
                        throw new IllegalCallerException(e);
                    } catch (InvocationTargetException e) {
                        // should not happen - we checked the method before.
                        e.printStackTrace();
                        throw new IllegalArgumentException(e);
                    }
                    if(!comparator.test(expected, actual)) {
                        throw new TestException(description,
                                problem,
                                test.getFirst(),
                                testCase.getFirst(), expected, actual
                        );
                    }
                }
                count++;
            }
            System.out.println("Passed : " + count + "/" + testCases.size());
        }
    }
}
