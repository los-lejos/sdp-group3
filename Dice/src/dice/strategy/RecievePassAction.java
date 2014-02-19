package dice.strategy;

import dice.communication.RobotInstruction;

import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;


/*
 * @author Sam Stern
 */
public class RecievePassAction extends StrategyAction {

	private Vector2 whereToRecieve;
	
	public RecievePassAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public String getActionType() {
		return "getActionType";
	}

	@Override
	public boolean isPossible(WorldState state) {
		if (state.getBallZone() == WorldState.PitchZone.OUR_DEFEND_ZONE) {
			return true;
		} else {
			return false;
		}
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
					(byte) 0);
		} else {
			Vector2 whereToRecieve = StratMaths.whereToRecievePass(state);
			
			return RobotInstruction.CreateMoveTo(
					(long) Math.toDegrees((getTargetObject(state).getRotationRelativeTo(whereToRecieve))),
					(byte) getTargetObject(state).getEuclidean(whereToRecieve));
		}
	}

}
