package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.Vector2;
import dice.state.WorldState;


/*
 * @author Sam Stern
 * 
 * move to current ball position
 */

public class ToBallAction extends StrategyAction {

	public ToBallAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return true;
		/*
		if (((state.getBallZone() == WorldState.PitchZone.OUR_ATTACK_ZONE) && (getTargetObject(state) == state.getOurAttacker())) ||
			(((state.getBallZone() == WorldState.PitchZone.OPP_DEFEND_ZONE) && (getTargetObject(state) == state.getOurDefender())))) {
			return true;
		} else {
			return false;
		}
		*/
	}

	@Override
	protected int calculateUtility(WorldState state) {
		return 2;
//		if ((state.getBallPossession() == WorldState.BallPossession.OUR_ATTACKER) ||
//				(state.getBallPossession() == WorldState.BallPossession.OUR_ATTACKER)) {
//			return 0;
//		} else {
//			return 1;
//		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		Vector2 ballPos = StratMaths.relativePos(this.getTargetObject(state), state.getBall());
		return RobotInstruction.CreateMoveTo(
				StratMaths.cartesianToPolarTheta(ballPos),
				StratMaths.cartestanToPolarR(ballPos));
	}

}
