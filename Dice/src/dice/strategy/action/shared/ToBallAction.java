package dice.strategy.action.shared;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StrategyAction;


/*
 * @author Sam Stern
 * 
 * move to current ball position
 */

public class ToBallAction extends StrategyAction {

	public ToBallAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		if (getTargetObject(state).getCurrentZone() == state.getBall().getCurrentZone()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected int calculateUtility(WorldState state) {
		return 2;
//		if ((state.getBallPossession() == WorldState.BallPossession.OUR_ATTACKER) ||
//				(state.getBallPossession() == WorldState.BallPossession.OUR_ATTACKER)) {
//			return 0;
//		} else {
//			return 1;
//		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		Vector2 ballPos = state.getBall().getPos();
        GameObject robot = getTargetObject(state);

        System.out.println("Ball pos: " + ballPos.X + "," + ballPos.Y);
        System.out.println("Robot pos: " + robot.getPos().X + "," + robot.getPos().Y);
        System.out.println(Math.toDegrees(robot.getRotationRelativeTo(ballPos)));
		return  RobotInstruction.CreateMoveTo(
				(long) Math.round(Math.toDegrees(robot.getRotationRelativeTo(ballPos))),
				Math.round(robot.getEuclidean(ballPos)));
	}
}
