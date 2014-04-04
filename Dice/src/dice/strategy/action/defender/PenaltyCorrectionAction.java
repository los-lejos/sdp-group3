package dice.strategy.action.defender;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

public class PenaltyCorrectionAction extends StrategyAction {

	private double dist;

	public PenaltyCorrectionAction(RobotType targetRobot) {
		super(targetRobot);
	}

	public boolean isPossible(WorldState state) {
		GameObject target = this.getTargetObject(state);

		boolean facingLeft = state.getSide() == WorldState.Side.RIGHT;
		double heading = StratMaths.getAngleRelativeToHorizontal(target, facingLeft);

		if(Math.abs(heading) > StratMaths.CORRECTION_ROT_THRESH) {
			this.dist = heading;
			
			return true;
		}
		
		return false;
	}

	public RobotInstruction getInstruction(WorldState state) {
		int rotSpeed = StratMaths.speedForRot(this.dist);
		return RobotInstruction.createRotate(this.dist, rotSpeed);
	}
}
