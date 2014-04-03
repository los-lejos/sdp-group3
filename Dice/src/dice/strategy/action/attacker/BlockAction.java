package dice.strategy.action.attacker;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Line;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

public class BlockAction extends StrategyAction {
	
	double movementAmount = 0;
	private boolean isFacingLeft;

	public BlockAction(RobotType targetRobot) {
		super(targetRobot);
	}
	
	public void setFacingLeft(boolean facingLeft) {
		this.isFacingLeft = facingLeft;
	}

	@Override
	public boolean isPossible(WorldState state) {
		GameObject ball = state.getBall();
		GameObject target = this.getTargetObject(state);
		final Vector2 ballPos = ball.getPos();
		final Vector2 targetPos = target.getPos();

		this.movementAmount = Double.MAX_VALUE;

		// if the opponent attacker has the ball, do line projection using
		// the orientation of that robot
		if (state.getObjectWithBall() == state.getOpponentDefender()) {
			// project a line from the rotation of the robot
			GameObject opponentDefender = state.getOpponentDefender();
			Line line = opponentDefender.projectLine();
			
			// Move towards wherever the opponent attacker is looking
			double yAtRobot = line.getYValue(targetPos.X);
			movementAmount = StratMaths.getStrafeDist(targetPos.Y, yAtRobot,
					isFacingLeft);
		}

		// If we haven't decided to do anything smarter, navigate to the ball's y
		if (this.movementAmount == Double.MAX_VALUE) {
			movementAmount = StratMaths.getStrafeDist(targetPos.Y,
					ballPos.Y, isFacingLeft);
		}

		// Don't want to issue lateral movement commands if we're not going to
		// be moving a decent amount
		return Math.abs(movementAmount) > 6;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		return RobotInstruction.createLateralMove(movementAmount);
	}
}
