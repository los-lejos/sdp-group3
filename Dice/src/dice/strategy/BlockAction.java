package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.Goal;
import dice.state.Vector2;
import dice.state.WorldState;

/*
 * @author Sam Stern
 * 
 * target robot moves between ball position and goal
 */

public class BlockAction extends StrategyAction {
	
	protected double criticalVel;
	protected Vector2 whereToMove;
	Goal ourGoal; // TODO

	public BlockAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		if ((state.getBallZone() == WorldState.PitchZone.OPP_DEFEND_ZONE) || (state.getBallZone() == WorldState.PitchZone.OPP_ATTACK_ZONE)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected int calculateUtility(WorldState state) {
	/*	if (((state.getBallPossession() == WorldState.BallPossession.OUR_ATTACKER) && (state.getBallZone() == WorldState.PitchZone.OPP_DEFEND_ZONE)) ||
			((state.getBallPossession() == WorldState.BallPossession.OUR_DEFENDER) && (state.getBallZone() == WorldState.PitchZone.OPP_ATTACK_ZONE) && 
			(Math.abs(state.getBall().getVelocity().Y) < criticalVel))){
				return 2;
			} else {
				return 0;
			}
	*/
		if ((getTargetObject(state) == state.getOurDefender()) && (state.getBallZone() == WorldState.PitchZone.OPP_ATTACK_ZONE) 
				&& (Math.abs(state.getBall().getVelocity().Y) < criticalVel)) {
			return 2;
		} else if ((getTargetObject(state) == state.getOurAttacker()) && (state.getBallZone() == WorldState.PitchZone.OPP_DEFEND_ZONE)){
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		whereToMove.setPos(getTargetObject(state).getPos().X,StratMaths.getBetweenY(state.getBall(), ourGoal.getGoalCenter()));
		return RobotInstruction.CreateMoveTo(
				StratMaths.cartesianToPolarTheta(whereToMove), 
				StratMaths.cartestanToPolarR(whereToMove));
	}

}
