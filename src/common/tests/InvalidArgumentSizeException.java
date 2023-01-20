package common.tests;

public class InvalidArgumentSizeException extends InvalidArgumentException{
    private final int parameterSize;
    private final int argumentsSize;
    public InvalidArgumentSizeException(String message, int parameterSize, int argumentsSize) {
        super(message);
        this.parameterSize = parameterSize;
        this.argumentsSize = argumentsSize;
    }

    public int getParameterSize() {
        return parameterSize;
    }

    public int getArgumentsSize() {
        return argumentsSize;
    }
}
