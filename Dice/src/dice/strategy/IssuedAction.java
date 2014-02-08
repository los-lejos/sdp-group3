package dice.strategy;

import dice.Log;
import dice.communication.RobotCommunicationCallback;

public class IssuedAction {
	private RobotCommunicationCallback callback;
	
	private boolean completed = false;
	
	public IssuedAction() {
		callback = new RobotCommunicationCallback() {
			public void onError() {
				Log.logInfo("Action error message recieved");
				completed = true;
			}
			
			public void onTimeout() {
				Log.logInfo("Action timeout message recieved");
				completed = true;
			}
			
			public void onDone() {
				Log.logInfo("Action done message recieved");
				completed = true;
			}
		};
	}
	
	public RobotCommunicationCallback getCallback() {
		return callback;
	}
	
	public boolean isCompleted() {
		return completed;
	}
}
