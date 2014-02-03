package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;

/**
 * Action which receives a pass from the defending robot using PassAction
 * 
 * @see dice.strategy.PassAction
 * @author Andrew Johnston
 * @author Sam Stern
 */

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
	public RobotInstruction getInstruction(WorldState state) {
		// TODO Auto-generated method stub
		return null;
	}

}
