package robot;


public abstract class MovementController {
	
	private enum State {
		READY, EXIT, MOVE, ROTATE, MOVE_LAT                                                                     
	}
	
	private final MovementThread thread;

	private int heading, distance;

    public MovementController() {
    	this.thread = new MovementThread();
    	this.thread.setDaemon(true);
    	this.thread.start();
    }
    
    // Abstract public interface methods
    public abstract boolean isMoving();
    public abstract void stop();
    
    public abstract void setTrackWidth(int width);
    public abstract void setTravelSpeed(int speedPercentage);
    public abstract void setRotateSpeed(int speedPercentage);
    
    // Public interface for initiating movement
    public void move(int distance) {
		this.distance = distance;
		
		// Convert to cm
		this.distance *= 10;
		this.thread.setState(State.MOVE);
    }
    
    public void rotate(int heading) {
    	if(heading > 180) {
			heading -= 360;
		} else if(heading < -180) {
			heading += 360;
		}
		
		assert (heading >= -180) && (heading <= 180);
		
		this.heading = heading;
		this.thread.setState(State.ROTATE);
    }
    
    public void moveLat(int distance) {
    	this.distance = distance;
    	this.thread.setState(State.MOVE_LAT);
    }

    // Abstract methods that implement movement logic
    protected abstract void performRotate(int heading);
    protected abstract void performMove(int distance);
    protected abstract void performMoveLat(int distance);

    public void cleanup() {
    	this.thread.setState(State.EXIT);
    }

	private class MovementThread extends Thread {
		
		private State currentState = State.READY;
		private State newState = State.READY;

		public MovementThread() { }
		
		public void setState(State newState) {
			this.newState = newState;
		}

		@Override
		public void run() {		
			while(currentState != State.EXIT) {
				if(currentState == State.MOVE) {
					performMove(distance);
				} else if(currentState == State.ROTATE) {
					performRotate(heading);
				} else if(currentState == State.MOVE_LAT) {
					performMoveLat(distance);
				}
				
				currentState = State.READY;
				
				if(newState != State.READY) {
					currentState = newState;
					newState = State.READY;
				}
			}
		}
	}
}
