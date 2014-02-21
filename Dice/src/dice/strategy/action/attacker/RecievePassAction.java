package dice.strategy.action.attacker;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;


/*
 * @author Sam Stern
 */
public class RecievePassAction extends StrategyAction {

	public RecievePassAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return state.getBall().getCurrentZone() == WorldState.PitchZone.OUR_DEFEND_ZONE;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		return 2;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		// if target is already in a good position then rotate to the ball otherwise move to a better position
		if (getTargetObject(state).getPos().equals(StratMaths.whereToRecievePass(state))) {
			return RobotInstruction.CreateMoveTo(
					(long) Math.toDegrees(getTargetObject(state).getRotationRelativeTo(state.getBall())),
					0);
		} else {
			Vector2 whereToRecieve = StratMaths.whereToRecievePass(state);
			
			return RobotInstruction.CreateMoveTo(
					(long) Math.toDegrees((getTargetObject(state).getRotationRelativeTo(whereToRecieve))),
					getTargetObject(state).getEuclidean(whereToRecieve));
		}
	}

}
