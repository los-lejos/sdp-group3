package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.Vector2;
import dice.state.WorldState;

public class ToBallAction extends StrategyAction {

/*
 * @author Sam Stern
 * 
 * move to current ball position
 */

	public ToBallAction(RobotType targetRobot) {
		
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		if (((state.getBallZone() == WorldState.PitchZone.OUR_ATTACK_ZONE) && (getTargetObject(state) == state.getOurAttacker())) ||
			(((state.getBallZone() == WorldState.PitchZone.OPP_DEFEND_ZONE) && (getTargetObject(state) == state.getOurDefender())))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected int calculateUtility(WorldState state) {
		if ((state.getBallPossession() == WorldState.BallPossession.OUR_ATTACKER) ||
				(state.getBallPossession() == WorldState.BallPossession.OUR_ATTACKER)) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		Vector2 ballPos = state.getBall().getPos();
		return RobotInstruction.CreateMoveTo(
				StratMaths.cartesianToPolarTheta(ballPos),
				StratMaths.cartestanToPolarR(ballPos));
	}

}
