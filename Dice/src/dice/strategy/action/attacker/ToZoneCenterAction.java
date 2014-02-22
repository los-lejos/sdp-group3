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
		if (!(state.getBall().getCurrentZone() == WorldState.PitchZone.OPP_ATTACK_ZONE)){
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		zoneCenter = state.getCellCenter(WorldState.PitchZone.OUR_ATTACK_ZONE);
		
		double angle = Math.toDegrees(getTargetObject(state).getRotationRelativeTo(zoneCenter));
		double dist = getTargetObject(state).getEuclidean(zoneCenter);
		
		return RobotInstruction.CreateMoveTo(angle, dist);
	}

}
