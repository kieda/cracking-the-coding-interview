package common.tests;

import java.lang.reflect.Type;

public class InvalidArgumentTypeException extends InvalidArgumentException{
    // the type that we're expecting from the parameter that we're currently checking.
    // This may include bounds or types of arrays, so this might be more specific than parameter.getParameterizedType()
    private final Type expected;
    // the type that we're passing in from the object that we're currently checking.
    // This may include bounds or types of arrays, so this might be more specific than argument.getClass()
    private final Type actual;

    public InvalidArgumentTypeException(String message, Type expected, Type actual) {
        super(message);
        this.expected = expected;
        this.actual = actual;
    }

    public Type getExpectedType() {
        return expected;
    }

    public Type getActualType() {
        return actual;
    }
}
