package dice.strategy;

import shared.RobotInstructions;
import dice.communication.RobotInstruction;

/*
 * @author Sam Stern
 */
import dice.communication.RobotType;
import dice.state.WorldState;

/*
 * @author Sam Stern
 */

public class ToGoalAction extends StrategyAction {
	
	byte x,y; // TODO set x and y to position of center of our goal


	public ToGoalAction(RobotType target) {
		super(target);
	}
	
	@Override
	public boolean isPossible(WorldState state) {
		return true;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		if (state.getBall().getPos().Y>1.0) { // TODO need position of middle of pitch
			return 2;
		} else return 1;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		return new RobotInstruction(
				RobotInstructions.MOVE_TO,
				this.x,
				this.y,
				(byte) 0,
				// TODO make robot orient towards opponents goal
				this.getTargetRobot(),
				this.getCallback());
	}

}
