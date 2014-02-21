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
		PitchZone ballZone = state.getBall().getCurrentZone();
		
		if ((ballZone == WorldState.PitchZone.OPP_DEFEND_ZONE) || (ballZone == WorldState.PitchZone.OPP_ATTACK_ZONE)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected int calculateUtility(WorldState state) {
		PitchZone ballZone = state.getBall().getCurrentZone();
		
		if ((getTargetObject(state) == state.getOurDefender()) && (ballZone == WorldState.PitchZone.OPP_ATTACK_ZONE) 
				&& (Math.abs(state.getBall().getVelocity().Y) < criticalVel)) {
			return 2;
		} else if ((getTargetObject(state) == state.getOurAttacker()) && (ballZone == WorldState.PitchZone.OPP_DEFEND_ZONE)){
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		Vector2 ourGoalCenter = state.getOurGoal().getGoalCenter();
		double whereToBlockX = getTargetObject(state).getPos().X;
		double whereToBlockY = StratMaths.getBetweenY(state.getBall(), ourGoalCenter);
		Vector2 whereToBlock = new Vector2 (whereToBlockX, whereToBlockY);
		GameObject robot = getTargetObject(state);
		return RobotInstruction.CreateMoveTo(
				(long) Math.round(Math.toDegrees(robot.getRotationRelativeTo(whereToBlock))),
				(byte) Math.round(10 * robot.getEuclidean(whereToBlock)));
	}

}