package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;

public class RecievePassAction extends StrategyAction {
	
	public RecievePassAction(RobotType target) {
		super(target);
	}
	
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
	public RobotInstruction getInstruction() {
		// TODO Auto-generated method stub
		return null;
	}

}
