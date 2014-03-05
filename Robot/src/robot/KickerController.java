package robot;

public abstract class KickerController {
	
	private enum KickerState {
		READY, KICK, GRAB, EXIT
	}
	
	private KickerState state = KickerState.READY;
	private KickerThread kickerThread;
	
	private boolean hasBall = false;
	
	public KickerController() {
		kickerThread = new KickerThread();
		kickerThread.setDaemon(true);
		kickerThread.start();
	}
	
	public void cleanup() {
		this.state = KickerState.EXIT;
	}
	
	public boolean getHasBall() {
		return hasBall;
	}
	
	public boolean isMoving() {
		return state != KickerState.READY;
	}
	
	public void kick() {
		this.state = KickerState.KICK;
	}

	public void grab() {
		this.state = KickerState.GRAB;
	}

	private class KickerThread extends Thread {
		
		@Override
		public void run() {
			try {
				performOpen();
	
				while (state != KickerState.EXIT) {
					if (state == KickerState.KICK) {
						performKick();
						hasBall = false;
						state = KickerState.READY;
					} else if (state == KickerState.GRAB) {
						performGrab();
						hasBall = true;
						state = KickerState.READY;
					}
				}

				performGrab();
			} catch (InterruptedException e) {
				// Exit if we were interrupted
			}
		}
	}
	
	protected abstract void performGrab() throws InterruptedException;
	protected abstract void performKick() throws InterruptedException;
	protected abstract void performOpen() throws InterruptedException;
}
