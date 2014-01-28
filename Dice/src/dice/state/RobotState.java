package dice.state;

/** A game object that can have the ball.
 * @author Craig Wilkinson
 */
public class RobotState extends GameObject {
    private boolean hasBall;

    public RobotState(double xPos, double yPos) {
    	super(xPos, yPos);
    }

    public void setHasBall(boolean hasBall) {
    	this.hasBall = hasBall;
    }

    public boolean getHasBall() {
    	return hasBall;
    }

}
