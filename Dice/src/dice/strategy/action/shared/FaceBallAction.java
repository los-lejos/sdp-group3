package dice.strategy.action.shared;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

public class FaceBallAction extends StrategyAction {
	
	/*
	 * @author Sam Stern
	 */
	

	public FaceBallAction(RobotType targetRobot) {
		super(targetRobot);
	}
	
	@Override
	public String getActionType(){
		return "FaceBallAction";
	}
	
	@Override
	public boolean isPossible(WorldState state) {
		return true;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		if (StratMaths.willCollideWithBall(getTargetObject(state))) {
			return 2;
		} else {
			return 0;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		double rotation = Math.toDegrees(getTargetObject(state).getRotationRelativeTo(state.getBall()));

		return RobotInstruction.CreateMoveTo(
				(long) rotation,
				(byte) 0);
	}

}
