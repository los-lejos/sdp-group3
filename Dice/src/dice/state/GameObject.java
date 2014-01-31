package dice.state;

import java.util.List;
import java.util.ArrayList;

/** This class represents the state of objects
 * on the table (that can have positions)
 * @author Craig Wilkinson
 */
public class GameObject {
    private List<Position> positions;
    private double rotation; // the rotation of the object relative
                             // to 'up' (on the camera)
    private boolean hasBall;

    public GameObject(double xPos, double yPos, double rotation) {
    	// add the first position
    	Position position = new Position(xPos, yPos);
    	positions = new ArrayList<Position>();
    	positions.add(position);

    	this.hasBall = false;
    	this.rotation = rotation;
    }

    public void setPos(double xPos, double yPos, double t) {
    	Position position = new Position(xPos, yPos);
    	positions.add(position);
    }

    public void setPos(Position position) {
    	positions.add(position);
    }

    public void setHasBall(boolean hasBall) {
    	this.hasBall = hasBall;
    }

    public boolean getHasBall() {
    	return hasBall;
    }

    // get the most recent position
    public Position getPos() {
    	if (positions.size() > 0)
            return positions.get(positions.size() - 1);
        else
        	return new Position(-1, -1); // essentially a failure
    }

    // get the projected position t milliseconds from
    // now. Obviously this is less likely to be correct
    // further in the future.
    // Obviously, this doesn't return a time component because it
    // hasn't happened.
    //
    // returns null if there aren't enough positions taken yet
    public Position projectPosition(double t) {
        if (positions.size() >= 2) {
            Position lastPos = positions.get(positions.size() - 1);
            Position nextLastPos = positions.get(positions.size() - 2);

            // crude method:
            // calculate the changes in X and Y values
            // over a time and project that forwards
            double dx = lastPos.X - nextLastPos.X;
            double dy = lastPos.Y - nextLastPos.Y;
            double dt = lastPos.T - nextLastPos.T;
            
            double vx = dx / dt;
            double vy = dy / dt;
            double newX = lastPos.X + vx * t;
            double newY = lastPos.Y + vy * t;

            return new Position(newX, newY);
        } else
            return new Position(-1, -1); // essentially a failure
    }


    // code to get the rotation. Currently awaiting vision from python
    // that contains rotation information
    public double getRotationRelativeTo(GameObject obj) {
        Position myPos = this.getPos();
        Position otherPos = obj.getPos();

        double yDiff = otherPos.Y - myPos.Y;
        double xDiff = otherPos.X - myPos.X;
        
        double theta = Math.atan2(yDiff, xDiff);

        return (Math.PI / 2.0) - theta - rotation;
    }
}
