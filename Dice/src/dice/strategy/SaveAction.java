package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Goal;
import dice.state.InvalidPathException;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.state.Path;

/*
 * @author Sam Stern
 * 
 * extrapolate position of ball and see if its possible to block ball. if yes, then move to block the ball
 */
public class SaveAction extends StrategyAction {
	private WorldState world;
	private Vector2 whereToBlock;
	private Vector2 goalCenter;

	public SaveAction(RobotType targetRobot) {
		super(targetRobot);
				
	}
	
	@Override
	public boolean isPossible(WorldState state) {
		Vector2 whereToBlock = null;
		GameObject ball = null;
		
		try {
			ball = state.getBall();
			Path path = ball.projectPathFromVelocity(state);
			if (path == null)
				System.err.println("Path not initialized.");
			goalCenter = new Vector2(570, 160);//ourGoal.getGoalCenter();
			Vector2 result = path.getCoordinateAtX(goalCenter.X);
			if (result != null)
				whereToBlock = path.getCoordinateAtX(goalCenter.X);
			System.out.println(whereToBlock.Y);
		} catch (InvalidPathException e) {
			System.err.println("Path not long enough yet: " + e.getMessage());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		if (ball != null && whereToBlock != null)
			return true;
		else
			return false;
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
		System.out.println("Calculating utility.");
		//Goal ourGoal = state.getOurGoal();
		
		
		
		return 2;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		return RobotInstruction.CreateMoveTo(
				StratMaths.cartesianToPolarTheta(whereToBlock),
				StratMaths.cartestanToPolarR(whereToBlock));
	}

}
