package dice.strategy;

import java.strategy.StrategyAction;

import dice.communication.RobotInstruction;
import dice.state.WorldState;

public class FaceBallAction extends StrategyAction {
	

	@Override
	public boolean isPossible(WorldState state) {
		return true;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		if (StratMaths.willCollideWithBall()) {
			return 2;
		} else {
			return 0;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		return RobotInstruction.CreateMoveTo(
				//TODO,
				0, 
				this.getCallback());
	}

}
