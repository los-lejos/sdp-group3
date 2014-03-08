package dice.strategy.action.attacker;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.WorldState;
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
		return state.getObjectWithBall() == state.getOurDefender();
	}

	@Override
	protected int calculateUtility(WorldState state) {
		return 5;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject attacker = state.getOurAttacker();
		GameObject defender = state.getOurDefender();
		
		double heading = attacker.getRotationRelativeTo(defender);
		
		return RobotInstruction.createRotate(heading);
	}

}
