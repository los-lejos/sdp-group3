package dice.state;

import java.lang.Math;

/** The state of an opponent.
 * @author Craig Wilkinson
 */
public class RobotStateOpponent extends RobotState {
	// there are a whole load of protected variables
	// inherited from the RobotState class
	private double orientation;
	
	public RobotStateOpponent(float xPos, float yPos) {
		super(xPos, yPos);
    }

    public double getOrientationRads() {
        return orientation;
    }

    public double getOrientationDegs() {
        return (orientation * 360) / 2.0 * Math.PI; 
    }
}
