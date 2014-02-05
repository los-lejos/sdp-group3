package dice.strategy;

import shared.RobotInstructions;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.WorldState;

/*
 * @author Sam Stern
 * 
 * extrapolate position of ball and see if its possible to block ball. if yes, then move to block the ball
 */
public class BlockAction extends StrategyAction {
	
	private double projectedPos;
	private byte thetaA,thetaB, r;

	public BlockAction(RobotType target) {
		super(target);
	}

	@Override
	public boolean isPossible(WorldState state) {
		//TODO need path extrapolation from craig.
		return true;
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
		return new RobotInstruction(
				RobotInstructions.MOVE_TO,
				thetaA,
				thetaB,
				r,
				this.getTargetRobot(),
				this.getCallback());
	}

}
