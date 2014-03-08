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
	protected int calculateUtility(WorldState state) {
		Vector2 goalCenter = state.getOppGoal().getGoalCenter();
		double relativeRotation = getTargetObject(state).getRotationRelativeTo(goalCenter);
		this.shouldRotate = Math.abs(relativeRotation) > StratMaths.ROTATION_FINISHED_THRESH;
		
		return 4;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		Goal opGoal = state.getOppGoal();
		Vector2 opGoalCenter = opGoal.getGoalCenter();
		
		if(this.shouldRotate) {
			GameObject robot = this.getTargetObject(state);
			double heading = robot.getRotationRelativeTo(opGoalCenter);
			
			return RobotInstruction.createRotate(heading);
		} else {
			return RobotInstruction.createKick();
		}
	}
}
