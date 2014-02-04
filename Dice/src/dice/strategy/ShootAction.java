package dice.strategy;

import shared.RobotInstructions;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.Position;
import dice.state.WorldState;

/**
 * @author Sam Stern
 */

public class ShootAction extends StrategyAction {
	
	byte xL,yL,xR,yR,xC,yC; //TODO set -L,-R and -C to be coordinates of opponents left, right and center goal.
	
	public ShootAction(RobotType target) {
		super(target);
	}

	@Override
	public boolean isPossible(WorldState state) {
		if (state.getOurAttacker().getHasBall()) {
			return true;
		} else return false;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		return 2;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		Position shootAt = new Position(KickTargets.shootTarget());
		double shootAtx = shootAt.X;
		double shootAty = shootAt.Y;
		return new RobotInstruction(
			RobotInstructions.KICK_TOWARDS,
			shootAtx,
			shootAty,
			this.getTargetRobot(),
			this.getCallback()
			);
		/*if (state.getOurAttacker().getPos().X<3) { //TODO set threshold for left, right and center
			return new RobotInstruction(
					RobotInstructions.KICK_TOWARDS,
					xL,
					yL,
					this.getTargetRobot(),
					this.getCallback()
					);
		} else if (state.getOurAttacker().getPos().X>9) {
			return new RobotInstruction(
				RobotInstructions.KICK_TOWARDS,
				xR,
				yR,
				this.getTargetRobot(),
				this.getCallback()
				);
		} else {
			return new RobotInstruction(
					RobotInstructions.KICK_TOWARDS,
					xC,
					yC,
					this.getTargetRobot(),
					this.getCallback()
					);*/
		}
	}

}
