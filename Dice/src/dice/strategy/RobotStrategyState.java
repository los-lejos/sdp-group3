package dice.strategy;

import dice.Log;
import dice.communication.RobotCommunicator;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;
import dice.strategy.action.attacker.RepositionAction;

/**
 * @author Joris S. Urbaitis
 */

public abstract class RobotStrategyState {
	// Currently assigned action
	private StrategyAction strategyAction;
	
	// Currently assigned instruction
	private RobotInstruction currentInstruction;

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

	public abstract StrategyAction getBestAction(WorldState state);
	
	public void updateCurrentAction(WorldState state, StrategyAction action) {
		if(action == null) {
			// Nothing is possible, cancel update
			return;
		}
		
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
		
		if(instruction != null) {
			boolean newInstruction = this.currentInstruction == null || !this.currentInstruction.equals(instruction);
			
			if(robotComms.isConnected() && newInstruction) {
				this.currentInstruction = instruction;
				robotComms.sendInstruction(instruction);
			}
		}
	}
}
