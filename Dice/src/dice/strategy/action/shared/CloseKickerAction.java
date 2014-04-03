package dice.strategy.action.shared;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;
import dice.strategy.StrategyAction;

public class CloseKickerAction extends StrategyAction {

	public CloseKickerAction(RobotType targetRobot) {
		super(targetRobot);
	}

	public boolean isPossible(WorldState state) {
		return true;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		return RobotInstruction.createCloseKicker();
	}
}
