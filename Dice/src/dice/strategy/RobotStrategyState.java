package dice.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dice.Log;
import dice.communication.RobotCommunicator;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;
import dice.strategy.action.attacker.RepositionAction;

/**
 * @author Joris S. Urbaitis
 */

public class RobotStrategyState {
	// Currently assigned action
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
			return null;
		}
		
		Collections.sort(possibleActions);
		return possibleActions.get(possibleActions.size() - 1);
	}
	
	public void setCurrentAction(StrategyAction action, WorldState state) {
		this.strategyAction = action;
		
		if (robotType == RobotType.ATTACKER && action instanceof RepositionAction) {
			RepositionAction reposAction = (RepositionAction) action;
			reposAction.resetRepositionAttempts();
		}
		
		Log.logInfo(this.robotType.toString() + " assigned " + action.getClass().getName());
		
		// Send instruction if we are connected
		if(robotComms.isConnected()) {
			RobotInstruction instruction = action.getInstruction(state);
			robotComms.sendInstruction(instruction);
		}
	}
}
