package dice.strategy.action.defender;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StrategyAction;

public class PassAction extends StrategyAction {

	public PassAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return WorldState.BallPossession.OPP_DEFENDER == state.getBallPossession();
	}

	@Override
	protected int calculateUtility(WorldState state) {
		return 2;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		Vector2 attackerPos = state.getOurAttacker().getPos();
		long relativeAttackerAngle = (long) Math.toDegrees(getTargetObject(state).getRotationRelativeTo(attackerPos));
		return RobotInstruction.createShootTo(relativeAttackerAngle);
	}

}
