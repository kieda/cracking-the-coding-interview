package common;

/**
 * Basically IllegalArgumentException, except it's thrown when doing an explicit check and is not a RuntimeException
 */
public class InvalidArgumentException extends Exception{
    public InvalidArgumentException(String message) {
        super(message);
    }
}
