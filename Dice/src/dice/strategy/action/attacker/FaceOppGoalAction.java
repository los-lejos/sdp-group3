package dice.strategy.action.attacker;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

public class FaceOppGoalAction extends StrategyAction {

	public FaceOppGoalAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return WorldState.BallPossession.OUR_ATTACKER == state.getBallPossession();
	}

	@Override
	protected int calculateUtility(WorldState state) {
		return 1;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		Vector2 whereToShoot = StratMaths.whereToShoot(getTargetObject(state), state);
		double relativeRot = Math.toDegrees(getTargetObject(state).getRotationRelativeTo(whereToShoot));
		return RobotInstruction.CreateMoveTo(relativeRot,0);
	}

}
