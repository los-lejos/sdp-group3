package dice.strategy;

import java.util.ArrayList;
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
	
	// Currently assigned instruction
	private RobotInstruction currentInstruction;
	
	// List of actions the robot can perform
	private List<StrategyAction> actions = new ArrayList<StrategyAction>();
	
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
		for(StrategyAction action : this.actions) {
			if(action.isPossible(state)) {
				return action;
			}
		}
		
		return null;
	}
	
	public void setCurrentAction(StrategyAction action, WorldState state) {
		// If we are assigning a new action, print info
		if(this.strategyAction == null || action.getClass() != this.strategyAction.getClass()) {
			Log.logInfo(this.robotType.toString() + " assigned " + action.getClass().getName());
		}
		
		this.strategyAction = action;
		
		if (robotType == RobotType.ATTACKER && action instanceof RepositionAction) {
			RepositionAction reposAction = (RepositionAction) action;
			reposAction.resetRepositionAttempts();
		}

		// Send instruction if we are connected
		// and if the new instruction is different from the currently
		// assigned one
		RobotInstruction instruction = action.getInstruction(state);
		boolean newInstruction = this.currentInstruction == null || !this.currentInstruction.equals(instruction);
		
		if(robotComms.isConnected() && newInstruction) {
			this.currentInstruction = instruction;
			robotComms.sendInstruction(instruction);
		}
	}
}
