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
		return 2;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject ball = state.getBall();
		Vector2 ballPos = ball.getPos();
		GameObject robot = getTargetObject(state);
		Vector2 robotPos = robot.getPos();
		
		double relativeRotation = robot.getRotationRelativeTo(ball);
		this.shouldRotate = Math.abs(relativeRotation) > StratMaths.getRotationTreshold(robotPos, ballPos);

		if(this.shouldRotate) {
			return RobotInstruction.createRotate(relativeRotation);
		} else {
			return RobotInstruction.createMove(robotPos.getEuclidean(ballPos));
		}
	}
}
