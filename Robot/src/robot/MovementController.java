package robot;


public abstract class MovementController {
	
	private enum State {
		READY, EXIT, MOVE, ROTATE, MOVE_LAT                                                                     
	}
	
	private MovementThread thread;

	private int heading, distance;
	
	private State currentState = State.READY;
	private State newState = State.READY;

    public void init() {
    	this.thread = new MovementThread();
    	this.thread.setDaemon(true);
    	this.thread.start();
    }
    
    // Abstract public interface methods
    public abstract boolean isMoving();
    public abstract void stop();
    public abstract void stopLateral();
    
    public abstract void setTrackWidth(int width);
    public abstract void setTravelSpeed(int speedPercentage);
    public abstract void setRotateSpeed(int speedPercentage);
    
    // Public interface for initiating movement
    public void move(int newDistance) {
		if(shouldChangeMovement(State.MOVE, this.distance, newDistance)) {
			this.distance = newDistance;
			
			// Convert to cm
			this.distance *= 10;
			this.newState = State.MOVE;
		}
    }
    
    public void rotate(int newHeading) {
    	if(newHeading > 180) {
    		newHeading -= 360;
		} else if(newHeading < -180) {
			newHeading += 360;
		}
		
		assert (newHeading >= -180) && (newHeading <= 180);

		if(shouldChangeMovement(State.ROTATE, this.heading, newHeading)) {
			this.heading = newHeading;
			this.newState = State.ROTATE;
		}
    }
    
    public void moveLat(int newDistance) {
    	// Only issue a new state if we are actually changing direction or movement type
		if(shouldChangeMovement(State.MOVE_LAT, this.distance, newDistance)) {
			this.distance = newDistance;
	    	this.newState = State.MOVE_LAT;
		}
    }
    
    private boolean shouldChangeMovement(State desiredState, int oldParam, int newParam) {
    	if(this.currentState != desiredState) {
			return true;
		} else {
			return (newParam <= 0 && oldParam >= 0) || (newParam >= 0 && oldParam <= 0);
		}
    }

    // Abstract methods that implement movement logic
    protected abstract void performRotate(int heading);
    protected abstract void performMove(int distance);
    protected abstract void performMoveLat(int distance);

    public void cleanup() {
    	this.newState = State.EXIT;
    }

	private class MovementThread extends Thread {

		public MovementThread() { }

		@Override
		public void run() {		
			while(currentState != State.EXIT) {
				if(newState != State.READY) {
					// Always stop regular movement
					stop();
					
					// Only stop lateral if we want to move laterally in a different direction
					if(newState == State.MOVE_LAT) {
						stopLateral();
					}

					currentState = newState;
					newState = State.READY;
					
					if(currentState == State.MOVE) {
						performMove(distance);
					} else if(currentState == State.ROTATE) {
						performRotate(heading);
					} else if(currentState == State.MOVE_LAT) {
						performMoveLat(distance);
					}
				} else if(!isMoving()) {
					currentState = State.READY;
					
					// If we are not being issued commands to do anything anymore and have
					// finished moving, just stop
					stop();
				}
			}
		}
	}
}
