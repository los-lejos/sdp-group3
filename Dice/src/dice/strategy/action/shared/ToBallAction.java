package dice.strategy.action.shared;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StrategyAction;
import dice.strategy.StratMaths;

/*
 * @author Sam Stern
 * 
 * move to current ball position
 */

public class ToBallAction extends StrategyAction {
	
	private boolean shouldRotate;

	public ToBallAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		GameObject robot = this.getTargetObject(state);
		GameObject ball = state.getBall();
		
		boolean weHaveTheBall = state.getObjectWithBall() == robot;
		boolean ballIsInSameZone = ball.getCurrentZone() == robot.getCurrentZone();
		
		return ball.getPos() != null &&  ballIsInSameZone && !weHaveTheBall;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		double relativeRotation = getTargetObject(state).getRotationRelativeTo(state.getBall());
		this.shouldRotate = Math.abs(relativeRotation) > StratMaths.ROTATION_FINISHED_THRESH;

		return 2;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		Vector2 ballPos = state.getBall().getPos();
		GameObject robot = getTargetObject(state);

		if(this.shouldRotate) {
			return RobotInstruction.createRotate(robot.getRotationRelativeTo(ballPos));
		} else {
			return RobotInstruction.createMove(robot.getEuclidean(ballPos));
		}
	}
}
