package robot;

public abstract class KickerController {
	
	private enum KickerState {
		READY, KICK, GRAB, OPEN
	}
	
	private KickerState state = KickerState.READY;
	private KickerState newState = KickerState.READY;
	private KickerThread kickerThread;

	private boolean hasBall = false;
	private boolean isRunning = true;

	public void init() {
		kickerThread = new KickerThread();
		kickerThread.setDaemon(true);
		kickerThread.start();
	}
	
	public void cleanup() {
		this.isRunning = false;
	}
	
	public boolean getHasBall() {
		return hasBall;
	}
	
	public boolean isMoving() {
		return state != KickerState.READY;
	}
	
	public void kick() {
		this.newState = KickerState.KICK;
	}

	public void grab() {
		this.newState = KickerState.GRAB;
	}
	
	public void open() {
		this.newState = KickerState.OPEN;
	}

	private class KickerThread extends Thread {
		
		@Override
		public void run() {
			try {
				performOpen();
	
				while (isRunning) {
					if (state == KickerState.KICK) {
						performKick();
						hasBall = false;
					} else if (state == KickerState.GRAB) {
						performGrab();
						hasBall = true;
					} else if(state == KickerState.OPEN) {
						performOpen();
						hasBall = false;
					}
					
					state = KickerState.READY;
					
					if(newState != KickerState.READY) {
						state = newState;
						newState = KickerState.READY;
					}
				}

				performGrab();
				stop();
			} catch (InterruptedException e) {
				// Exit if we were interrupted
			}
		}
	}

	protected abstract void stop();
	protected abstract void performGrab() throws InterruptedException;
	protected abstract void performKick() throws InterruptedException;
	protected abstract void performOpen() throws InterruptedException;
}
