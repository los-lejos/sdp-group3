package dice.strategy.action.defender;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Line;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StrategyAction;

public class CorrectionAction extends StrategyAction {
	
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
	
	public int calculateUtility(WorldState state) {
		return 2;
	}
	
	public RobotInstruction getInstruction(WorldState state) {
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
		
		return RobotInstruction.CreateMoveTo(Math.toDegrees(angle), 0.0);
	}
}
