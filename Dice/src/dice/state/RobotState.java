package dice.state;

/** This class represents the state of a robot
 * @author Craig Wilkinson
 */
public abstract class RobotState {
    protected float xPos;
    protected float yPos;
    protected boolean hasBall;

    public RobotState(float xPos, float yPos) {
    	this.xPos = xPos;
    	this.yPos = yPos;
    }

    public void setPos(float xPos, float yPos) {
    	this.xPos = xPos;
    	this.yPos = yPos;
    }

    public boolean hasBall() {
    	return hasBall;
    }

    public void setHasBall(boolean hasBall) {
    	this.hasBall = hasBall;
    }

    // I have left these functions abstract because
    // I feel that it may be beneficial to return orientation
    // relative to facing the goals, mathematically
    abstract double getOrientationRads();

    abstract double getOrientationDegs();

}
