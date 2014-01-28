package dice.state;

/** This class represents the state of objects
 * on the table (that can have positions)
 * @author Craig Wilkinson
 */
public abstract class GameObject {
    protected double xPos;
    protected double yPos;

    public GameObject(double xPos, double yPos) {
    	this.xPos = xPos;
    	this.yPos = yPos;
    }

    public void setPos(double xPos, double yPos) {
    	this.xPos = xPos;
    	this.yPos = yPos;
    }

    public void setX(double xPos) {
    	this.xPos = xPos;
    }

    public void setY(double yPos) {
    	this.yPos = yPos;
    }

    public double getX() {
    	return xPos;
    }

    public double getY() {
    	return yPos;
    }

    // I have left these functions abstract because
    // I feel that it may be beneficial to return orientation
    // relative to facing the goals, mathematically
    /*abstract double getOrientationRelativeTo(GameObject object);


    abstract double getOrientationDegs();*/
}
