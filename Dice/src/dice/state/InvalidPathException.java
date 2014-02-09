package dice.state;

import java.lang.Exception;

public class InvalidPathException extends Exception {
    public InvalidPathException() {
        super();
    }

    public InvalidPathException(String message) {
        super(message);
    }

}
