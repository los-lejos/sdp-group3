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
	public boolean isPossible(WorldState state) {
		
		boolean ourAttackerHasBall = state.getBallPossession() == WorldState.BallPossession.OUR_ATTACKER;
		boolean ourDefenderHasBall = state.getBallPossession() == WorldState.BallPossession.OUR_DEFENDER;
		
		boolean targetIsAttacker = getTargetObject(state) == state.getOurAttacker();
		boolean targetIsDefender = getTargetObject(state) == state.getOurDefender();
		
		return !((targetIsAttacker && ourAttackerHasBall) || (targetIsDefender && ourDefenderHasBall)); 
	}

	@Override
	protected int calculateUtility(WorldState state) {
		
		if (StratMaths.willCollideWithBall(getTargetObject(state),state)) {
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
				0);
	}

}
