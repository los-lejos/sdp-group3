package dice.strategy.action.defender;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.WorldState;
import dice.strategy.StrategyAction;

/*
 * @author Sam Stern
 * 
 * extrapolate position of ball and see if its possible to block ball. if yes, then move to block the ball
 */
public class SaveAction extends StrategyAction {

	public SaveAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		// This is only possible if the ball has been seen
		return state.getBall().getPos() != null;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		return 0;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject ball = state.getBall();
		GameObject target = this.getTargetObject(state);

		return RobotInstruction.createLateralMove(ball.getPos().Y - target.getPos().Y);
	}
}
