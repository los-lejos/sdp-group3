package dice.strategy.action.attacker;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StrategyAction;

/*
 * @author Sam Stern
 */

public class ToZoneCenterAction extends StrategyAction {

	Vector2 zoneCenter;
	
	public ToZoneCenterAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return true;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		if ((getTargetObject(state) == state.getOurAttacker()) && !(state.getBall().getCurrentZone() == WorldState.PitchZone.OUR_ATTACK_ZONE)){
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		if (getTargetObject(state) == state.getOurAttacker()) {
			zoneCenter = state.getCellCenter(WorldState.PitchZone.OUR_ATTACK_ZONE);
		} else {
			zoneCenter = state.getCellCenter(WorldState.PitchZone.OUR_DEFEND_ZONE);
		}
		
		long angle = (long) Math.toDegrees(getTargetObject(state).getRotationRelativeTo(zoneCenter));
		byte dist = (byte) getTargetObject(state).getEuclidean(zoneCenter);
		
		return RobotInstruction.CreateMoveTo(angle, dist);
	}

}
