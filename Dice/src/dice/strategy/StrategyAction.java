package dice.strategy;

import dice.communication.RobotCommunicationCallback;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;

/*
 * @author Joris S. Urbaitis
 */

public abstract class StrategyAction implements Comparable<StrategyAction>  {
	
	private RobotCommunicationCallback callback;
	protected RobotType target;
	
	private boolean completed = false;
	
	private int cachedUtility = 0;
	
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
	protected abstract int calculateUtility(WorldState state);
	public abstract RobotInstruction getInstruction(WorldState state);
	
	public void updateUtility(WorldState state) {
		this.cachedUtility = this.calculateUtility(state);
	}
	
	public int getCachedUtility() {
		return this.cachedUtility;
	}
	
	public RobotCommunicationCallback getCallback() {
		return callback;
	}
	
	public boolean isCompleted() {
		return completed;
	}

	public RobotType getTargetRobot() {
		return target;
	}
	
	@Override
    public int compareTo(StrategyAction a) {
		Integer a1Utility = this.getCachedUtility();
		Integer a2Utility = a.getCachedUtility();
        return a1Utility.compareTo(a2Utility);
    }
}
