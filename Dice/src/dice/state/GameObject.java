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
    private static double DELTA = 3; 

    private List<Vector2> positions;
    private List<Double> rotations; // the rotation of the object relative
                             // to 'up' (on the camera)


    public GameObject() {
    	positions = new ArrayList<Vector2>();
	rotations = new ArrayList<Double>();
    	System.out.println("Initializing object.");

    	this.rotations.add(0.0);
    }
    
    public void setRotation(double rotation) {
        // representation of rotation as given by vision returns value in
        // radians measured clockwise from the left
    	double result = (rotation - Math.PI / 2.0) % (Math.PI * 2.0);
    	this.rotations.add(result);
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
    private boolean validatePos(Vector2 newPosition) {
        // only do the check if the new position isn't already
        // invalid
    	if (positions.size() > 2) {
	        if (newPosition.X == -1) {
	            return false;
	        } else {
	            Vector2 velocity = this.getVelocity();
	            if (velocity != null) {

	                // run the projection function to get an estimate
	                // of the new position
	                double newX = newPosition.X + getVelocity().X;
	                double newY = newPosition.Y + getVelocity().Y;
	                Vector2 estimate = new Vector2(newX, newY);
	                double xDiff = Math.abs(newPosition.X - estimate.X);
	                double yDiff = Math.abs(newPosition.Y - estimate.Y);
	                if (xDiff > Math.abs(velocity.X) * DELTA ||
	                    yDiff > Math.abs(velocity.Y) * DELTA) {
                        System.err.println("Unreasonable position. Currently at " + String.valueOf(getPos().X) + "," + String.valueOf(getPos().Y) + " and trying to update position to " + String.valueOf(newPosition.X) + "," + String.valueOf(newPosition.Y) + ".");
	                    return false;
	                } else {
	                    return true;
	                }
	            } else {
	                // if the object is "new", then assume the position makes
	                // sense
	                return true;
                }
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
            
            // currently just the velocity in pixels per frame.
            // far from ideal
            return new Vector2(dx, dy);
        } else {
            return null;
        }
    }

    // get the rotation relative to another object in radians
    public double getRotationRelativeTo(GameObject obj) {
        return getRotationRelativeTo(obj.getPos());
    }

    // get the rotation relative to another position in radians
    public double getRotationRelativeTo(Vector2 otherPos) {
        Vector2 myPos = this.getPos();

        double yDiff = otherPos.Y - myPos.Y;
        double xDiff = otherPos.X - myPos.X;
        
        double phi;
        double theta = Math.PI / 2.0 - Math.atan2(yDiff, xDiff);

        if (theta < 0)
        	theta = Math.PI * 2 + theta;
        
        if (getRotation() > theta) {
        	if (getRotation() - theta > Math.PI) {
        		return Math.PI * 2 - (getRotation() - theta);
        		
        	} else {
        		return theta - getRotation();
        	}
        } else {
        	if (theta - getRotation() > Math.PI) {
        		return -1 * (Math.PI * 2 - (theta - getRotation()));
        	} else {
        		return theta - getRotation();
        	}
        }
    }

    // get the euclidean distance to the object
    public double getEuclidean(GameObject obj) {
        return getEuclidean(obj.getPos());
    }

    // get the euclidean distance from the object
    public double getEuclidean(Vector2 position) {
        return Math.sqrt(Math.pow(position.X - getPos().X, 2) +
                         Math.pow(position.Y - getPos().Y,2));
    }

    // convert radians to degrees
    public static double asDegrees(double radians) {
        return 360 * (radians / (2.0 * Math.PI));
    }

    // relative to the top of the screen
    public double getRotation() {
        return rotations.get(rotations.size() - 1);
    }
    
    public Line getLineFromVelocity() {
    	Vector2 newPos = new Vector2(getPos().X + getVelocity().X, getPos().Y + getVelocity().Y);
    	BoundedLine speedVector = new BoundedLine(getPos(), newPos);
    	return new UnboundedLine(getPos(), speedVector.getGradient());
    }
    
    public double getSpeed() {
    	double result = Math.sqrt(Math.pow(getVelocity().X,2)+ Math.pow(getVelocity().Y,2));
    	return result;
    }
    
}
