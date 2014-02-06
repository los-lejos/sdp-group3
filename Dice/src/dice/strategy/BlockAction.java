package dice.strategy;

import shared.RobotInstructions;
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
public class BlockAction extends StrategyAction {
	

	private Vector2 whereToBlock;

	public BlockAction(RobotType target) {
		super(target);
	}

	@Override
	public boolean isPossible(WorldState state) {
		GameObject ball = state.getBall();
		whereToBlock = ball.projectLine(ball.getPos());
		if (StratMaths.canBlock(whereToBlock,state.getOurDefender())) {
			return true;
		}
		return false;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		if (isPossible(state)) {
			return 2;
		}
		else {
			return 1;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		return RobotInstruction.CreateMoveTo(
				StratMaths.cartesianToPolarTheta(whereToBlock),
				StratMaths.cartestanToPolarR(whereToBlock),
				this.getTargetRobot(),
				this.getCallback());
	}

}
