package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;

public class FaceBallAction extends StrategyAction {
	

	public FaceBallAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return true;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		if (StratMaths.willCollideWithBall(getTargetObject(state))) {
			return 2;
		} else {
			return 0;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		double rotation = Math.toDegrees(getTargetObject(state).getRotationRelativeTo(state.getBall()));

		return RobotInstruction.CreateMoveTo(
				(long) rotation,
				(byte) 0);
	}

}
