package robot;



public abstract class MovementController {
	
	private enum State {
		MOVING,
		ROTATING,
		STRAFING,
		IDLE
	}
	
	private State state;
	private boolean lastDir;

	public MovementController() {
	}
    
    // Abstract public interface methods
	public abstract void cleanup();
    public abstract boolean isMoving();
    public abstract boolean isDriving();
    public abstract boolean isStrafing();
    public abstract void stop();
    public abstract void stopLateral();
    
    public abstract void setTrackWidth(int width);
    public abstract void setTravelSpeed(int speedPercentage);
    public abstract void setRotateSpeed(int speedPercentage);
    
    // Public interface for initiating movement
    public void move(int newDistance) {
    	if(!this.isMoving()) {
    		this.state = State.IDLE;
    	}

    	this.stopLateral();

    	boolean newDir = newDistance > 0;
		this.state = State.MOVING;
		
		if(this.isDriving() && this.lastDir == newDir) {
    		return;
    	}
		
		this.lastDir = newDir;
    	
		// Convert to cm
		newDistance *= 10;
		performMove(newDistance);
    }
    
    public void rotate(int newHeading) {
    	if(this.isStrafing()) {
    		if(Math.abs(newHeading) > 12) {
    			return;
    		}
    	}
    		
    	if(!this.isMoving()) {
    		this.state = State.IDLE;
    	}

    	if(newHeading > 180) {
    		newHeading -= 360;
		} else if(newHeading < -180) {
			newHeading += 360;
		}
		
		assert (newHeading >= -180) && (newHeading <= 180);

		boolean newDir = newHeading > 0;
		if(newDir != lastDir || this.state != State.ROTATING) {
			this.lastDir = newDir;
			this.state = State.ROTATING;
			
	    	this.stopLateral();

			performRotate(newHeading);
		}
    }
    
    public void moveLat(int newDistance) {
    	if(!this.isMoving()) {
    		this.state = State.IDLE;
    	}

    	boolean newDir = newDistance > 0;
    	
    	if(this.isStrafing() && newDir == this.lastDir) {
    		return;
    	}
    	
    	this.stop();
    	this.lastDir = newDir;
		this.state = State.STRAFING;
    	
    	performMoveLat(newDistance);
    }

    // Abstract methods that implement movement logic
    protected abstract void performRotate(int heading);
    protected abstract void performMove(int distance);
    protected abstract void performMoveLat(int distance);

}
