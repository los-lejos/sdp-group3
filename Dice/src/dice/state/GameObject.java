package dice.state;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.ListIterator;
import java.lang.Math;

import dice.Log;
import dice.state.WorldState.PitchZone;

/** This class represents the state of objects
 * on the table (that can have positions)
 * @author Craig Wilkinson
 */
public class GameObject {
    // allow for a variation in possible position changes
	private class Rotation {
		private long timestamp;
		private double angle;
		public Rotation(double angle) {
			this.angle = angle;
			this.timestamp = System.currentTimeMillis();
		}
		public long getTimestamp() {
			return timestamp;
		}
		public double getAngle() {
			return angle;
		}
	}
	private static class RotationComparator implements Comparator<Rotation> {
		@Override
		public int compare(Rotation a, Rotation b) {
			return a.getAngle() < b.getAngle() ? -1 : a.getAngle() == b.getAngle() ? 0 : 1;
		}
	}
	private class LimitedList<E> extends LinkedList<E> {
	    private final int limit;
	    public LimitedList(int limit) {
	        this.limit = limit;
	    }
	    @Override
	    public boolean add(E o) {
	        super.add(o);
	        while (size() > limit) { super.remove(); }
	        return true;
	    }
	}
    private static double POSITION_VALIDATION_THRESH = 3;
    private static int MAX_ROTATIONS = 30;
    private static int MAX_POSITIONS = 100;
    private static int ROTATION_VALIDATION_COUNT = 1;
    private static int ROTATION_VALIDATION_MIN_COUNT = 15;
    private static double ROTATION_VALIDATION_THRESH = Math.PI / 3;
    private static long ROTATION_TIMEOUT = 3000; // in ms

    private LimitedList<Vector2> positions;
    private LimitedList<Rotation> rotations; // the rotation of the object relative
                          // to 'up' (on the camera)

    private PitchZone currentZone;

    public GameObject() {
    	positions = new LimitedList<Vector2>(MAX_POSITIONS);
    	rotations = new LimitedList<Rotation>(MAX_ROTATIONS);
    	System.out.println("Initializing object.");

    	this.rotations.add(new Rotation(0.0));
    }
    
    public PitchZone getCurrentZone() {
    	return currentZone;
    }
    
    public void setCurrentZone(PitchZone currentZone) {
    	this.currentZone = currentZone;
    }
    
    public void setRotation(double rotation) {
    	if (validateRotation(rotation)) {
    		this.rotations.add(new Rotation(rotation));
    	} else {
    		System.out.println("Invalid rotation value: " + rotation);
    	}
    }
    
    public boolean validateRotation(double rotation) {
    	ArrayList<Rotation> lastFew = getLastNRotations(rotations);
    	if (lastFew.size() >= ROTATION_VALIDATION_MIN_COUNT) {
    		// get the median of the last rotations
    		double medianRotation = getMedianRotation(lastFew);
    		// only accept the rotation if it is closeish to the median
    		// of the last few rotations
    		return (Math.abs(rotation - medianRotation) < ROTATION_VALIDATION_THRESH);
    	} else {
    		return true;
    	}
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
    	// if the position isn't already the
    	// "invalid" position
	    if (newPosition.X < -1 || newPosition.Y == -1) {
            return false;
        } else {
            Vector2 velocity = getVelocity();
            
            // if the object can have a velocity (has had more than
            // one positions)
            if (velocity != null) {
                return isProjectedPosReasonable(newPosition);
            } else {
                return true;
            }
        }
    }
    
    // takes a new position and returns true if the new position
    // is reasonable given the objects current velocity and position
    // ("reasonable" here means within bounds controlled by the
    // POSITION_VALIDATION_THRESH constant)
    private boolean isProjectedPosReasonable(Vector2 newPosition) {
    	Vector2 velocity = getVelocity();
        double newX = newPosition.X + getVelocity().X;
        double newY = newPosition.Y + getVelocity().Y;
        
        Vector2 estimate = new Vector2(newX, newY);
        
        // if the difference between the projected estimate
        // and the actual new position is too large,
        // return false and report the positions
        double xDiff = Math.abs(newPosition.X - estimate.X);
        double yDiff = Math.abs(newPosition.Y - estimate.Y);
        if (xDiff > Math.abs(velocity.X) * POSITION_VALIDATION_THRESH ||
            yDiff > Math.abs(velocity.Y) * POSITION_VALIDATION_THRESH) {
    
        	Log.logInfo("Unreasonable position. Currently at " + 
				    getPos().X + "," + getPos().Y +
				    " and trying to update position to " +
				    newPosition.X + "," + newPosition.Y + ".");
        	
        	return false;
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
    // (used for the ball, since the ball has no "rotation")
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

    /** get the rotation relative to another position in radians
     * 
     * @param otherPos
     * @return 
     */
    public double getRotationRelativeTo(Vector2 otherPos) {
        Vector2 myPos = this.getPos();
        
        if(myPos == null || otherPos == null) {
        	return 0;
        }

        double yDiff = otherPos.Y - myPos.Y;
        double xDiff = otherPos.X - myPos.X;
        
        // atan2 measures anticlockwise from +X.
        // we want clockwise from +Y
        double theta = -1 * Math.atan2(yDiff, xDiff) + Math.PI / 2.0;
        if (theta > Math.PI) {
        	theta = -2.0 * Math.PI + theta;
        }
        
        double myRotation = getRotation();

        return theta - myRotation;
    }

    // get the euclidean distance to the object
    public double getEuclidean(GameObject obj) {
        return getEuclidean(obj.getPos());
    }

    // get the euclidean distance from the object
    public double getEuclidean(Vector2 position) {
    	if (position != null && getPos() != null) {
	        return Math.sqrt(Math.pow(position.X - getPos().X, 2) +
	                         Math.pow(position.Y - getPos().Y, 2));
    	} else {
    		return 0.0;
    	}
    }

    // relative to the top of the screen
    public double getRotation() {
    	if (rotations.size() > 0) {
    		return rotations.get(rotations.size() - 1).getAngle();
    	} else {
    		return 0;
    	}
    }
    
    // simply projects a straight line f
    public Line getLineFromVelocity() {
    	if (getPos() != null && getVelocity() != null) {
	    	double dx = getPos().X + getVelocity().X;
	    	double dy = getPos().Y + getVelocity().Y;
	    	
	    	// create the next predicted position
	    	Vector2 newPos = new Vector2(dy, dx);
	    	
	    	BoundedLine speedVector = new BoundedLine(getPos(), newPos);
	    	return new UnboundedLine(getPos(), speedVector.getGradient());
    	} else {
    		return null;
    	}
    }
    
    // get the speed as a scalar
    public double getSpeed() {
    	if (getVelocity() != null) {
    		return Math.sqrt(Math.pow(getVelocity().X,2)+ Math.pow(getVelocity().Y,2));
    	} else {
    		return 0;
    	}
    }
    
    private static ArrayList<Rotation> getLastNRotations(LimitedList<Rotation> rotations) {
    	ListIterator<Rotation> li = rotations.listIterator(rotations.size());
    	// Iterate in reverse.
    	ArrayList<Rotation> lastFew = new ArrayList();
    	long timeNow = System.currentTimeMillis();
    	while(li.hasPrevious()) {
    		Rotation rotation = li.previous();
    		if (timeNow - rotation.getTimestamp() > ROTATION_TIMEOUT && lastFew.size() < ROTATION_VALIDATION_THRESH) {
    			lastFew.add(rotation);
    		}
    	}
    	return lastFew;
    }
    private static double getMedianRotation(List<Rotation> newrotations) {
    	double result;
    	
    	Collections.sort(newrotations, new RotationComparator());
    	if (newrotations.size() % 2 == 0) {
    		result = (newrotations.get((int) Math.floor(newrotations.size() / 2.0)).getAngle() +
    			   newrotations.get((int) Math.ceil(newrotations.size() / 2.0)).getAngle())
    			   / 2.0;
    	} else {
    		result = newrotations.get((int) Math.floor(newrotations.size() / 2.0)).getAngle(); 
    	}
    	    	
    	return result;
    }
}
