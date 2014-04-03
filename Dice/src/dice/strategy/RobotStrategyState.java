package dice.strategy;

import shared.RobotInstructions;
import dice.Log;
import dice.communication.RobotCommunicator;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;

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
	
	private boolean kickerOpen = false;
	
	public RobotStrategyState(RobotType robotType) {
		this.robotType = robotType;
	}
	
	public void onKickerClosed() {
		this.kickerOpen = false;
	}
	
	public boolean isKickerOpen() {
		return this.kickerOpen;
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

		// Send instruction if we are connected
		// and if the new instruction is different from the currently
		// assigned one
		RobotInstruction instruction = action.getInstruction(state);
		
		if(instruction != null) {
			boolean newInstruction = this.currentInstruction == null || !this.currentInstruction.equals(instruction);
			
			if(robotComms.isConnected() && newInstruction) {
				if(instruction.getType() == RobotInstructions.OPEN_KICKER) {
					this.kickerOpen = true;
				} else if(instruction.getType() == RobotInstructions.CLOSE_KICKER) {
					this.kickerOpen = false;
				} else if(instruction.getType() == RobotInstructions.KICK) {
					this.kickerOpen = false;
				}
				
				this.currentInstruction = instruction;
				robotComms.sendInstruction(instruction);
			}
		}
	}
}
