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
    private static double DELTA = 0.1; 

    private List<Vector2> positions;
    private double rotation; // the rotation of the object relative
                             // to 'up' (on the camera)

    public GameObject(double xPos, double yPos, double rotation) {
    	// add the first position
    	Vector2 position = new Vector2(xPos, yPos);
    	positions = new ArrayList<Vector2>();
    	positions.add(position);

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
        Vector2 velocities = this.getSpeed();
        if (velocities != null) {
            double dt = position.T - getPos().T;

            // run the projection function to get an estimate
            // of the new position
            Vector2 estimate = projectVector2(dt);
            double xDiff = Math.abs(position.X - estimate.X);
            double yDiff = Math.abs(position.Y - estimate.Y);
            if (xDiff > Math.abs(velocities.X) * DELTA ||
                yDiff > Math.abs(velocities.Y) * DELTA) {
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

    // get the most recent position
    public Vector2 getPos() {
    	if (positions.size() > 0)
            return positions.get(positions.size() - 1);
        else
        	return null;
    }

    // get the projected position t milliseconds from
    // now. Obviously this is less likely to be correct
    // further in the future.
    // Obviously, this doesn't return a time component because it
    // hasn't happened.
    //
    // returns null if there aren't enough positions taken yet
    public Vector2 projectVector2(double t) {
        Vector2 velocities = this.getSpeed();
        if (velocities != null) {
            Vector2 lastPos = positions.get(positions.size() - 1);
            double newX = lastPos.X + velocities.X * t;
            double newY = lastPos.Y + velocities.Y * t;

            return new Vector2(newX, newY);
        } else {
            return null;
        }
    }

    // this returns a "position" which is really just a 2D
    // vector representing the X and Y velocities
    // returns null if the object hasn't travelled for more than
    // two frames
    private Vector2 getSpeed() {
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
}
