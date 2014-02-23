package dice.strategy.action.shared;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;
import dice.strategy.StrategyAction;

/*
 * action for testing. should not be used in match
 */

public class TestAction extends StrategyAction {

	public TestAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return state.getBall().getCurrentZone() != WorldState.PitchZone.OUR_ATTACK_ZONE;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		System.out.println("test strategy");
		return null;
	}

}
