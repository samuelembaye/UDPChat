package Greenest;

// Base exception for all Greenest program exceptions
public class GreenestException extends Exception {
    public GreenestException(String message) {
        super(message);
    }

    public GreenestException(String message, Throwable cause) {
        super(message, cause);
    }
}

