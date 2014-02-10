package dice.strategy;



import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.Vector2;
import dice.state.WorldState;

public class InterceptAction extends StrategyAction {
	
/*
 * @author Sam Stern
 * 
 * extrapolate position of the ball and move to that position
 */

	public InterceptAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return true;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		if (StratMaths.willCollideWithBall(getTargetObject(state))) {
			return 0;
		} else {
			return 2;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		Vector2 whereToIntercept = new Vector2(0,0); // 
		return RobotInstruction.CreateMoveTo(
				StratMaths.cartesianToPolarTheta(whereToIntercept),
				StratMaths.cartestanToPolarR(whereToIntercept));
	}

}
