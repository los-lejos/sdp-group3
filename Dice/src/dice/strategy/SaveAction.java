package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Goal;
import dice.state.InvalidPathException;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.state.Path;
import dice.state.Line;

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
		
		goalCenter = new Vector2(570, 160);//ourGoal.getGoalCenter();

		whereToBlock = goalCenter;
				
	}
	
	@Override
	public String getActionType(){
		return "SaveAction";
	}
	
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
		//Goal ourGoal = state.getOurGoal();
		
		try {
			GameObject ball = state.getBall();
			Line line = ball.getLineFromVelocity();
			if (line == null)
				System.out.println("Line not initialized.");
			else
				whereToBlock = new Vector2(goalCenter.X, line.getYValue(goalCenter.X));
			//Vector2 result = path.getCoordinateAtX(goalCenter.X);
			//if (result != null) {
				//whereToBlock = result;
				//System.out.println(whereToBlock.Y);
			//}
			
		//} catch (InvalidPathException e) {
		//	System.err.println("Path not long enough yet: " + e.getMessage());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		return 2;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		System.out.println(whereToBlock.Y - state.getOurDefender().getPos().Y);
		return RobotInstruction.CreateLateralMoveTo((byte) Math.round((whereToBlock.Y - state.getOurDefender().getPos().Y) / 10.0));
				//StratMaths.cartesianToPolarTheta(whereToBlock),
				//StratMaths.cartestanToPolarR(whereToBlock));
	}

}
