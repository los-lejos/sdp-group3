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
		
		double heading = defender.getRotationRelativeTo(attacker);
		
		if(heading > StratMaths.ROTATION_FINISHED_THRESH) {
			return RobotInstruction.createRotate(heading);
		} else {
			return RobotInstruction.createKick();
		}
	}
}
