package dice.state;

import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/** This class represents the state of objects
 * on the table (that can have positions)
 * @author Craig Wilkinson
 */
public class GameObject {
    // allow for a variation in possible position changes
    private static double DELTA = 200; 

    private List<Vector2> positions;
    private double rotation; // the rotation of the object relative
                             // to 'up' (on the camera)


    public GameObject() {
    	positions = new ArrayList<Vector2>();
    	System.out.println("Initializing object.");

    	this.rotation = 0;
    }
    
    public void setRotation(double rotation) {
    	this.rotation = rotation;
    }

    public void setPos(double xPos, double yPos, double t) {
    	Vector2 position = new Vector2(xPos, yPos);
        
        if (validatePos(position))
            positions.add(position);
    }

    public void setPos(Vector2 position) {
        if (validatePos(position))
            positions.add(position);
        }

    // decides if a new position for the object is viable given its
    // past positions
    private boolean validatePos(Vector2 position) {
        // only do the check if the new position isn't already
        // invalid
    	if (positions.size() > 2) {
	        if (position.X == -1) {
	            return false;
	        } else {
	        	return true;
	            /*Vector2 velocity = this.getVelocity();
	            if (velocity != null) {
	                double dt = position.T - getPos().T;
	
	                // run the projection function to get an estimate
	                // of the new position
	                double newX = position.X + getVelocity().X;
	                double newY = position.Y + getVelocity().Y;
	                Vector2 estimate = new Vector2(newX, newY);
	                double xDiff = Math.abs(position.X - estimate.X);
	                double yDiff = Math.abs(position.Y - estimate.Y);
	                if (xDiff > Math.abs(velocity.X) * DELTA ||
	                    yDiff > Math.abs(velocity.Y) * DELTA) {
	                    return false;
	                } else {
	                    return true;
	                }
	            } else {
	                // if the object is "new", then assume the position makes
	                // sense
	                return true;*/
	        }
        } else {
        	return true;
        }
    }

    // get the most recent position
    public Vector2 getPos() {
    	if (positions.size() > 0)
            return positions.get(positions.size() - 1);
        else
        	return null;
    }

    // project a path based on the rotation of the object
    // of course, this won't work for the ball. To project
    // the ball position, you should use the velocity
    public Path projectPath(WorldState world) {
        if (getPos() != null) {
	    	Path result = new Path();
	
	        result.addPoint(getPos());
	
	        double gradient = Math.atan(getRotation() - Math.PI / 2.0);
	        Line newLine = new UnboundedLine(getPos(), gradient);
	        Vector2 intersectionPoint = newLine.intersect(world.getTopLine());
	        if (intersectionPoint == null)
	            intersectionPoint = newLine.intersect(world.getBottomLine());
	        result.addPoint(intersectionPoint);
	
	        return result;
        } else {
        	return null;
        }
    }

    // project a path based on the velocity of the object
    // of course, this won't work for the ball. To project
    // the ball position, you should use the velocity
    public Path projectPathFromVelocity(WorldState world) {
        if (positions.size() > 2) {
	        Path result = new Path();
	
	        result.addPoint(getPos());
	        
	        Line line = new BoundedLine(getPos(), positions.get(positions.size() - 2));
	
	        double gradient = line.getGradient();
	        Line newLine = new UnboundedLine(getPos(), gradient);
	        Vector2 intersectionPoint = newLine.intersect(world.getTopLine());
	        if (intersectionPoint == null)
	            intersectionPoint = newLine.intersect(world.getBottomLine());
	        result.addPoint(intersectionPoint);
	
	        return result;
        } else {
        	System.err.println("Not enough points in object history: " + positions.size());
        	return null;
        }
    }

    // returns null if the object hasn't travelled for more than
    // two frames
    public Vector2 getVelocity() {
        if (positions.size() >= 2) {
            Vector2 lastPos = positions.get(positions.size() - 1);
            Vector2 nextLastPos = positions.get(positions.size() - 2);

            // crude method:
            // calculate the changes in X and Y values
            // over a time and project that forwards
            double dx = lastPos.X - nextLastPos.X;
            double dy = lastPos.Y - nextLastPos.Y;
            double dt = lastPos.T - nextLastPos.T;
            
            double vx = dx / dt;
            double vy = dy / dt;

            return new Vector2(vx, vy);
        } else {
            return null;
        }
    }


    // code to get the rotation. Currently awaiting vision from python
    // that contains rotation information
    public double getRotationRelativeTo(GameObject obj) {
        Vector2 myPos = this.getPos();
        Vector2 otherPos = obj.getPos();

        double yDiff = myPos.Y - otherPos.Y;
        double xDiff = otherPos.X - myPos.X;
        
        double theta = Math.PI / 2.0 - Math.atan2(yDiff, xDiff);

        if (yDiff < 0)
            return theta - rotation;
        else
            return rotation - theta;
    }

    // convert radians to degrees
    public static double asDegrees(double radians) {
        return 360 * (radians / (2.0 * Math.PI));
    }

    // relative to the top of the screen
    public double getRotation() {
        return rotation;
    }
    
    public Line getLineFromVelocity() {
    	return new UnboundedLine(getPos(), 0);
    }
    
}
