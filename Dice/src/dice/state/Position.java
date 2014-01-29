package dice.state;

/** Class to represent position of an object
 * @author Craig Wilkinson
 */
public class Position {
	public double X;
	public double Y;
	public double T;

	public Position(double X, double Y) {
		this.X = X;
		this.Y = Y;
    }

    public Position(double X, double Y, double T) {
    	this(X, Y);
    	this.T = T;
    }

    // copy values from other position
    public Position(Position position) {
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

    public void setPos(Position position) {
    	this.X = position.X;
    	this.Y = position.Y;
    }

}
