package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;

public class BlockAction extends StrategyAction {

	public BlockAction(RobotType targetRobot) {
		super(targetRobot);
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
	public RobotInstruction getInstruction(WorldState state) {
		// TODO Auto-generated method stub
		return null;
	}

}
