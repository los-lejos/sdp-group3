package dice.state;

/** A line class. Used to construct a path.
 * @author Craig Wilkinson
 */
public class BoundedLine extends Line {
    private Vector2 startPoint;
    private Vector2 endPoint;

    public BoundedLine(Vector2 startPoint, Vector2 endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    // may return Infinity
    public double getGradient() {
        return (endPoint.Y - startPoint.Y) /
               (endPoint.X - startPoint.X);
    }

    public double getYIntersect() {
        return startPoint.Y - (getGradient() * startPoint.X);
    }

    // check to see if a point is within the bounds of the line.
    // this doesn't actually check if the point is _on_ the line,
    // it just checks if it's in the bounding box of the line.
    // this is used in conjunction with the intersection of another
    // line, so after this we know whether the point lies on the line
    // or not anyway
    public boolean withinBounds(Vector2 point) {
        // return true if the X and Y coordinates of the point
        // are between those of the start and end points
        if ((point.X >= startPoint.X && point.X <= endPoint.X) ||
            (point.X <= startPoint.X && point.X >= endPoint.X)) {
            if ((point.Y >= startPoint.Y && point.Y <= endPoint.Y) ||
                (point.Y <= startPoint.Y && point.Y >= endPoint.Y)) {
                return true;
            }
        }

        return false;
    }
    
    public Vector2 getStartPoint() {
    	return startPoint;
    }
    
    public Vector2 getEndPoint() {
    	return endPoint;
    }
    
    // this function should only be called on a vertical line
    public double getXValueFromVertical() {
    	assert startPoint.X == endPoint.X;
    	
    	return startPoint.X;
    }

}
