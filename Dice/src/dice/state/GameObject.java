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
        Vector2 velocity = this.getVelocity();
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

    // this returns a "position" which is really just a 2D
    // vector representing the X and Y velocities
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
}
