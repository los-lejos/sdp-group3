package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;

/*
 * @author Sam Stern
 * 
 * extrapolate position of ball and see if its possible to block ball. if yes, then move to block the ball
 */
public class InterceptAction extends StrategyAction {
	

	public InterceptAction(RobotType targetRobot) {
		super(targetRobot);
	}

	private Vector2 whereToBlock;


	@Override
	// TODO Move content of this method to calculateUtility
	public boolean isPossible(WorldState state) {
		return true;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		GameObject ball = state.getBall();
//		whereToBlock = new Vector2(x,ball.projectPath().getY(x));
		boolean canReachBall = StratMaths.canReach(whereToBlock, getTargetObject(state));
		if (canReachBall && !StratMaths.willCollideWithBall(getTargetObject(state))) {
			return 2;
		} else if (canReachBall && StratMaths.willCollideWithBall(getTargetObject(state))){
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		return RobotInstruction.CreateMoveTo(
				StratMaths.cartesianToPolarTheta(whereToBlock),
				StratMaths.cartestanToPolarR(whereToBlock),
				this.getCallback());
	}

}
