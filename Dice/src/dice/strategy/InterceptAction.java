package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;

/*
 * @author Joris S. Urbaitis
 */

public class InterceptAction extends StrategyAction {

	private byte x, y;
	
	public InterceptAction(RobotType target, byte x, byte y) {
		super(target);
		this.x = x;
		this.y = y;
	}
	
	public boolean isPossible(WorldState state) {
		return true;
	}
	
	public int calculateUtility(WorldState state) {
		return 0;
	}

	@Override
	public RobotInstruction getInstruction() {
		return new RobotInstruction(
				RobotInstruction.MOVE_TO,
				this.x,
				this.y,
				this.getTargetRobot(),
				this.getCallback());
	}
}
