package dice.strategy.action.attacker;


import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Goal;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

/*
 * @author Sam Stern
 */

public class ShootAction extends StrategyAction {

	private boolean shouldRotate;
	
	public ShootAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return state.getObjectWithBall() == this.getTargetObject(state);
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		Goal opGoal = state.getOppGoal();
		Vector2 opGoalCenter = opGoal.getGoalCenter();
		
		GameObject robot = getTargetObject(state);
		double relativeRotation = robot.getRotationRelativeTo(opGoalCenter);
		this.shouldRotate = Math.abs(relativeRotation) > StratMaths.ROTATION_SHOOT_THRESH;
		
		if(this.shouldRotate) {
			double heading = robot.getRotationRelativeTo(opGoalCenter);
			
			int rotSpeed = StratMaths.speedForRot(heading);
			return RobotInstruction.createRotate(heading, rotSpeed);
		} else {
			return RobotInstruction.createKick();
		}
	}
}
