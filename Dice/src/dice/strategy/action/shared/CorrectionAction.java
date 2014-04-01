package dice.strategy.action.shared;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.state.WorldState.Side;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

public class CorrectionAction extends StrategyAction {
	
	private boolean faceBackwards = false;
	private boolean shouldRotate;
	private double dist;
	
	public CorrectionAction(RobotType targetRobot) {
		super(targetRobot);
	}
	
	public CorrectionAction(RobotType targetRobot, boolean faceBackwards) {
		super(targetRobot);
		
		this.faceBackwards = faceBackwards;
	}
	
	public boolean isPossible(WorldState state) {
		GameObject target = this.getTargetObject(state);
		
		Vector2 zoneMiddle = state.getCellCenter(target.getCurrentZone());
		//System.out.println(target.getPos().X);

		boolean facingLeft = state.getSide() == Side.RIGHT;
		if(faceBackwards) facingLeft = !facingLeft;
		
		double heading = StratMaths.getAngleRelativeToHorizontal(target, facingLeft);
		double deltaX = zoneMiddle.X - target.getPos().X;

		if(Math.abs(heading) > StratMaths.CORRECTION_ROT_THRESH) {
			shouldRotate = true;
			this.dist = heading;
			
			return true;
		} else if(Math.abs(deltaX) > StratMaths.POSITION_FUZZ) {
			shouldRotate = false;
			
			if(state.getSide() == Side.LEFT) {
				this.dist = deltaX;
			} else {
				this.dist = -deltaX;
			}
			
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
			int moveSpeed = StratMaths.speedForDist(this.dist);
			return RobotInstruction.createMove(this.dist, moveSpeed);
		}
	}
	
	
}
