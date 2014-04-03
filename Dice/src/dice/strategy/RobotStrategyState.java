package dice.strategy;

import java.util.Arrays;

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
	
	private int STRAFE_TIMEOUT = 2000;
	private long strafeStartTime;
	private boolean isStrafing = false;
	
	public RobotStrategyState(RobotType robotType) {
		this.robotType = robotType;
	}
	
	public boolean isStrafing() {
		return this.isStrafing;
	}
	
	public void setIsStrafing(boolean isStrafing) {
		this.isStrafing = isStrafing;
		
		if(this.isStrafing) {
			this.strafeStartTime = System.currentTimeMillis();
		}
		
		if(this.robotType == RobotType.ATTACKER) {
			System.out.println(isStrafing);
		}
	}
	
	public StrategyAction getCurrentAction() {
		return this.strategyAction;
	}

	public void setCommunicator(RobotCommunicator robotComms) {
		this.robotComms = robotComms;
	}
	
	public void update() {
		if(this.isStrafing && System.currentTimeMillis() - this.strafeStartTime > STRAFE_TIMEOUT) {
			this.isStrafing = false;
		}
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
				if(instruction.getType() == RobotInstructions.LAT_MOVE) {
					this.setIsStrafing(true);
				}
				
				this.currentInstruction = instruction;
				robotComms.sendInstruction(instruction);
			}
		}
	}
}
