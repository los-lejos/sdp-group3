package dice.strategy.action.shared;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

public class CorrectionAction extends StrategyAction {
	
	public enum Side {
		OUR,
		OPP,
		EITHER
	}
	
	private Side faceSide = Side.OPP;
	private boolean shouldRotate;
	private double dist;
	private boolean facingLeft;

	public CorrectionAction(RobotType targetRobot, Side faceSide) {
		super(targetRobot);
		
		this.faceSide = faceSide;
	}
	
	public boolean isFacingLeft() {
		return this.facingLeft;
	}
	
	public boolean isPossible(WorldState state) {
		GameObject target = this.getTargetObject(state);
		
		Vector2 zoneMiddle = state.getCellCenter(target.getCurrentZone());

		double heading;

		if(this.faceSide == Side.OPP) {
			facingLeft = state.getSide() == WorldState.Side.RIGHT;
			heading = StratMaths.getAngleRelativeToHorizontal(target, facingLeft);
		} else if(this.faceSide == Side.OUR) {
			facingLeft = state.getSide() == WorldState.Side.LEFT;
			heading = StratMaths.getAngleRelativeToHorizontal(target, facingLeft);
		} else {
			double headingLeft = StratMaths.getAngleRelativeToHorizontal(target, true);
			double headingRight = StratMaths.getAngleRelativeToHorizontal(target, false);
			
			if(Math.abs(headingLeft) < Math.abs(headingRight)) {
				facingLeft = true;
				heading = headingLeft;
			} else {
				facingLeft = false;
				heading = headingRight;
			}
		}

		double deltaX = zoneMiddle.X - target.getPos().X;

		if(Math.abs(heading) > StratMaths.CORRECTION_ROT_THRESH) {
			shouldRotate = true;
			this.dist = heading;
			
			return true;
		} else if(Math.abs(deltaX) > StratMaths.CORRECTION_POS_THRESH) {
			shouldRotate = false;
			this.dist = facingLeft ? -deltaX : deltaX;
			return true;
		}
		
		return false;
	}
	
	/** The utility of this action should be high
	 * when the angle is off by a certain amount (NEEDS_CORRECTION_THRESH)
	 * Otherwise, it is worthless.
	 */
	public int calculateUtility(WorldState state) {
		return 1;
	}
	
	public RobotInstruction getInstruction(WorldState state) {
		if(this.shouldRotate) {
			int rotSpeed = StratMaths.speedForRot(this.dist);
			return RobotInstruction.createRotate(this.dist, rotSpeed);
		} else {
			return RobotInstruction.createMove(this.dist, 80);
		}
	}
	
	
}
