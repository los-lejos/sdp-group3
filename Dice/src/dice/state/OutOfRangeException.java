package dice.state;

import java.lang.Exception;

/** @author Craig Wilkinson
 */
public class OutOfRangeException extends Exception {
    public OutOfRangeException() {
    	super();
    }

    public OutOfRangeException(String message) {
    	super(message);
    }
}
