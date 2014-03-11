package dice.strategy.action.defender;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

public class PassAction extends StrategyAction {

	public PassAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return state.getObjectWithBall() == state.getOurDefender();
	}

	@Override
	protected int calculateUtility(WorldState state) {
		return 5;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject defender = state.getOurDefender();
		GameObject attacker = state.getOurAttacker();
		
		double headingDefender = defender.getRotationRelativeTo(attacker);
		double headingAttacker = attacker.getRotationRelativeTo(defender);
		
		double defenderThresh = StratMaths.SHOOT_AIM_ADJUSTMENT;
		double attackerThresh = StratMaths.SHOOT_AIM_ADJUSTMENT;
		
		if(headingDefender > defenderThresh ||
		   headingAttacker > attackerThresh) {
			return RobotInstruction.createRotate(headingDefender);
		} else {
			return RobotInstruction.createKick();
		}
	}
}
