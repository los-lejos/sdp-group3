package dice.state;

import java.lang.Math;

/** Just some utilities for converting between angle representations.
 * 
 * @author Craig Wilkinson
 * 
 */
public class AngleMaths {
	
	/** Converts the WorldState representation of an angle
	 * (clockwise from Y axis) to one appropriate for use with
	 * gradients and tan (anticlockwise from X axis).
	 * @param input: input angle in radians
	 */
	public static double yToX(double input) {
		return (-1.0 * input) - (Math.PI / 2.0);
	}
	
	/** Converts a normal representation of an angle
	 * (anticlockwise from X axis) to the way that the WorldState
	 * represents angles (clockwise from Y axis)
	 * @param input
	 * @return
	 */
	public static double xToY(double input) {
		return -1.0 * (input + Math.PI / 2.0);
	}
	
	/** Converts the angle that is received from the vision
	 * to the representation used by WorldState. Similar to
	 * xToY but also changes the "all positive" approach of the
	 * vision to the negative anti-clockwise positive clockwise
	 * 
	 */
	public static double visionToWorldState(double input) {
		// In vision, the angle is relative to +X
		// In dice, it's relative to +Y

		double result = (input + Math.PI / 2);
		if (result > Math.PI) {
			result = -1 * (Math.PI * 2 - result);
		}
		
		return result;
	}
}
