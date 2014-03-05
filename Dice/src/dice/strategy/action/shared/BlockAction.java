package dice.strategy.action.shared;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.state.WorldState.PitchZone;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

/*
 * @author Sam Stern
 * 
 * target robot moves between ball position and goal
 */

public class BlockAction extends StrategyAction {
	
	protected double criticalVel; //TODO what is critical velocity?

	public BlockAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		GameObject ball = state.getBall();
		GameObject robot = getTargetObject(state);
		
		boolean ballInAttZone = WorldState.PitchZone.OUR_ATTACK_ZONE == state.getBall().getCurrentZone();
		boolean ballInDefZone = WorldState.PitchZone.OUR_DEFEND_ZONE == state.getBall().getCurrentZone();
		
		if (ball != null && robot != null && robot.getPos() != null && ball.getPos() != null) {
			if (getTargetObject(state) == state.getOurAttacker() && !ballInAttZone) {
				return true;
			} else if (getTargetObject(state) == state.getOurDefender() && !ballInDefZone) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
		/*if (ball != null && robot != null && robot.getPos() != null) {
			PitchZone ballZone = state.getBall().getCurrentZone();

			if ((ballZone == WorldState.PitchZone.OPP_DEFEND_ZONE) ||
				(ballZone == WorldState.PitchZone.OPP_ATTACK_ZONE)) {
					return true;
			}
		}
			
		return false;
		*/
	}

	@Override
	protected int calculateUtility(WorldState state) {
		
		GameObject ball = state.getBall();
		PitchZone ballZone = state.getBall().getCurrentZone();
		GameObject target = getTargetObject(state);

		double distToBallY = Math.abs(target.getPos().Y - ball.getPos().Y);
		boolean distYMoreThanFuzz = distToBallY > StratMaths.POSITION_FUZZ;
		
		if (ball.getVelocity() != null) {
			if (target == state.getOurAttacker() && distYMoreThanFuzz) {
				return 2;
			} else if (ballZone != WorldState.PitchZone.OUR_DEFEND_ZONE) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
		
		/*if (ball.getVelocity() != null) {
			if ((getTargetObject(state) == state.getOurDefender()) && (ballZone == WorldState.PitchZone.OPP_ATTACK_ZONE) 
					&& (Math.abs(state.getBall().getVelocity().Y) < criticalVel)) {
				return 2;
			} else if ((getTargetObject(state) == state.getOurAttacker()) && (ballZone == WorldState.PitchZone.OPP_DEFEND_ZONE)){
				return 1;
			} else {
				return 0;
			}
		} else {
			return 0;
		} */
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		Vector2 ourGoalCenter = state.getOurGoal().getGoalCenter();
		GameObject robot = getTargetObject(state);

		double whereToBlockX = robot.getPos().X;
		double whereToBlockY = StratMaths.getBetweenY(state.getBall(), ourGoalCenter);
		Vector2 whereToBlock = new Vector2 (whereToBlockX, whereToBlockY);
		
		return RobotInstruction.createMoveTo(
				Math.toDegrees(robot.getRotationRelativeTo(whereToBlock)),
				robot.getEuclidean(whereToBlock));
	}

}
