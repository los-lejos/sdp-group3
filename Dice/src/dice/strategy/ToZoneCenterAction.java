package dice.strategy;

import shared.RobotInstructions;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;

/*
 * @author Sam Stern
 */

public class ToZoneCenterAction extends StrategyAction {
	
	byte xD,yD,xA,yA; // TODO make -A and -B the zone centers for the attacking and defending robots
	
	public ToZoneCenterAction(RobotType target) {
		super(target);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return true;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		switch( this.getTargetRobot()) {
		case ATTACKER:
			if (state.getBall().getPos().Y < 5 || state.getBall().getPos().Y > 7) { // TODO change numbers to seperation of zone coordinates	
				return 1;
			} else {
				return 0;
			}
		case DEFENDER:
			if (state.getBall().getPos().Y > 7) {// TODO same as above
				return 1;
			} else {
				return 0;
			}
			return 0;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		switch(this.getTargetRobot()) {
		case ATTACKER:
			return new RobotInstruction(
					RobotInstructions.MOVE_TO,
					xA,
					yA,
					this.getTargetRobot(),
					this.getCallback()
					);
		case DEFENDER:
			return new RobotInstruction(
					RobotInstructions.MOVE_TO,
					xD,
					yD,
					this.getTargetRobot(),
					this.getCallback()
					);
			return new RobotInstruction(
		}
	}

}
