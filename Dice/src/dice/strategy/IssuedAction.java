package dice.strategy;

import dice.Log;
import dice.communication.RobotCommunicationCallback;

public class IssuedAction {
	private RobotCommunicationCallback callback;
	
	private boolean completed = false;
	
	public IssuedAction() {
		callback = new RobotCommunicationCallback() {
			public void onError() {
				completed = true;
			}
			
			public void onTimeout() {
				completed = true;
			}
			
			public void onDone() {
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
