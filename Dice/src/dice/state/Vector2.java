package dice.state;

/** Class to represent position of an object
 * @author Craig Wilkinson
 */
public class Vector2 {
	public double X;
	public double Y;

	public Vector2(double X, double Y) {
		this.X = X;
		this.Y = Y;
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

    public void setPos(double X, double Y) {
    	this.X = X;
    	this.Y = Y;
    }

    public void setPos(Vector2 position) {
    	this.X = position.X;
    	this.Y = position.Y;
    }
    
    // get the euclidean distance from the object
    public double getEuclidean(Vector2 position) {
    	if(position == null) return 0;
    	
        return Math.sqrt(Math.pow(position.X - this.X, 2) +
                         Math.pow(position.Y - this.Y, 2));
    }

    @Override
    public String toString() {
        return "(" + X + "," + Y + ")";
    }
    
    public boolean equals(Vector2 other) {
    	return ((X == other.X) && (Y == other.Y));
    }

}
