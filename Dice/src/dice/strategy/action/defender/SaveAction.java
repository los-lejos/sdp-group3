package dice.strategy.action.defender;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Line;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StrategyAction;

/*
 * @author Sam Stern
 * 
 * extrapolate position of ball and see if its possible to block ball. if yes, then move to block the ball
 */
public class SaveAction extends StrategyAction {
	
	private Vector2 whereToBlock;
	private Vector2 goalCenter;

	public SaveAction(RobotType targetRobot) {
		super(targetRobot);
		
		goalCenter = new Vector2(10, 160);//ourGoal.getGoalCenter();

		whereToBlock = goalCenter;	
	}

	@Override
	public boolean isPossible(WorldState state) {
		// this is only possible if the ball has been seen
		GameObject ball = state.getBall();
		if (ball == null || state.getOurDefender().getPos() == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected int calculateUtility(WorldState state) {
		GameObject ball = state.getBall();
		Line line = ball.getLineFromVelocity();
		ball.getSpeed();
		
		double yValue;
		if (line != null) {
			yValue = line.getYValue(goalCenter.X);
		} else {
			yValue = -1;
		}
		
		if (ball.getSpeed() > 7 && yValue <= 320 && yValue >= 0)
			whereToBlock = new Vector2(goalCenter.X, line.getYValue(goalCenter.X));
		else
			whereToBlock = new Vector2(goalCenter.X, ball.getPos().Y);

		return 2;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		return RobotInstruction.CreateLateralMoveTo(whereToBlock.Y - state.getOurDefender().getPos().Y);
	}

}
