package dice.strategy.action.defender;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.BoundedLine;
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
	
	double movementAmount = 0;

	public SaveAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		// This is only possible if the ball has been seen
		if(state.getBall().getPos() == null) {
			return false;
		}
		
		GameObject ball = state.getBall();
		GameObject target = this.getTargetObject(state);
		Vector2 ballVel = state.getBall().getVelocity();
		
		this.movementAmount = Double.MAX_VALUE;
		
		// Try to move to the projected point of the ball
		BoundedLine goalLine = (BoundedLine)state.getOurGoal().getLine();
		Line ballTraj = ball.getTrajectory();

		if(ballVel != null && ballTraj != null && ballVel.getLength() > 4) {
			Vector2 intersection = ballTraj.intersect(goalLine);
			if(intersection != null && goalLine.withinBounds(intersection)) {
				double yAtRobot = ballTraj.getYValue(target.getPos().X);
				if (state.getSide() == WorldState.Side.LEFT) {
					movementAmount = yAtRobot - target.getPos().Y;
				} else {
					movementAmount = target.getPos().Y - yAtRobot;
				}
			}
		}
		
		if(this.movementAmount == Double.MAX_VALUE) {
			if (state.getSide() == WorldState.Side.LEFT) {
				movementAmount = ball.getPos().Y - target.getPos().Y;
			} else {
				movementAmount = target.getPos().Y - ball.getPos().Y;
			}
		}

		// Don't move past the post while blocking since you want to be in front
		// of the goal at all times
		movementAmount = clampMovementDist(movementAmount, state);

		// Don't want to issue lateral movement commands if we're not going to be moving a decent amount
		return Math.abs(movementAmount) > 8;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		return 0;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		return RobotInstruction.createLateralMove(movementAmount);
	}
	
	private double clampMovementDist(double dist, WorldState state) {
		GameObject target = this.getTargetObject(state);
		double distFromPost;
		
		if (state.getSide() == WorldState.Side.LEFT) {
			if(dist > 0) {
				distFromPost = state.getOurGoal().getTopPost().Y - target.getPos().Y;
			} else {
				distFromPost = state.getOurGoal().getBottomPost().Y - target.getPos().Y;
			}
		} else {
			if(dist < 0) {
				distFromPost = target.getPos().Y - state.getOurGoal().getTopPost().Y;
			} else {
				distFromPost = target.getPos().Y - state.getOurGoal().getBottomPost().Y;
			}
		}
		
		// Don't move past the post while blocking since you want to be in front
		// of the goal at all times
		if(Math.abs(distFromPost) < Math.abs(dist)) {
			dist = distFromPost;
		}
		
		return dist;
	}
}
