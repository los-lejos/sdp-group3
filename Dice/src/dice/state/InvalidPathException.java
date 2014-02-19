package dice.state;

import java.lang.Exception;

public class InvalidPathException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidPathException() {
        super();
    }

    public InvalidPathException(String message) {
        super(message);
    }

}
