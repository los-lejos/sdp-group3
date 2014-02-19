package dice.state;

import java.lang.Exception;

/** @author Craig Wilkinson
 */
public class OutOfRangeException extends Exception {

	private static final long serialVersionUID = 1L;

	public OutOfRangeException() {
    	super();
    }

    public OutOfRangeException(String message) {
    	super(message);
    }
}
