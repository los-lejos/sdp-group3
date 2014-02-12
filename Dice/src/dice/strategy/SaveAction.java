package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Goal;
import dice.state.InvalidPathException;
import dice.state.Vector2;
import dice.state.WorldState;

/*
 * @author Sam Stern
 * 
 * extrapolate position of ball and see if its possible to block ball. if yes, then move to block the ball
 */
public class SaveAction extends StrategyAction {
	private WorldState world;
	private Goal ourGoal;

	public SaveAction(RobotType targetRobot) {
		super(targetRobot);
				
	}

	private Vector2 whereToBlock;
	
	@Override
	public boolean isPossible(WorldState state) {
		return true;
	}

	@Override
	protected int calculateUtility(WorldState state) {
	/*	GameObject ball = state.getBall();
		try {
			whereToBlock = new Vector2(ourGoalX,ball.projectPath(state).getCoordinateAtX(ourGoalX).Y);
		} catch (InvalidPathException e) {
			e.printStackTrace();
		}
		boolean canReachBall = StratMaths.canReach(whereToBlock, getTargetObject(state));
		if (canReachBall && !StratMaths.willCollideWithBall(getTargetObject(state))) {
			return 2;
		} else if (canReachBall && StratMaths.willCollideWithBall(getTargetObject(state))){
			return 0;
		} else {
			return 1;
		}
		*/
		ourGoal = state.getOurGoal();
		
		GameObject ball = state.getBall();
		try {
			Vector2 goalCenter = ourGoal.getGoalCenter();
			whereToBlock = new Vector2(goalCenter.X,ball.projectPath(state).getCoordinateAtX(goalCenter.X).Y);
		} catch (InvalidPathException e) {
			e.printStackTrace();
		}
		
		return 2;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		return RobotInstruction.CreateMoveTo(
				StratMaths.cartesianToPolarTheta(whereToBlock),
				StratMaths.cartestanToPolarR(whereToBlock));
	}

}
