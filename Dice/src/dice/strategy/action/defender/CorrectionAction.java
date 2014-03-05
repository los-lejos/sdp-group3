package dice.strategy.action.defender;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Line;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StrategyAction;

public class CorrectionAction extends StrategyAction {
	
	private final double NEEDS_CORRECTION_THRESH = Math.PI / 6.0;
	
	public CorrectionAction(RobotType targetRobot) {
		super(targetRobot);
	}
	
	public boolean isPossible(WorldState state) {
		if (state.getOurDefender().getPos() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	/** The utility of this action should be high
	 * when the angle is off by a certain amount (NEEDS_CORRECTION_THRESH)
	 * Otherwise, it is worthless.
	 */
	public int calculateUtility(WorldState state) {
		boolean ballInDefZone = WorldState.PitchZone.OUR_DEFEND_ZONE == state.getBall().getCurrentZone();
		// this action is higher utility if the angle is off by a lot
		if (Math.abs(getAngleRelativeToHorizontal(state)) > NEEDS_CORRECTION_THRESH)
			if (!ballInDefZone)
				return 2;
			else
				return 0;
		else
			return 0;
	}
	
	public RobotInstruction getInstruction(WorldState state) {
		double angle = getAngleRelativeToHorizontal(state);
		
		return RobotInstruction.createMoveTo(Math.toDegrees(angle), 0.0);
	}
	
	/** Gets the angle relative to a point directly infront of the robot.
	 * 
	 * @param state - The world state
	 * @return the angle in radians
	 */
	private static double getAngleRelativeToHorizontal(WorldState state) {
		GameObject defender = state.getOurDefender();
		
		// get the rotation relative to a point just "infront"
		// of the defender
		double angle;
		double xPos;
		if (state.getSide() == WorldState.Side.LEFT) {
			xPos = defender.getPos().X + 1;
		} else {
			xPos = defender.getPos().X - 1;
		}
			
		angle = defender.getRotationRelativeTo(
				new Vector2(xPos, defender.getPos().Y));
		
		return angle;
	}
}
