package dice.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dice.communication.RobotCommunication;
import dice.state.WorldState;

/*
 * @author Joris S. Urbaitis
 */

public class RobotStrategyState {
	// Currently assigned action
	private StrategyAction action;
	
	// List of actions the robot can perform
	private List<StrategyAction> actions = new ArrayList<StrategyAction>();
	
	// List of actions that are possible in a given moment in time
	private List<StrategyAction> possibleActions = new ArrayList<StrategyAction>();

	public RobotStrategyState() {
	}
	
	public void addAction(StrategyAction action) {
		actions.add(action);
	}
	
	public StrategyAction getBestAction(WorldState state) {
		possibleActions.clear();
		
		for(StrategyAction action : this.actions) {
			if(action.isPossible(state)) {
				action.updateUtility(state);
				possibleActions.add(action);
			}
		}
		
		Collections.sort(possibleActions);
		return possibleActions.get(possibleActions.size() - 1);
	}
	
	public void setCurrentAction(StrategyAction action) {
		this.action = action;
		RobotCommunication.getInstance().sendInstruction(action.getInstruction());
	}
	
	public boolean needsNewAction(WorldState state) {
		return action == null || action.isCompleted() || !action.isPossible(state);
	}
}
