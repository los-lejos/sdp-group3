package dice.strategy.action.defender;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.BoundedLine;
import dice.state.GameObject;
import dice.state.Line;
import dice.state.Path;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StratMaths;
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
		GameObject ball = state.getBall();
		GameObject target = this.getTargetObject(state);
		final Vector2 ballVel = ball.getVelocity();
		final Vector2 ballPos = ball.getPos();
		final Vector2 targetPos = target.getPos();
		
		// This is only possible if the ball has been seen
		if (ballPos == null || targetPos == null) {
			return false;
		}

		this.movementAmount = Double.MAX_VALUE;

		// if the opponent attacker has the ball, do line projection using
		// the orientation of that robot
		if (state.getObjectWithBall().equals(state.getOpponentAttacker())) {
			// project a line from the rotation of the robot
			GameObject opponentAttacker = state.getOpponentAttacker();
			Line line = opponentAttacker.projectLine();

			if (opponentAttacker != null && target != null && line != null) {
				// Move towards wherever the opponent attacker is looking
				double yAtRobot = line.getYValue(targetPos.X);
				movementAmount = getMovementAmount(targetPos.Y, yAtRobot,
						state.getSide());
			}
		} else {
			// Try to move to the projected point of the ball, if it is moving
			BoundedLine goalLine = (BoundedLine) state.getOurGoal().getLine();
			Line ballTraj = ball.getTrajectory();

			if (ballVel != null && ballTraj != null && ballVel.getLength() > StratMaths.BALL_SPEED_THRESH) {
				Vector2 intersection = ballTraj.intersect(goalLine);
				if (intersection != null && goalLine.withinBounds(intersection)) {
					double yAtRobot = ballTraj.getYValue(targetPos.X);
					movementAmount = getMovementAmount(targetPos.Y,
							yAtRobot, state.getSide());
				}
			}
		}

		// If we haven't decided to do anything smarter, navigate to the ball's y
		if (this.movementAmount == Double.MAX_VALUE) {
			movementAmount = getMovementAmount(targetPos.Y,
					ballPos.Y, state.getSide());
		}

		// Don't move past the post while blocking since you want to be in front
		// of the goal at all times
		movementAmount = clampMovementDist(movementAmount, state);

		// Don't want to issue lateral movement commands if we're not going to
		// be moving a decent amount
		return Math.abs(movementAmount) > 8;
	}

	private static double getMovementAmount(double ourY, double targetY,
			WorldState.Side side) {
		if (side == WorldState.Side.LEFT) {
			return targetY - ourY;
		} else {
			return ourY - targetY;
		}
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
		final Vector2 targetPos = target.getPos();

		if (state.getSide() == WorldState.Side.LEFT) {
			if (dist > 0) {
				distFromPost = state.getOurGoal().getTopPost().Y
						- targetPos.Y;
			} else {
				distFromPost = state.getOurGoal().getBottomPost().Y
						- targetPos.Y;
			}
		} else {
			if (dist < 0) {
				distFromPost = targetPos.Y
						- state.getOurGoal().getTopPost().Y;
			} else {
				distFromPost = targetPos.Y
						- state.getOurGoal().getBottomPost().Y;
			}
		}

		// Don't move past the post while blocking since you want to be in front
		// of the goal at all times
		if (Math.abs(distFromPost) < Math.abs(dist)) {
			dist = distFromPost;
		}

		return dist;
	}
}
