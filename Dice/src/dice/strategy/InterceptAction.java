package dice.strategy;

import shared.RobotInstructions;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;

/**
 * @author Joris S. Urbaitis
 */

public class InterceptAction extends StrategyAction {

	private byte x, y;
	
	public InterceptAction(RobotType target) {
		super(target);
	}
	
	public boolean isPossible(WorldState state) {
		return true;
	}
	
	public int calculateUtility(WorldState state) {
		return 0;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		return new RobotInstruction(
				RobotInstructions.MOVE_TO,
				this.x,
				this.y,
				this.getTargetRobot(),
				this.getCallback());
	}
}
