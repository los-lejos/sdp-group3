package dice.state;

/** The state of an opponent.
 * @author Craig Wilkinson
 */
public class RobotStateOpponent extends RobotState {
	// there are a whole load of protected variables
	// inherited from the RobotState class
	private float orientation;
	
	public RobotStateOpponent(float xPos, float yPos) {
		super(xPos, yPos);
    }

    public float getOrientationRads() {
        return orientation;
    }

    public float getOrientationDegs() {
        return (orientation * 360) / 2 * Math.pi; 
    }
}
