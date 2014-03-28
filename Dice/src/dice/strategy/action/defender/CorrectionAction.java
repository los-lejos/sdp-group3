package dice.strategy.action.defender;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.state.WorldState.PitchZone;
import dice.state.WorldState.Side;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

public class CorrectionAction extends StrategyAction {
	
	private boolean shouldRotate;
	private double dist;
	
	public CorrectionAction(RobotType targetRobot) {
		super(targetRobot);
	}
	
	public boolean isPossible(WorldState state) {
		GameObject target = this.getTargetObject(state);
		
		Vector2 zoneMiddle = state.getCellCenter(PitchZone.OUR_DEFEND_ZONE);
		
		double heading = this.getAngleRelativeToHorizontal(state);
		double deltaX = zoneMiddle.X - target.getPos().X;
		double rotationThresh = StratMaths.getRotationTreshold(target.getPos(), zoneMiddle);

		if(Math.abs(heading) > rotationThresh) {
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
	
	/** Gets the angle relative to a point directly infront of the robot.
	 * 
	 * @param state - The world state
	 * @return the angle in radians
	 */
	private double getAngleRelativeToHorizontal(WorldState state) {
		GameObject target = this.getTargetObject(state);
		
		// get the rotation relative to a point just "infront"
		// of the defender
		Vector2 pos = target.getPos();
		Vector2 forward = null;
		
		if(state.getSide() == Side.LEFT) {
			forward = new Vector2(pos.X + 1, pos.Y);
		} else {
			forward = new Vector2(pos.X - 1, pos.Y);
		}
		
		return target.getRotationRelativeTo(forward);
	}
}
