package dice.state;

/** Class to represent position of an object
 * @author Craig Wilkinson
 */
public class Vector2 {
	public double X;
	public double Y;
	public double T;

	public Vector2(double X, double Y) {
		this.X = X;
		this.Y = Y;
    }

    public Vector2(double X, double Y, double T) {
    	this(X, Y);
    	this.T = T;
    }

    // copy values from other position
    public Vector2(Vector2 position) {
    	this.X = position.X;
    	this.Y = position.Y;
    }

    public void setX(double X) {
    	this.X = X;
    }

    public void setY(double Y) {
    	this.Y = Y;
    }

    public void setT(double T) {
    	this.T = T;
    }

    public void setPos(double X, double Y) {
    	this.X = X;
    	this.Y = Y;
    }

    public void setPos(Vector2 position) {
    	this.X = position.X;
    	this.Y = position.Y;
    }

}