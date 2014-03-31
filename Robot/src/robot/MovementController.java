package robot;


public abstract class MovementController {
    
    // Abstract public interface methods
	public abstract void cleanup();
    public abstract boolean isMoving();
    public abstract void stop();
    public abstract void stopLateral();
    
    public abstract void setTrackWidth(int width);
    public abstract void setTravelSpeed(int speedPercentage);
    public abstract void setRotateSpeed(int speedPercentage);
    
    // Public interface for initiating movement
    public void move(int newDistance) {
    	this.stopLateral();
    	
		// Convert to cm
		newDistance *= 10;
		performMove(newDistance);
    }
    
    public void rotate(int newHeading) {
    	this.stopLateral();
    	
    	if(newHeading > 180) {
    		newHeading -= 360;
		} else if(newHeading < -180) {
			newHeading += 360;
		}
		
		assert (newHeading >= -180) && (newHeading <= 180);
	
		performRotate(newHeading);
    }
    
    public void moveLat(int newDistance) {
    	performMoveLat(newDistance);
    }

    // Abstract methods that implement movement logic
    protected abstract void performRotate(int heading);
    protected abstract void performMove(int distance);
    protected abstract void performMoveLat(int distance);

}
