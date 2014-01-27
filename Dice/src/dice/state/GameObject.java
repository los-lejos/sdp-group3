package dice.state;

/** This class represents the state of objects
 * on the table (that can have positions)
 * @author Craig Wilkinson
 */
public abstract class GameObject {
    protected float xPos;
    protected float yPos;

    public GameObject(float xPos, float yPos) {
    	this.xPos = xPos;
    	this.yPos = yPos;
    }

    public void setPos(float xPos, float yPos) {
    	this.xPos = xPos;
    	this.yPos = yPos;
    }

    public void setX(float xPos) {
    	this.xPos = xPos;
    }

    public void setY(float yPos) {
    	this.yPos = yPos;
    }

    public float getX() {
    	return xPos;
    }

    public float getY() {
    	return yPos;
    }

    // I have left these functions abstract because
    // I feel that it may be beneficial to return orientation
    // relative to facing the goals, mathematically
    /*abstract double getOrientationRelativeTo(GameObject object);


    abstract double getOrientationDegs();*/
}
