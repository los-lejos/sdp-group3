package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Goal;
import dice.state.Vector2;
import dice.state.WorldState;

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
	public String getActionType(){
		return "BlockAction";
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
