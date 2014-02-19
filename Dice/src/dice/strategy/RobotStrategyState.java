package dice.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dice.Log;
import dice.communication.RobotCommunicator;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;

/**
 * @author Joris S. Urbaitis
 */

public class RobotStrategyState {
	// Currently assigned action
	private IssuedAction issuedAction;
	private StrategyAction strategyAction;
	
	// List of actions the robot can perform
	private List<StrategyAction> actions = new ArrayList<StrategyAction>();
	
	// List of actions that are possible in a given moment in time
	private List<StrategyAction> possibleActions = new ArrayList<StrategyAction>();
	
	private RobotCommunicator robotComms;
	private RobotType robotType;
	
	public RobotStrategyState(RobotType robotType) {
		this.robotType = robotType;
	}
	
	public StrategyAction getCurrentAction() {
		return this.strategyAction;
	}

	public void setCommunicator(RobotCommunicator robotComms) {
		this.robotComms = robotComms;
	}
	
	public void addAction(StrategyAction action) {
		actions.add(action);
	}
	
	public void clearActions() {
		this.actions.clear();
	}
	
	public boolean actionsAvailable() {
		return this.actions.size() > 0;
	}
	
	public StrategyAction getBestAction(WorldState state) {
		if(!this.actionsAvailable()) return null;

		possibleActions.clear();
		
		for(StrategyAction action : this.actions) {
			if(action.isPossible(state)) {
				action.updateUtility(state);
				possibleActions.add(action);
			}
		}
		
		if(possibleActions.size() == 0) {
			Log.logError("No possible actions for " + this.robotType.toString());
			return null;
		}
		
		Collections.sort(possibleActions);
		return possibleActions.get(possibleActions.size() - 1);
	}
	
	public void setCurrentAction(StrategyAction action, WorldState state) {
		this.issuedAction = new IssuedAction();
		this.strategyAction = action;
		
		RobotInstruction instruction = action.getInstruction(state);
		instruction.setCallback(issuedAction.getCallback());
		robotComms.sendInstruction(instruction);
	}
	
	public boolean needsNewAction(WorldState state) {
		return issuedAction == null || issuedAction.isCompleted() || !strategyAction.isPossible(state);
	}
}
