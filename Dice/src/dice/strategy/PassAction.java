package dice.strategy;

import org.jfree.util.Log;

import shared.RobotInstructions;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;

/**
 * Action which has our defending robot pass to the attacking robot.
 *
 * @author Andrew Johnston
 * @author Sam Stern
 */

public class PassAction extends StrategyAction {

	public PassAction(RobotType target) {
		super(target);
	}
	
	@Override
	public boolean isPossible(WorldState state) {
		switch (this.getTargetRobot()) {
		case ATTACKER:
			// do we need to pass to the goalie?
			// probably not
			return false;
		case DEFENDER:
			// we need to have the ball
// TODO: getHasBall() no longer works, use state.possession		
//			if (state.getOurDefender().getHasBall() == true) return true;
//			else return false;
			return false;
		default:
			// something went wrong
			Log.debug("The sky is falling");
			return false;
		}
	}

	@Override
	protected int calculateUtility(WorldState state) {
		switch (this.getTargetRobot()) {
		case ATTACKER:
			return 0;
		case DEFENDER:
			// arbitrary
			return 1;
		default:
			return 0;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		
		switch (this.getTargetRobot()) {
		case ATTACKER:
		case DEFENDER:		
		}
		
		
		return null;
	}

}
