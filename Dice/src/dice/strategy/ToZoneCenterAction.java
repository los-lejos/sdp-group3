package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;

/*
 * @author Sam Stern
 */

public class ToZoneCenterAction extends StrategyAction {

	Vector2 zoneCenter;
	
	public ToZoneCenterAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public String getActionType() {
		return "ToZoneCenterAction";
	}

	@Override
	public boolean isPossible(WorldState state) {
		return true;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		if (getTargetObject(state) == state.getOurAttacker()) {
			zoneCenter = state.getCellCenter(WorldState.PitchZone.OUR_ATTACK_ZONE);
		} else {
			zoneCenter = state.getCellCenter(WorldState.PitchZone.OUR_DEFEND_ZONE);
		}
		
		long angle = (long) GameObject.asDegrees(getTargetObject(state).getRotationRelativeTo(zoneCenter));
		byte dist = (byte) getTargetObject(state).getEuclidean(zoneCenter);
		
		return RobotInstruction.CreateMoveTo(angle, dist);
	}

}
