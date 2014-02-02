package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;

/*
 * @author Sam Stern
 */
public class GetBallAction extends StrategyAction {

	public GetBallAction(RobotType target) {
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
