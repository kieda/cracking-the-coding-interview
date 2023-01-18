package common;

public class TestException extends Exception{
    private final Class<?> testClass;
    private final String testName;
    private final Object input;
    private final Object expected;
    private final Object actual;

    public TestException(Class<?> testClass,
                         String testName,
                         Object input, Object expected, Object actual) {
        this(null, testClass, testName, input, expected, actual);
    }
    /**
     * Creates an exception that should be thrown if a test fails a check
     * @param reason general reason why this test has failed
     * @param testClass the caller's class
     * @param testName the name of the test or function within the class
     * @param input input argument(s) to the function
     * @param expected expected output
     * @param actual actual output from the function
     */
    public TestException(String reason,
         Class<?> testClass,
         String testName,
         Object input, Object expected, Object actual) {
        super(reason);
        this.testClass = testClass;
        this.testName = testName;
        this.input = input;
        this.expected = expected;
        this.actual = actual;
    }

    private String generatedMessage = null;

    @Override
    public String getMessage() {
        if(generatedMessage == null) {
            String reason = super.getMessage();
            String inputString = (input instanceof Tuple) ? input.toString() : "(" + input + ")";
            generatedMessage = testClass.getSimpleName() + ":" + (reason != null ? " " + reason : "") + "\n" +
                    "expected: " + testName + inputString + " = " + expected + "\n" +
                    "actual  : " + testName + inputString + " = " + actual;
        }

        return generatedMessage;
    }
}
