package dice.strategy;

import dice.Log;
import dice.communication.RobotCommunicationCallback;

public class IssuedAction {
	
	// After the timeout the action will be considered to be completed
	private final int TIMEOUT = 5000;
	
	private RobotCommunicationCallback callback;
	
	private boolean completed = false;
	private long issueTime;
	
	public IssuedAction() {
		this.issueTime = System.currentTimeMillis();
		
		callback = new RobotCommunicationCallback() {
			public void onError() {
				Log.logInfo("Action completion error");
				completed = true;
			}
			
			public void onTimeout() {
				Log.logInfo("Action timed out");
				completed = true;
			}
			
			public void onDone() {
				Log.logInfo("Action completed");
				completed = true;
			}
		};
	}
	
	public RobotCommunicationCallback getCallback() {
		return callback;
	}
	
	public boolean isCompleted() {
		long currentTime = System.currentTimeMillis();
		
		if(Math.abs(currentTime - issueTime) > TIMEOUT) {
			this.callback.onTimeout();
		}
		
		return completed;
	}
}
