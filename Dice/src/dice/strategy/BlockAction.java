package dice.strategy;

import dice.communication.RobotInstruction;
import dice.state.WorldState;

public class BlockAction extends StrategyAction {

	@Override
	public boolean isPossible(WorldState state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		// TODO Auto-generated method stub
		return null;
	}

}
