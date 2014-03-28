package dice.strategy.action.shared;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

public class ToZoneCenterAction extends StrategyAction {

	public ToZoneCenterAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return true;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		// This is the base action for both robots if nothing else is possible
		return -1;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject robot = this.getTargetObject(state);
		Vector2 zoneCenter = state.getCellCenter(robot.getCurrentZone());
	
		double angle = robot.getRotationRelativeTo(zoneCenter);
		
		if(Math.abs(angle) > StratMaths.getRotationTreshold(robot.getPos(), zoneCenter)) {
			int rotSpeed = StratMaths.speedForRot(angle);
			return RobotInstruction.createRotate(angle, rotSpeed);
		} else {
			double dist = robot.getPos().getEuclidean(zoneCenter);
			int moveSpeed = StratMaths.speedForDist(dist);
			return RobotInstruction.createMove(dist, moveSpeed);
		}
	}
}
