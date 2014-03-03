package dice.strategy.action.shared;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

/*
 * @author Sam Stern
 */

public class FaceBallAction extends StrategyAction {

	public FaceBallAction(RobotType targetRobot) {
		super(targetRobot);
	}
	
	@Override
	public boolean isPossible(WorldState state) {
		boolean ourAttHasBall = WorldState.BallPossession.OUR_ATTACKER == state.getBallPossession();
		boolean ourDefHasBall = WorldState.BallPossession.OUR_DEFENDER == state.getBallPossession();
		
		if (getTargetObject(state).getCurrentZone() == state.getBall().getCurrentZone()) {
			return getTargetObject(state) == state.getOurAttacker() && ourAttHasBall ||
				getTargetObject(state) == state.getOurDefender() && ourDefHasBall;
		} else {
			return false;
		}
	}

	@Override
	protected int calculateUtility(WorldState state) {
		if (StratMaths.willCollideWithBall(getTargetObject(state),state)) {
			return 2;
		} else {
			return 0;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		System.out.println("Face ball.");
		Vector2 ballPos = state.getBall().getPos();
		GameObject robot = getTargetObject(state);

		// -1 because holonomics work with negatives for clockwise
        return  RobotInstruction.createMoveTo(
				Math.toDegrees(robot.getRotationRelativeTo(ballPos)),
				0);
	}
}
