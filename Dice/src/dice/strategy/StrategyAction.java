package dice.strategy;

import dice.communication.RobotCommunicationCallback;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;

/*
 * @author Joris S. Urbaitis
 */

public abstract class StrategyAction {
	
	private RobotCommunicationCallback callback;
	private RobotType target;
	
	private boolean completed = false;
	
	public StrategyAction(RobotType target) {
		this.target = target;

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

	public abstract boolean isPossible(WorldState state);
	public abstract int calculateUtility(WorldState state);
	public abstract RobotInstruction getInstruction();
	
	public RobotCommunicationCallback getCallback() {
		return callback;
	}
	
	public boolean isCompleted() {
		return completed;
	}
	
	public RobotType getTargetRobot() {
		return target;
	}
}
